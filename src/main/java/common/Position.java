/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xposte00 - Aleksander Postelga
 */

package main.java.common;

/**
 * Represents a position in a 2D grid, defined by row and column indices.
 */
public class Position {
    private final int row;
    private final int col;

    /**
     * Initializes a position
     *
     * @param row The row index of the position
     * @param col The column index of the position
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row index of this position
     *
     * @return The row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column index of this position
     *
     * @return The column index
     */
    public int getCol() {
        return col;
    }

    /**
     * Compares this position with another object for equality
     *
     * @param o The object to compare with
     * @return true if the other object is a Position with the same row and column, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (row != position.row) return false;
        return col == position.col;
    }

    /**
     * Returns a hash code value for this position
     *
     * @return A hash code value for this object
     */
    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    /**
     * Returns a string representation of this position
     *
     * @return A string representation of this position
     */
    @Override
    public String toString() {
        return "Position\n" + "Row=" + row + "\n" + "Col=" + col + "\n";
    }
}

