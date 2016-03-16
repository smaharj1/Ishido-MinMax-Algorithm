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

public class StartPageActivity extends AppCompatActivity {
    public static final String MESSAGE_TURN = "turn";
    public static final String MESSAGE_GAME = "game";
    public static final String MESSAGE_FILENAME = "filename";
    private final int HEAD = 0;
    private final int TAIL = 0;
//    private final int HUMAN = 1;
//    private final int COMPUTER =0;

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

    public int tossCoin() {

        //Toast.makeText(getApplicationContext(), "Tossing the coin", Toast.LENGTH_LONG).show();

        Random rand = new Random();
        return rand.nextInt(2);

    }

    public boolean isStartGame() {
        return startGame;
    }

    // Plays heads or tail game and checks with users input. If same, then returns true, else false. meaning user lost or won.
    public boolean headTail(int userInput) {
        int result = tossCoin();
        System.out.println("Tossed the coin");
        if (result == userInput) {
            return true;
        }
        else {
            return false;
        }
    }


    public void playGame(View view) {


        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Head or tail choice");
        alertDialog.setMessage("Please select heads or tails");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Head",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startGame = true;

                        //dialog.dismiss();

                        AlertDialog newDialog = new AlertDialog.Builder(alertDialog.getContext()).create();

                        int userChoice = HEAD;

                        // Checks if the userChoice and random head/tail are same. If yes, then user won.
                        if (headTail(userChoice)) {

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
                        //dialog.dismiss();

                    }
                });
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

    public void loadGame(View view) {
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

                intent.putExtra(MESSAGE_GAME, "load");

                intent.putExtra(MESSAGE_FILENAME, filename);

                startActivity(intent);


            }
        });


        builder.create().show();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        // Puts in as new game. So, load it from the raw folder
        intent.putExtra(MESSAGE_GAME, "load");

        //dialog.dismiss();
        //if (startGame) startActivity(intent);
    }
}
