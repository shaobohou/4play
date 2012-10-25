package net.swiftkey.fourplay;

/**
 * A Connect4 game board state.
 */
public class Board
{
    // Original Connect4 board dimensions
    public static final int DEFAULT_ROWS = 6;
    public static final int DEFAULT_COLS = 7;

    private final int[] boardState;
    private final int numRows;
    private final int numCols;

    public Board(int[] pieces) {
        this(pieces, DEFAULT_ROWS, DEFAULT_COLS);
    }

    public Board(int[] pieces, int numRows, int numCols) {
        this.boardState = pieces;
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public int queryCell(int x, int y) {
        int offset = (x + (y * this.numCols));
        return this.boardState[offset];
    }

    public int[] getColumn(int x) {
        int[] col = new int[this.numRows];
        for(int i = 0; i < this.numRows; ++i) {
            col[i] = queryCell(x, i);
        }

        return col;
    }

    public int[] getRow(int y) {
        int[] row = new int[this.numCols];
        for(int i = 0; i < this.numCols; ++i) {
            row[i] = queryCell(i, y);
        }

        return row;
    }
}
