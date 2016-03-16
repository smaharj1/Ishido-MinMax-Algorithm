package com.ishido3.model;

import android.content.Context;
import android.os.Environment;

import com.ishido3.view.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class deals with opening and parsing the files. It then parses into appropriate variables for ease.
 * Created by Tsujil on 2/12/2016.
 */
public class FileAccess {
    // Declares the constants for strings to consider while parsing the file
    public static final int FILE = 1;
    private final String LAYOUT_START = "Layout";
    private final String STOCK_START = "Stock";
    private final String HUMAN_SCORE_START = "Human";
    private final String COMPUTER_SCORE_START = "Computer";
    private final String NEXT_PLAYER = "Next";

    // Declares the current context
    private Context currentContext;

    // Declares the string array of board that will hold the information on the board
    private String []boardData = new String[8];

    // Declares the stock and score
    private String stockString;
    private ArrayList<TileInfo> stock = new ArrayList<TileInfo>();
    private int human_score;
    private int computer_score;
    private int next_player;

    /**
     * Constructor of FileAccess class that will set the context of the fileAccess
     * @param context It consists of the context of the android app
     */
    public FileAccess(Context context) {
        currentContext = context;
    }

    /**
     * It reads everything from the file into a string
     * @return Returns the string of the content of the file
     * @throws IOException
     */
    private String readFromFile( String filename)  {
        File file = new File(Environment.getExternalStorageDirectory(), filename+".txt");

        int fileLength = (int) file.length();

        byte[] resultInBytes = new byte[fileLength];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);

            in.read(resultInBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new String(resultInBytes);

    }

    /**
     * It reads the data and splits into the appropriate holdings (boardData, stock, score)
     * @param filename It is the file that needs to be read
     */
    public void readData(String filename) {
        String result = "";
        // Reads the file from the actual file and then converts it into the string
        result= readFromFile(filename);

        // Splits the given string into an array corresponding to lines
        String [] lineSplit = result.split("\n");

        // Loops through each line and puts the data value accordingly
        for (int index=0; index < lineSplit.length; ++index) {
            // If the line contains "layout:", put the eight other lines into a loop and populate the Board string
            if (lineSplit[index].contains(LAYOUT_START)) {
                index++;

                // Loops through each row of the board
                for (int boardIndex=0; boardIndex < 8 ; ++boardIndex) {
                    boardData[boardIndex] = lineSplit[index];
                    ++index;
                }
            }
            else if (lineSplit[index].contains(STOCK_START)) {
                index++;

                stockString = lineSplit[index];
                setStock(stockString);
            }
            else if (lineSplit[index].contains(HUMAN_SCORE_START)) {
                index++;
                human_score =Integer.parseInt(lineSplit[index]);
            }
            else if (lineSplit[index].contains(COMPUTER_SCORE_START)) {
                index++;
                computer_score = Integer.parseInt(lineSplit[index]);
            }
            else if(lineSplit[index].contains(NEXT_PLAYER)) {
                index++;
                if (lineSplit[index].contains("Human")) {
                    next_player = MainActivity.HUMAN;
                }
                else {
                    next_player = MainActivity.COMPUTER;
                }
            }
        }

    }

    public void setStock(String str) {
        // Splits the string with spacebar and then adds it in the queue as integers
        String[] stockStr = str.split(" ");

        if (!stock.isEmpty()) {
            stock.clear();
        }
        for (int index = 0; index < stockStr.length; index++) {
            // Adds the TileInfo to the arraylist of stock
            if (!stockStr[index].isEmpty()) {
                TileInfo tmp = Board.calculateTile(Integer.parseInt(stockStr[index]));
                stock.add(tmp);
            }
        }
    }


    /**
     * Gets the string array of the board data
     * @return Returns the string of board data
     */
    public String[] getBoardData () {
        return boardData;
    }

    /**
     * Returns the stock
     * @return Returns the stock
     */
    public ArrayList<TileInfo> getStock() {
        return stock;
    }

    /**
     * REturns the score
     * @return Returns the score
     */
    public int getHumanScore() {
        return human_score;
    }

    public int getComputerScore() { return computer_score; }

    public int getNextPlayer() { return next_player; }

    private int convertTile (TileInfo tile) {
        return ((tile.getNumericColorVal()+1) *10) + (tile.getNumericSymbolVal()+1);
    }

    public void save(Board board, ArrayList<TileInfo> stock, int stockIndex, Player humanPlayer, Player computerPlayer, int turn) throws FileNotFoundException {
        File file = new File(Environment.getExternalStorageDirectory(),"savedGame.txt");

        if (file.exists()) file.delete();
        FileOutputStream outStream = new FileOutputStream(file);

        try {
            outStream.write("Layout:\n".getBytes());
            // Save everything locally first for easiness
            for (int rowIndex = 0; rowIndex < 8; ++rowIndex) {
                String temp = "";
                for (int colIndex = 0; colIndex < 12; ++colIndex) {
                    TileInfo tileInfo = board.getTile(rowIndex, colIndex);
                    if (tileInfo != null) {
                        int convertedTile = convertTile(tileInfo);
                        temp = temp + Integer.toString(convertedTile) + " ";
                    } else {
                        temp = temp + "00 ";
                    }
                }
                temp += "\n";
                outStream.write(temp.getBytes());
            }

            outStream.write("\n".getBytes());
            outStream.write("Stock:\n".getBytes());

            stockString = "";
            for (int index =stockIndex; index < stock.size(); ++index) {
                int convertedTile = convertTile(stock.get(index));
                stockString += Integer.toString(convertedTile)+ " ";
            }

            outStream.write(stockString.getBytes());
            outStream.write("\n".getBytes());
            outStream.write("\n".getBytes());

            outStream.write("Human Score: \n".getBytes());
            outStream.write(Integer.toString(humanPlayer.getScore()).getBytes());

            outStream.write("\n".getBytes());
            outStream.write("\n".getBytes());
            outStream.write("Computer Score: \n".getBytes());
            outStream.write(Integer.toString(computerPlayer.getScore()).getBytes());
            outStream.write("\n".getBytes());
            outStream.write("\n".getBytes());

            outStream.write("Next Player:\n".getBytes());
            if (turn == MainActivity.HUMAN) {
                outStream.write("Human".getBytes());
            }
            else {
                outStream.write("Computer".getBytes());
            }
        } catch (Exception e) {

        }









    }
}

