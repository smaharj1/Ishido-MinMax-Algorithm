/************************************************************
 * Name:  Sujil Maharjan                                    *
 * Project:  Project 3/Ishido Game			               *
 * Class:  Artificial Intelligence/CMP 331                  *
 * Date:  3/22/2016			                               *
 ************************************************************/

package com.ishido3.model;

/**
 * This class holds the information of a single tile. It holds the information like what is the attributes of tile, the coordinates of tile
 * in the board, its hierarchical parent tile and the total score that would be if we place this tile on the board hierarchically.
 * Created by Tsujil on 2/15/2016.
 */
public class TileNode{
    // Declares the information of the tile
    private TileInfo tile;

    // Declares the table coordinates in which the tile belong to
    private TableCoordinates coordinates;

    // Declares the score until this tile
    // private int playerScore;
    //private int computerScore;

    // Holds the heursitic value of current Node
    private int heuristicVal;
    //private int turn;

    /**
     * Initializes the values of the tile, coordinates, parent tile, and the total score
     * @param tileInfo It consists of the tile information (TileInfo object)
     * @param tableCoordinates It consists of the TableCoordinates object
     */
    public TileNode(TileInfo tileInfo, TableCoordinates tableCoordinates, int heuristic) {
        tile = tileInfo;
        coordinates = tableCoordinates;
        heuristicVal = heuristic;

    }

    /**
     * It initializes the new TileNode to consist of null values
     */
    public TileNode() {
        tile = null;
        coordinates = new TableCoordinates(0,0);

        heuristicVal = 0;

    }

    /**
     * Returns the TileInfo object (color/symbol) of the tile
     * @return Returns the tile
     */
    public TileInfo getTile() {
        return tile;
    }

    /**
     * Returns the coordinates of the tile
     * @return Returns the coordinates of the current tile
     */
    public TableCoordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Checks if the TileNode is empty
     * @return Returns if the tiletree is empty or not
     */
    public boolean isEmpty() {
        return tile == null;
    }

    /**
     * Returns the total score until this tile
     * @return Returns the total score
     */
    /*
    public int getPlayerScore() {
        return playerScore;
    }

    public int getComputerScore() {
        return computerScore;
    }

    */

    /**
     * Sets the heuristic value
     * @param val Holds the value that needs to be set
     */
    public void setHeuristicVal(int val) {
        heuristicVal = val;
    }

    /**
     * Gets the heuristic value
     * @return Returns the heuristic value
     */
    public int getHeuristicVal () {
        return heuristicVal;
    }

}
