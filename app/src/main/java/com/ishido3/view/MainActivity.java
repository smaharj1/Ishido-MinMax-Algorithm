package com.ishido3.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ishido3.R;
import com.ishido3.model.Board;
import com.ishido3.model.Deck;
import com.ishido3.model.FileAccess;
import com.ishido3.model.Player;
import com.ishido3.model.TableCoordinates;
import com.ishido3.model.TileInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int DEFAULT_COLOR = Color.parseColor("#FCEBB6");
    private final String NEW_GAME = "new";
    private final String LOAD_GAME = "load";
    public static final int COMPUTER = 0;
    public static final int HUMAN = 1;
    private FileAccess fileAccess;
    private Board board= new Board();
    private Deck deck = new Deck();
    private Player humanPlayer = new Player();
    private Player computerPlayer = new Player();
    private ArrayList<TileInfo> stock = new ArrayList<TileInfo>();
    private int turn = HUMAN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializes the file access according to this context
        fileAccess = new FileAccess(getApplicationContext());

        // Reads the given file and stores the strings of board data, stock and score
        String gameType = getIntent().getStringExtra(StartPageActivity.MESSAGE_GAME);
        String file = getIntent().getStringExtra(StartPageActivity.MESSAGE_FILENAME);
        System.out.println(gameType );

        // Check if the game type is new or needs to be loaded
        // If it is new, then load the original available grid from raw resource folder
        if (gameType.equals("new")) {
            System.out.println("Starting the new game");
            fileAccess.readData(R.raw.newgame,"",FileAccess.RAW);
        }
        else {
            // do something else to load from the user saved file
            fileAccess.readData(0,file, FileAccess.FILE);
            System.out.println("not a new game type");

        }

        // Fills the board initially with the data retrieved from the file
        board.fillBoard(fileAccess.getBoardData(), deck);

        // Makes the table
        makeTable();

        // Get the view for human and computer initial scores
        TextView humanPlayerView = (TextView) findViewById(R.id.playerScore);
        TextView computerPlayerView = (TextView) findViewById(R.id.computerScore);

        // Add to the score model of the player
        humanPlayer.addScore(fileAccess.getHumanScore());
        computerPlayer.addScore(fileAccess.getComputerScore());

        // Set the text in the view for the scores
        humanPlayerView.setText("" + humanPlayer.getScore());
        computerPlayerView.setText(""+computerPlayer.getScore());

        // Get the stock from File
        stock = fileAccess.getStock();

        // Get the turn in the game
        turn = fileAccess.getNextPlayer();

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

                //columns.setOnClickListener(calculatePosition);
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
