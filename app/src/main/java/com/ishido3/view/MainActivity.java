package com.ishido3.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ishido3.R;
import com.ishido3.model.Board;
import com.ishido3.model.Deck;
import com.ishido3.model.FileAccess;
import com.ishido3.model.Player;
import com.ishido3.model.TableCoordinates;
import com.ishido3.model.TileInfo;
import com.ishido3.model.TileNode;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int DEFAULT_COLOR = Color.parseColor("#FCEBB6");
    private final String MESSAGE = "score";
    public static final int COMPUTER = 0;
    public static final int HUMAN = 1;
    private FileAccess fileAccess;
    private Board board= new Board();
    private Deck deck = new Deck();
    private Player humanPlayer = new Player();
    private Player computerPlayer = new Player();
    private ArrayList<TileInfo> stock = new ArrayList<TileInfo>();
    private int turn = HUMAN;
    private boolean hintPressed = false;
    private boolean humanEnabled = false;

    private int stockIndex =0;
    private int nextIndex =0;

    private TextView animatedBox = null;
    private Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializes the file access according to this context
        fileAccess = new FileAccess(getApplicationContext());

        // Reads the given file and stores the strings of board data, stock and score
        String gameType = getIntent().getStringExtra(StartPageActivity.MESSAGE_GAME);

        if (gameType.equals("new")) {
            // Get the turn from the head or tail game from user
            String temp = getIntent().getStringExtra(StartPageActivity.MESSAGE_TURN);
            if (temp.equals("human")) {
                turn = HUMAN;
            }
            else turn = COMPUTER;

            // populate the stock
            while (!deck.isDone()) {
                TileInfo tempTile = new TileInfo();
                if (deck.generateTile(tempTile)) {
                    deck.recordTile(tempTile.getNumericColorVal(), tempTile.getNumericSymbolVal());
                    stock.add(tempTile);
                }
            }
        }
        else {
            String file = getIntent().getStringExtra(StartPageActivity.MESSAGE_FILENAME);

            fileAccess.readData(file);

            // Fills the board initially with the data retrieved from the file
            board.fillBoard(fileAccess.getBoardData(), deck);

            // Add to the score model of the player
            humanPlayer.addScore(fileAccess.getHumanScore());
            computerPlayer.addScore(fileAccess.getComputerScore());

            // Get the stock from File
            stock = fileAccess.getStock();

            // Get the turn in the game
            turn = fileAccess.getNextPlayer();
        }

        // Makes the table
        makeTable();

        // Get the view for human and computer initial scores
        TextView humanPlayerView = (TextView) findViewById(R.id.playerScore);
        TextView computerPlayerView = (TextView) findViewById(R.id.computerScore);

        // Set the text in the view for the scores
        humanPlayerView.setText("" + humanPlayer.getScore());
        computerPlayerView.setText(""+computerPlayer.getScore());

        // Gives the user what the current tile is
        updateTileView();

        // Helps blink the tile
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
    }

    // Updates the current tile to be placed
    public void updateTileView() {
        TextView cTileSymbol = (TextView) findViewById(R.id.resultSymbol);
        cTileSymbol.setText(stock.get(stockIndex).getSymbol());
        cTileSymbol.setBackgroundColor(stock.get(stockIndex).getColor());
        nextIndex = stockIndex;
    }

    /**
     * Handles the onClickListener for the game board and performs the operation such as updating the table, player score, game deck
     */
	public View.OnClickListener calculatePosition = new View.OnClickListener() {
		public void onClick(View v) {
            // Retrieves the position (row/column numbers) for the clicked cell in the table
            TableCoordinates clickPosition = (TableCoordinates) v.getTag();

            if (turn == HUMAN) {
                if (board.isTileAvailable(clickPosition.getRow(), clickPosition.getColumn())) {
                    // Now, if the tile is available, try to fill it with the tile in the stock for human
                    if (board.canFillTile(clickPosition.getRow(), clickPosition.getColumn(), stock.get(stockIndex))) {
                        // Temporarily stores the values so that we don't have to repeat same thing over and over
                        TileInfo tempTile = stock.get(stockIndex);

                        if (animatedBox != null) {
                            animatedBox.clearAnimation();
                        }

                        if (humanEnabled) {
                            animatedBox.setText("");
                            animatedBox.setBackgroundColor(DEFAULT_COLOR);
                            humanEnabled = false;
                        }

                        TextView box = (TextView) v;
                        box.setText(tempTile.getSymbol());
                        box.setBackgroundColor(tempTile.getColor());

                        // Records the generated/selected tile in the deck so that there is more more than 2 repetitions of certain combination
                        deck.recordTile(tempTile.getNumericColorVal(), tempTile.getNumericSymbolVal());
                        board.fillTile(clickPosition.getRow(),clickPosition.getColumn(), tempTile);

                        // Generates and adds the score of the player
                        humanPlayer.addScore(board.calculateScore(clickPosition.getRow(), clickPosition.getColumn(), tempTile));

                        // Prints and updates the player score
                        TextView playerScore = (TextView) findViewById(R.id.playerScore);
                        playerScore.setText("" + humanPlayer.getScore());

                        // Now change the turn
                        turn = COMPUTER;

                        // Increase the stock index for next user
                        stockIndex++;

                        // Updates the view of the tile
                        updateTileView();

                    } else {
                        Toast.makeText(getApplicationContext(), "Illegal move", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "The position is already filled", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Slow down! not your turn.. GOSH!!!", Toast.LENGTH_SHORT).show();
            }
        }
	};

    public void hint(View view) {
        if (turn == HUMAN) {
            hintPressed = true;
            playComputer(view);
            hintPressed = false;
        }
        else {
            Toast.makeText(getApplicationContext(), "Not your turn yet.", Toast.LENGTH_SHORT).show();
        }
    }

    // This is for computer's turn. When this button is clicked, then the algorithm runs to bring out the best node for the PC
    public void playComputer(View view) {
        if (animatedBox != null) {
            animatedBox.clearAnimation();
        }

        if(turn == HUMAN && hintPressed == false) {
            Toast.makeText(getApplicationContext(), "It is your turn, not computer's.", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText plyView = (EditText) findViewById(R.id.cutoffVal);
        String tempValue = (plyView.getText().toString());

        CheckBox isAB = (CheckBox) findViewById(R.id.alphabeta);
        if (isAB.isChecked()) {
            board.enableGodMode();
        }
        else {
            board.disableGodMode();
        }

        int cutOffValue =0;
        if (!tempValue.isEmpty()) {
            cutOffValue = Integer.parseInt(tempValue);
        }

        // Starts the algorithm
        long startTime = System.currentTimeMillis();

        // Checks if the algorithm should be run as human or computer. If human, then pass the value to board.
        if (turn == HUMAN) {
            board.setHumanUsingAlgo(true);
        }
        else {
            board.setHumanUsingAlgo(false);
        }

        TileNode answerNode = board.startAlgorithm(stock, stockIndex, humanPlayer.getScore(), computerPlayer.getScore(), cutOffValue);

        long endTime = System.currentTimeMillis();
        Toast.makeText(getApplicationContext(), "" + (endTime - startTime),Toast.LENGTH_SHORT).show();
        System.out.println("" + (endTime-startTime));

        if (answerNode.getCoordinates() == null) {
            Toast.makeText(getApplicationContext(),"Game over",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            System.out.println("The answer is " + answerNode.getCoordinates().getRow() + "   " + answerNode.getCoordinates().getColumn());
            System.out.println("Heuristic is " + answerNode.getHeuristicVal());
        }

        // After the TileNode is got from the algorithm, calculate the score according to the result and place it on the board.
        // For now, it is solely computer for algorithm
        int tempScore = board.calculateScore(answerNode.getCoordinates().getRow(), answerNode.getCoordinates().getColumn(), answerNode.getTile());
        computerPlayer.addScore(tempScore);

        TextView CPView = (TextView) findViewById(R.id.computerScore);

        CPView.setText("" + computerPlayer.getScore());

        TextView box = (TextView) findViewInTable(answerNode.getCoordinates());
        box.setText(answerNode.getTile().getSymbol());
        box.setBackgroundColor(answerNode.getTile().getColor());

        box.startAnimation(anim);
        animatedBox = box;

        if (turn != HUMAN) {
            // Now put it on the board
            board.fillTile(answerNode.getCoordinates().getRow(), answerNode.getCoordinates().getColumn(), answerNode.getTile());
            stockIndex++;
            turn = HUMAN;
        }
        else {
            humanEnabled = true;
        }



        updateTileView();
    }

    /**
     * Finds the cell view in the TableLayout since it cannot be directly found
     *
     * @param inputCoordinates It is the given coordinates for the cell
     * @return
     */
    private TextView findViewInTable(TableCoordinates inputCoordinates) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.givenGrid);

        // Loops through each cell view and checks if it matches with the coordinates with the view tag
        for (int rows = 0; rows < tableLayout.getChildCount(); ++rows) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(rows);

            for (int col = 0; col < tableRow.getChildCount(); ++col) {
                TextView box = (TextView) tableRow.getChildAt(col);
                TableCoordinates boxCoordinates = (TableCoordinates) box.getTag();
                if (boxCoordinates.getRow() == inputCoordinates.getRow() && boxCoordinates.getColumn() == inputCoordinates.getColumn()) {
                    return box;
                }
            }
        }
        return null;

    }


    // Saves the game into a file. Basically calls the function in the fileAccess
    public void saveGame(View v) {
        try {
            fileAccess.save(board, stock, stockIndex, humanPlayer,computerPlayer,turn);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.finish();
    }

    // Gets the next tile view for the purpose of view only
    public void getNext(View view) {
        nextIndex = nextIndex+1;
        TextView cTileSymbol = (TextView) findViewById(R.id.resultSymbol);
        cTileSymbol.setText(stock.get(nextIndex).getSymbol());
        cTileSymbol.setBackgroundColor(stock.get(nextIndex).getColor());
    }


    /**
     * Makes the table in the board
     */
    public void makeTable() {
        // Finds the table that we will be working with
        TableLayout table = (TableLayout) findViewById(R.id.givenGrid);

        // Adds the grid in the android activity
        // It uses the TableLayout to create the overall table of the board
        for (int rowIndex = 0; rowIndex < 8; ++rowIndex) {
            TableRow row = new TableRow(this);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 4, 4, 4);
            for (int columnIndex = 0; columnIndex < 12; ++columnIndex) {
                // Specifies each cell of the table with TextView
                TextView columns = new TextView(this);
                columns.setWidth(50);
                columns.setHeight(35);
                columns.setTextSize(20);

                TileInfo boardTile = board.getTile(rowIndex, columnIndex);
                if (boardTile == null) {
                    columns.setText("");
                    columns.setBackgroundColor(DEFAULT_COLOR);
                } else {
                    columns.setText(boardTile.getSymbol());
                    columns.setBackgroundColor(boardTile.getColor());
                }
                columns.setGravity(Gravity.CENTER);

                // Sets the tag for each cell view so that we can retrieve the row and column number clicked
                TableCoordinates tableCoordinates = new TableCoordinates(rowIndex, columnIndex);
                columns.setTag(tableCoordinates);

                columns.setOnClickListener(calculatePosition);
                row.addView(columns, params);
            }
            table.addView(row);
        }

        // Displays the row numbers of the board from this table
        TableLayout rowIndexing = (TableLayout) findViewById(R.id.rowIndexing);

        // Adds the grid in the android activity
        // It uses the TableLayout to create the overall table of the board
        for (int rowIndex = 0; rowIndex < 9; ++rowIndex) {
            TableRow row = new TableRow(this);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 4, 4, 4);

            TextView rowView = new TextView(this);
            rowView.setText(rowIndex + "");
            rowView.setWidth(50);
            rowView.setHeight(35);
            rowView.setGravity(Gravity.CENTER);

            row.addView(rowView, params);
            rowIndexing.addView(row);
        }

        // Displays the column numbers of the board
        TableLayout colIndexing = (TableLayout) findViewById(R.id.columnIndexing);

        TableRow row = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 4, 4, 4);

        for (int col = 0; col < 12; col++) {
            TextView colView = new TextView(this);
            colView.setText(col + 1 + "");
            colView.setWidth(50);
            colView.setHeight(35);
            colView.setGravity(Gravity.CENTER);
            row.addView(colView, params);
        }
        colIndexing.addView(row);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
