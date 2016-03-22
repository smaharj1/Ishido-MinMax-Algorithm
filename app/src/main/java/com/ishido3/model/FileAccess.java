/************************************************************
 * Name:  Sujil Maharjan                                    *
 * Project:  Project 3/Ishido Game			               *
 * Class:  Artificial Intelligence/CMP 331                  *
 * Date:  3/22/2016			                               *
 ************************************************************/
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

                // This step is very important in order to parse the string into the integers. Otherwise, we get weird errors
                lineSplit[index] = lineSplit[index].replaceAll("\\D","");
                human_score =Integer.parseInt(lineSplit[index]);
            }
            else if (lineSplit[index].contains(COMPUTER_SCORE_START)) {
                index++;
                lineSplit[index] = lineSplit[index].replaceAll("\\D", "");
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

    /**
     * Sets the stock array list from the given string locally
     * @param str Holds the whole stock from the file in string
     */
    public void setStock(String str) {
        // Splits the string with spacebar and then adds it in the queue as integers
        String[] stockStr = str.split(" ");

        // If the stock is not empty, then clears it first
        if (!stock.isEmpty()) {
            stock.clear();
        }

        // Loops through the stockStr and then stores individual stock to the stock array
        for (int index = 0; index < stockStr.length; index++) {
            // Adds the TileInfo to the arraylist of stock
            System.out.println(""+ stockStr[index]);
            if (!stockStr[index].isEmpty()) {
                stockStr[index] = stockStr[index].replaceAll("\\D+","");
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
     * REturns the human score
     * @return Returns the score
     */
    public int getHumanScore() {
        return human_score;
    }

    /**
     * Returns the computer score
     * @return Returns the computer Score
     */
    public int getComputerScore() { return computer_score; }

    /**
     * Returns who the next player is
     * @return Returns who the next player is
     */
    public int getNextPlayer() { return next_player; }

    /**
     * Converst the tile to its numeric equivalent
     * @param tile Holds the tile that needs to be converted
     * @return Returns the integer after conversion
     */
    private int convertTile (TileInfo tile) {
        return ((tile.getNumericColorVal()+1) *10) + (tile.getNumericSymbolVal()+1);
    }

    /**
     * Saves the current progress to the external storage
     * @param board Holds the current Board Model
     * @param stock Holds the current stock
     * @param stockIndex Holds the current stock index so that we save only from current tile
     * @param humanPlayer Holds the current human Player object
     * @param computerPlayer  Holds the current computer Player object
     * @param turn Holds whose turn it is
     * @throws FileNotFoundException
     */
    public void save(Board board, ArrayList<TileInfo> stock, int stockIndex, Player humanPlayer, Player computerPlayer, int turn) throws FileNotFoundException {
        File file = new File(Environment.getExternalStorageDirectory(),"savedGame.txt");

        // Check if the filename already exists. If yes, then delete it and create it again
        if (file.exists()) file.delete();
        FileOutputStream outStream = new FileOutputStream(file);

        // Writes it into the file
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

