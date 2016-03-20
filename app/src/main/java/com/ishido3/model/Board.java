/************************************************************
 * Name:  Sujil Maharjan                                    *
 * Project:  Project 2/Ishido Game			               *
 * Class:  Artificial Intelligence/CMP 331                  *
 * Date:  2/23/2016			                               *
 ************************************************************/

package com.ishido3.model;

import java.util.ArrayList;

/**
 * Holds the game board and updates the board
 * Created by Tsujil on 1/26/2016.
 */
public class Board implements Cloneable{
    // Total number of rows
    public static final int TOTAL_ROWS = 8;

    // Total number of columns in the board
    public static final int TOTAL_COLUMNS = 12;

    private ArrayList<TileInfo> stock = new ArrayList<TileInfo>();
    private int stockIndex = 0;

    private boolean isGodMode = false;

    // Two dimensional array of tiles with their respective information
    private TileInfo board[][] = new TileInfo[TOTAL_ROWS][TOTAL_COLUMNS];

    /**
     * Initializes the board with null values in each cell
     */
    public Board() {
        for (int row = 0; row < TOTAL_ROWS; ++row) {
            for (int column = 0; column < TOTAL_COLUMNS; ++column) {
                board[row][column] = null;
            }
        }
    }

    public void enableGodMode() {
        isGodMode = true;
    }

    public void disableGodMode() {
        isGodMode = false;
    }
    /**
     * This is called by the activity to start performing the heuristic of min max
     */
    public TileNode startAlgorithm(ArrayList<TileInfo> stock, int sIndex, int humanScore, int computerScore, int cutoff) {
        this.stock = stock;
        stockIndex = sIndex;

        // The tile held would be null and coordinates to start with would be 0,0
        TileNode arbitraryNode = new TileNode(null,new TableCoordinates(-1,-1),Integer.MIN_VALUE);


        return performMinMax(arbitraryNode, true, true, cutoff, humanScore, computerScore,Integer.MIN_VALUE,Integer.MAX_VALUE);
    }


    /**
     * This is the main min max algorithm function that is used for the computer's case more often
     */

    public TileNode performMinMax(TileNode originNode, boolean isMaximizer, boolean isComputer, int cutoffVal, int humanScore, int computerScore, int alphaH, int betaH) {
        // This means that it is the last node. So, it calculates the heuristic of the node
        if (cutoffVal ==0) {
            int heuristicVal=0;
            if (!isComputer) {
                heuristicVal = humanScore - computerScore;
            }
            else {
                heuristicVal = computerScore - humanScore;
            }
            //heuristicVal = Math.abs(heuristicVal);
            //System.out.println("Heuristic calculated. " + humanScore + " - " + computerScore + " = " + heuristicVal);
            //System.out.println("Stock index: " + stockIndex);
            return new TileNode (originNode.getTile(),originNode.getCoordinates(),heuristicVal);
        }

        if (isMaximizer) {
            TileInfo tempInfo = stock.get(stockIndex);
            //System.out.println("MAXIMIER WINDOW:::: working for stock index "+ stockIndex);
            stockIndex++;
            TileNode tempNode = new TileNode(tempInfo, new TableCoordinates(-1,-1), Integer.MIN_VALUE);

            TileNode childNode = findNextAvailableTileNode(tempNode);
            TileNode bestChoice = new TileNode(null, null, Integer.MIN_VALUE);
            while (childNode != null) {
                //System.out.println("Child node has "+ childNode.getCoordinates().getRow() + "   " + childNode.getCoordinates().getColumn());
                int tempScore = calculateScore(childNode.getCoordinates().getRow(), childNode.getCoordinates().getColumn(), childNode.getTile());
                if (isComputer) {
                    computerScore += tempScore;
                }
                else {
                    humanScore += tempScore;
                }

                isComputer = !isComputer;

                // Fill the tile on the board and perform min max with new node that is increased in stock
                fillTile(childNode.getCoordinates().getRow(), childNode.getCoordinates().getColumn(), childNode.getTile());

                TileNode algoNode = performMinMax(childNode,false, isComputer, cutoffVal-1, humanScore, computerScore, alphaH, betaH);

                //System.out.println("Heuristic for this is " + algoNode.getHeuristicVal());
                removeTile(childNode.getCoordinates().getRow(), childNode.getCoordinates().getColumn());

                if (algoNode.getHeuristicVal() > bestChoice.getHeuristicVal()) {

                    System.out.println("Better node found! It is " + algoNode.getHeuristicVal() + "  PREVIOUS HEURISTIC: " + bestChoice.getHeuristicVal() + " for stock index " + (stockIndex - 1) + "    " + childNode.getCoordinates().getRow() + "  " + childNode.getCoordinates().getColumn());

                    childNode.setHeuristicVal(algoNode.getHeuristicVal());
                    bestChoice = childNode;
                }

                if (isGodMode) {
                    if (bestChoice.getHeuristicVal() > alphaH)
                        alphaH = bestChoice.getHeuristicVal();

                    if (betaH <= alphaH) {
                        System.out.println("Alpha beta called at index " + (stockIndex - 1) + " from MAX");
                        break;
                    }
                }
                isComputer = !isComputer;

                if (isComputer) {
                    computerScore -= tempScore;
                }
                else {
                    humanScore -= tempScore;
                }

                childNode = findNextAvailableTileNode(childNode);
            }
            stockIndex--;
            return bestChoice;

        }
        else {
            TileInfo tempInfo = stock.get(stockIndex);
            //System.out.println("MAXIMIER WINDOW:::: working for stock index "+ stockIndex);
            stockIndex++;
            TileNode tempNode = new TileNode(tempInfo, new TableCoordinates(-1,-1), Integer.MAX_VALUE);

            TileNode childNode = findNextAvailableTileNode(tempNode);
            TileNode bestChoice = new TileNode(null, null, Integer.MAX_VALUE);
            while (childNode != null) {
                int tempScore = calculateScore(childNode.getCoordinates().getRow(), childNode.getCoordinates().getColumn(), childNode.getTile());
                if (isComputer) {
                    computerScore += tempScore;
                }
                else {
                    humanScore += tempScore;
                }

                isComputer = !isComputer;

                // Fill the tile on the board and perform min max with new node that is increased in stock
                fillTile(childNode.getCoordinates().getRow(), childNode.getCoordinates().getColumn(), childNode.getTile());

                TileNode algoNode = performMinMax(childNode,true, isComputer, cutoffVal-1, humanScore, computerScore, alphaH, betaH);

                removeTile(childNode.getCoordinates().getRow(), childNode.getCoordinates().getColumn());

                if (algoNode.getHeuristicVal() < bestChoice.getHeuristicVal()) {
                    System.out.println("Better node found! It is " + algoNode.getHeuristicVal() + "  PREVIOUS HEURISTIC: " + bestChoice.getHeuristicVal()+ " for stock index " + (stockIndex-1) + "    " + childNode.getCoordinates().getRow() + "  " + childNode.getCoordinates().getColumn());
                    childNode.setHeuristicVal(algoNode.getHeuristicVal());
                    bestChoice = childNode;
                }

                if(isGodMode) {
                    if (bestChoice.getHeuristicVal() < betaH) betaH = bestChoice.getHeuristicVal();

                    if (betaH <= alphaH) {
                        //System.out.println("Alpha beta called at index " + (stockIndex-1) + " from MIN");
                        break;
                    }
                }

                isComputer = !isComputer;

                if (isComputer) {
                    computerScore -= tempScore;
                }
                else {
                    humanScore -= tempScore;
                }

                childNode = findNextAvailableTileNode(childNode);
            }
            stockIndex--;
            return bestChoice;
        }
    }





    /**
     * Checks if the tile is available in the board
     *
     * @param row    Row number in table
     * @param column Column number in table
     * @return Returns if the tile is available
     */
    public boolean isTileAvailable(int row, int column) {
        return board[row][column] == null;
    }

    public TileInfo getTile(int row, int column) {
        return board[row][column];
    }

    /**
     * Fills the tile with TileInfo in the given cell by verifying if it can be placed.
     *
     * @param row    Row number in table
     * @param column Column number in table
     * @param tile   TileInfo provided
     * @return Returns if writing to the cell is successful or not
     */
    public boolean canFillTile(int row, int column, TileInfo tile) {
        boolean isNull = true;

        if (board[row][column]!= null) {
            return false;
        }
        // First verify if we can place the tile. If yes, then check with the tiles on the four sides if there is any rules restricting it from being placed.
        if (column > 0) {
            if (board[row][column - 1] != null) {
                isNull = false;
                if ((tile.getNumericColorVal() != board[row][column - 1].getNumericColorVal()) && (tile.getNumericSymbolVal() != board[row][column - 1].getNumericSymbolVal())) {
                    //board[row][column] = tile;
                    //System.out.println("Color: " + board[row][column].getNumericColorVal() + " Symbol: " + board[row][column].getNumericSymbolVal());
                    return false;
                }
            }
        }
        if (column < 11) {
            if (board[row][column + 1] != null) {
                isNull = false;
                if ((tile.getNumericColorVal() != board[row][column + 1].getNumericColorVal()) && (tile.getNumericSymbolVal() != board[row][column + 1].getNumericSymbolVal())) {
                    //board[row][column] = tile;
                    //System.out.println("Color: " + board[row][column].getNumericColorVal() + " Symbol: " + board[row][column].getNumericSymbolVal());
                    return false;
                }
            }
        }
        if (row > 0) {
            if (board[row - 1][column] != null) {
                isNull = false;
                if ((tile.getNumericColorVal() != board[row - 1][column].getNumericColorVal()) && (tile.getNumericSymbolVal() != board[row - 1][column].getNumericSymbolVal())) {
                    //board[row][column] = tile;
                    return false;
                }
            }
        }
        if (row < 7) {
            if (board[row + 1][column] != null) {
                isNull = false;
                if ((tile.getNumericColorVal() != board[row + 1][column].getNumericColorVal()) && (tile.getNumericSymbolVal() != board[row + 1][column].getNumericSymbolVal())) {
                    //board[row][column] = tile;

                    return false;
                }
            }
        }

        // If there is nothing on the sides, then place the tile
        if (isNull == true) {
            //board[row][column] = tile;

            return true;
        }

        //System.out.println("end of conditions");
        return true;
    }

    public void fillTile(int row, int col, TileInfo tile) {
        board[row][col] = tile;
    }

    public void removeTile(int row, int col) {
        board[row][col] = null;
    }

    /**
     * Calculates the score that the player gets in each entry
     *
     * @param row    Row number in the table
     * @param column Column number in the table
     * @param tile   TileInfo
     * @return Returns the score of the current tile placed
     */
    public int calculateScore(int row, int column, TileInfo tile) {
        int score = 0;

        // Checks all four sides of the tile for the similarities. If there are, then adds it to the score.
        if (column > 0) {
            if (board[row][column - 1] != null) {
                if ((tile.getNumericColorVal() == board[row][column - 1].getNumericColorVal()) || (tile.getNumericSymbolVal() == board[row][column - 1].getNumericSymbolVal())) {
                    score++;
                }
            }
        }
        if (column < 11) {
            if (board[row][column + 1] != null) {
                if ((tile.getNumericColorVal() == board[row][column + 1].getNumericColorVal()) || (tile.getNumericSymbolVal() == board[row][column + 1].getNumericSymbolVal())) {
                    score++;
                }
            }
        }
        if (row > 0) {
            if (board[row - 1][column] != null) {
                if ((tile.getNumericColorVal() == board[row - 1][column].getNumericColorVal()) || (tile.getNumericSymbolVal() == board[row - 1][column].getNumericSymbolVal())) {
                    score++;
                }
            }
        }
        if (row < 7) {
            if (board[row + 1][column] != null) {
                if ((tile.getNumericColorVal() == board[row + 1][column].getNumericColorVal()) || (tile.getNumericSymbolVal() == board[row + 1][column].getNumericSymbolVal())) {
                    score++;
                }
            }
        }

        return score;
    }

    /**
     * It checks if the board has any tiles left and if there are tiles left, it checks if the tiles are available to be placed.
     *
     * @param deck It consists of the deck that holds current tile combinations
     * @return Returns true if the board cannot place any tiles i.e. it represents the board is done with solutions
     */
    public boolean isDone(Deck deck) {
        boolean freeTiles = true;
        // Go through each and every cells in the board and check if there are empty cells where tiles can be placed.
        for (int row = 0; row < TOTAL_ROWS; ++row) {
            for (int column = 0; column < TOTAL_COLUMNS; ++column) {
                // Each cell is represented by board[row][column]
                // Check if the current cell is empty. Only go forward if it is null
                if (board[row][column] == null) {
                    return false;
//                    if (checkTileAvailability(row, column, deck)) {
//                        return false;
//                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if the tile that meets the color & symbol combination is available in the deck
     *
     * @param row    It represents the row number of the current empty cell
     * @param column It represents the column number of the current empty cell
     * @param deck   It represents the Deck class that contains the current combinations of colors & symbols
     * @return Returns true if there are tiles available to be placed in that particular cell.
     */
    private boolean checkTileAvailability(int row, int column, Deck deck) {
        boolean sideEmpty = true;
        boolean isColorAvailable = false;
        boolean isSymbolAvailable = false;

        // Checks if the color/symbol with different symbol/color is available in the deck. If yes, returns that there are tiles which can be placed in the selected cell.
        if (column > 0) {
            if (board[row][column - 1] != null) {
                sideEmpty = false;
                isColorAvailable = checkColorAvailability(board[row][column - 1].getNumericColorVal(), deck);
                isSymbolAvailable = checkSymbolAvailability(board[row][column - 1].getNumericSymbolVal(), deck);
                if (isColorAvailable || isSymbolAvailable) {
                    return true;
                }
            }
        }

        if (column < 11) {
            if (board[row][column + 1] != null) {
                sideEmpty = false;
                isColorAvailable = checkColorAvailability(board[row][column + 1].getNumericColorVal(), deck);
                isSymbolAvailable = checkSymbolAvailability(board[row][column + 1].getNumericSymbolVal(), deck);
                if (isColorAvailable || isSymbolAvailable) {
                    return true;
                }
            }
        }

        if (row > 0) {
            if (board[row - 1][column] != null) {
                sideEmpty = false;
                isColorAvailable = checkColorAvailability(board[row - 1][column].getNumericColorVal(), deck);
                isSymbolAvailable = checkSymbolAvailability(board[row - 1][column].getNumericSymbolVal(), deck);
                if (isColorAvailable || isSymbolAvailable) {
                    return true;
                }
            }
        }

        if (row < 7) {
            if (board[row + 1][column] != null) {
                sideEmpty = false;
                isColorAvailable = checkColorAvailability(board[row + 1][column].getNumericColorVal(), deck);
                isSymbolAvailable = checkSymbolAvailability(board[row + 1][column].getNumericSymbolVal(), deck);
                if (isColorAvailable || isSymbolAvailable) {
                    return true;
                }
            }
        }

        if (sideEmpty == true) {
            return true;
        }
        return false;
    }

    /**
     * It checks if the color with any symbol is available in the deck
     *
     * @param color It represents the color needed
     * @param deck  It represents the current deck to search into
     * @return Returns true if there is color available (doesn't matter which symbol)
     */
    private boolean checkColorAvailability(int color, Deck deck) {
        // Checks the deck for a certain color with different symbol and their availability
        for (int symbol = 0; symbol < deck.getTotalRowColumn(); ++symbol) {
            if (deck.verifyTile(color, symbol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * It checks if the symbol with any color is available in the deck
     *
     * @param symbol It represents the symbol needed
     * @param deck   It represents the current deck to search into
     * @return Returns true if there is symbol available (doesn't matter which color)
     */
    private boolean checkSymbolAvailability(int symbol, Deck deck) {
        // Checks the deck for a certain symbol with different colors and their availability
        for (int color = 0; color < deck.getTotalRowColumn(); ++color) {
            if (deck.verifyTile(color, symbol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fills the board with the data received from the string array. String array can be gotten from FileAccess.
     * @param boardInfo Consists of string array of data
     * @param deck Consists of the deck of the current game
     */
    public void fillBoard(String[] boardInfo, Deck deck ) {
        // Loop through each row
        for (int rowIndex = 0; rowIndex<TOTAL_ROWS; ++rowIndex) {
            String temp = boardInfo[rowIndex];

            // Split the row of the boardInfo with space bar and divide it into values
            String[] values = temp.split(" ");

            //Loop thorugh the columns of the board and put the values if it exists
            for (int columnIndex=0; columnIndex < TOTAL_COLUMNS; columnIndex++) {
                // Converts it into the numeric value
                values[columnIndex] = values[columnIndex].replaceAll("\\D","");
                int numericValue = Integer.parseInt(values[columnIndex]);

                // If there exists the value, put it in the board
                if (numericValue != 0) {
                    TileInfo tile = calculateTile(numericValue);

                    fillTile(rowIndex, columnIndex, tile);
                    deck.recordTile(tile.getNumericColorVal(),tile.getNumericSymbolVal());
                }
                else {
                    TileInfo tile = null;
                }
            }
        }
    }

    /**
     * Calculates the TileInfo from the given integer equivalent of the tile
     * @param tileValue Numeric representation of the tile
     * @return Returns the TileINfo object of the given numeric equivalent
     */
    public static TileInfo calculateTile(int tileValue) {
        int colorVal = (tileValue/10)-1;
        int symbolVal = (tileValue%10)-1;

        TileInfo tile = new TileInfo();
        tile.setColor(colorVal);
        tile.setSymbol(symbolVal);

        return tile;
    }

    /**
     * Finds the next available location starting the search from the given row and column
     * @param tileNode
     * @return Returns the coordinates that is available. NULL if not available
     */
    public TileNode findNextAvailableTileNode(TileNode tileNode) {
        // Creating the first case scenario
        TableCoordinates coords = tileNode.getCoordinates();
        int row;
        int col;
        //System.out.println("Row and Col initial: " + coords.getRow()+"  " + coords.getColumn());
        if (coords.getColumn() ==-1 || coords.getRow() == -1) {
            row = 0;
            col = 0;
            //System.out.println("-1 point is hit");
        }
        else {
            row = tileNode.getCoordinates().getRow();
            col = tileNode.getCoordinates().getColumn();

            if (row==7 && col == 11) return null;
            if (col <11) col++;
            else {
                col =0;
                row++;
            }


        }

       // System.out.println("Loop start row and column: " + row+"   " + col);
        for (int rowIndex = row; rowIndex<TOTAL_ROWS; rowIndex++) {
            for (int colIndex = col; colIndex <TOTAL_COLUMNS; colIndex++) {
                if (canFillTile(rowIndex,colIndex, tileNode.getTile())) {
                    TableCoordinates tempCoords = new TableCoordinates(rowIndex,colIndex);

                    return new TileNode(tileNode.getTile(),tempCoords,tileNode.getHeuristicVal());
                }
            }
        }

        return null;
    }

}


/******************************************************************
 * Discarded Functions
 */


