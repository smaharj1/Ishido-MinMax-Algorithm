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
import android.widget.Toast;

import com.ishido3.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class StartPageActivity extends AppCompatActivity {
    public static final String MESSAGE_TURN = "turn";
    public static final String MESSAGE_GAME = "game";
    public static final String MESSAGE_FILENAME = "filename";
    private final int HUMAN = 1;
    private final int COMPUTER =0;

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

    public void playGame(View view) {


        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("The coin is tossed");
        alertDialog.setMessage("Alert message to be shown");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startGame = true;

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        // Ask for human to do the coin toss for choosing the first player and send the message via extra message
                        int result = HUMAN;

                        // do the coin toss
                        result = tossCoin();
                        System.out.println("Coin tossed is" + result);

                        // Puts into intent as for whose turn it is
                        intent.putExtra(MESSAGE_TURN, result);
                        intent.putExtra(MESSAGE_FILENAME, "");

                        // Puts in as new game. So, load it from the raw folder
                        intent.putExtra(MESSAGE_GAME, "new");

                        //dialog.dismiss();
                        if (startGame) startActivity(intent);
                    }
                });
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

                try {

                    InputStream in = openFileInput(filename+".txt");

                    in.close();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    intent.putExtra(MESSAGE_GAME, "load");
                    intent.putExtra(MESSAGE_TURN, HUMAN);
                    intent.putExtra(MESSAGE_FILENAME, filename);

                    startActivity(intent);

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "The file you specified is not found", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


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
