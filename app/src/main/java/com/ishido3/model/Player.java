/************************************************************
 * Name:  Sujil Maharjan                                    *
 * Project:  Project 3/Ishido Game			               *
 * Class:  Artificial Intelligence/CMP 331                  *
 * Date:  3/22/2016			                               *
 ************************************************************/
package com.ishido3.model;

/**
 * Holds the player's score
 */
public class Player {
    private int score;

    /**
     * Initializes the player score to 0
     */
    public Player() {
        score = 0;
    }

    /**
     * Adds the score to the total of the player
     *
     * @param value Given score from calculation
     */
    public void addScore(int value) {
        score += value;
    }

    /**
     * Gets the current score
     *
     * @return Returns the total score of the player
     */
    public int getScore() {
        return score;
    }

    /**
     * Removes the score from the total score
     * @param value It consists of the value that should be removed from the total
     */
    public void removeScore(int value) {
        score -= value;
    }

}
