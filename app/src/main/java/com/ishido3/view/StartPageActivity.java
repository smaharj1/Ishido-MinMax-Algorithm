/************************************************************
 * Name:  Sujil Maharjan                                    *
 * Project:  Project 3/Ishido Game			               *
 * Class:  Artificial Intelligence/CMP 331                  *
 * Date:  3/22/2016			                               *
 ************************************************************/
package com.ishido3.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.ishido3.R;

import java.util.Random;

/**
 * This is the first page that we see in the game. There is the option of start the new game or load the game
 */
public class StartPageActivity extends AppCompatActivity {
    // Constants in the class to pass the vales to other classes
    public static final String MESSAGE_TURN = "turn";
    public static final String MESSAGE_GAME = "game";
    public static final String MESSAGE_FILENAME = "filename";

    // Holds the values for head and tail
    private final int HEAD = 0;
    private final int TAIL = 1;

    // Initializes if the startGame is new or not
    private boolean startGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_page, menu);
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

    /**
     * Handles the coin toss. It generates the random value
     * @return Returns if it is head or tail
     */
    public int tossCoin() {
        Random rand = new Random();
        return rand.nextInt(2);

    }

    /**
     * Returns if startGame is true
     * @return Returns if startGame is true
     */
    public boolean isStartGame() {
        return startGame;
    }

    /**
     * Plays heads or tail game and checks with users input. If same, then returns true, else false. meaning user lost or won.
     * @param userInput Holds the value that user put in
     * @return Returns if the user is right
     */
    public boolean headTail(int userInput) {
        // Computes the coin toss
        int result = tossCoin();

        // If the user input and the coin toss result is the same, user won. Else, user lost
        if (result == userInput) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Plays the game once the play game button is pressed
     * @param view
     */
    public void playGame(View view) {
        // Creates the alert dialog to allow user to choose heads or tails
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Head or tail choice");
        alertDialog.setMessage("Please select heads or tails");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Head",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startGame = true;

                        // Creates another dialog for the result
                        AlertDialog newDialog = new AlertDialog.Builder(alertDialog.getContext()).create();

                        int userChoice = HEAD;

                        // Checks if the userChoice and random head/tail are same. If yes, then user won.
                        if (headTail(userChoice)) {
                            // Displays the victory dialog and give option to start the actual game
                            newDialog.setMessage("Congratulations! Head it is. Your turn first!");
                            newDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialogInterface, int whi) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.putExtra(MESSAGE_TURN, "human");
                                            intent.putExtra(MESSAGE_GAME, "new");
                                            startActivity(intent);
                                        }
                                    });
                        }
                        else {
                            newDialog.setMessage("Sorry! Tail rolled. Computer goes first");
                            newDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialogInterface, int whi) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.putExtra(MESSAGE_TURN, "computer");
                                            intent.putExtra(MESSAGE_GAME, "new");
                                            startActivity(intent);
                                        }
                                    });
                        }

                        newDialog.show();
                    }
                });
        // Compute for tail pressed
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Tail",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startGame = true;

                        AlertDialog newDialog = new AlertDialog.Builder(alertDialog.getContext()).create();

                        int userChoice = TAIL;

                        // Checks if the userChoice and random head/tail are same. If yes, then user won.
                        if (headTail(userChoice)) {
                            newDialog.setMessage("Congratulations! Tail it is. Your turn first!");
                            newDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialogInterface, int whi) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.putExtra(MESSAGE_TURN, "human");
                                            intent.putExtra(MESSAGE_GAME, "new");
                                            startActivity(intent);
                                        }
                                    });
                        }
                        else {
                            newDialog.setMessage("Sorry! Head rolled. Computer goes first");
                            newDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialogInterface, int whi) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.putExtra(MESSAGE_TURN, "computer");
                                            intent.putExtra(MESSAGE_GAME, "new");
                                            startActivity(intent);
                                        }
                                    });
                        }
                        newDialog.show();
                    }
                }
                );
        alertDialog.show();
    }

    /**
     * Loads if load game button is pressed.
     * @param view
     */
    public void loadGame(View view) {
        // Creates the dialog box for allowing user to select which file to open. User does not have to input the extension
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.check, null));
        builder.setMessage("Please enter the required info");
        builder.setNeutralButton("NEXT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dialog d = (Dialog) dialog;

                EditText file = (EditText) d.findViewById(R.id.filename);
                String filename = file.getText().toString();


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                // Puts the extra message in the intent for another activity to computer likewise
                intent.putExtra(MESSAGE_GAME, "load");

                intent.putExtra(MESSAGE_FILENAME, filename);

                startActivity(intent);


            }
        });


        builder.create().show();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        // Puts in as new game. So, load it from the raw folder
        intent.putExtra(MESSAGE_GAME, "load");

    }
}
