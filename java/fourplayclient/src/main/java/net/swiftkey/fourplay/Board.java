package net.swiftkey.fourplay;

import java.util.List;
import java.util.ArrayList;

/**
 * A Connect-N game board state.
 * 
 * The board is immutable. Constructors expect a sequence of
 * piece positions, exactly as deserialised from JSON. Use any
 * of the public methods to guide your game moves, and make use
 * of the withMove() function to model subsequent game states.
 * 
 * Note that pieces in the board are represent as integers:
 *   0 - indicates empty cell.
 *   1 - cell occupied by you
 *  -1 - cell occupied by opponent.
 *  
 */
public class Board
{
    // Original Connect4 board dimensions
    public static final int DEFAULT_ROWS = 6;
    public static final int DEFAULT_COLS = 7;

    private final int[] boardState;
    private final int numRows;
    private final int numCols;

    /**
     * Construct a Board state from deserialised JSON representation.
     * Uses DEFAULT_COLS and DEFAULT_ROWS (7x6), same as an original
     * Connect 4 board.
     * 
     * @param pieces board state, exactly as represented by server.
     */
    public Board(int[] pieces) {
        this(pieces, DEFAULT_ROWS, DEFAULT_COLS);
    }

    /**
     * Construct a board state from JSON representation, but provide
     * dimensions explicitly. Note that the length of pieces must be
     * exactly numRows * numCols.
     * 
     * @param pieces board state, exactly as represented by server.
     * @param numRows number of rows in board
     * @param numCols number of columns in board.
     */
    public Board(int[] pieces, int numRows, int numCols) {
        assert (pieces.length == (numRows * numCols));

        this.boardState = pieces;
        this.numRows = numRows;
        this.numCols = numCols;
    }
    
    /**
     * Pretty-print the board state, in columns.
     */
    public String toString() {
    	if (this.boardState == null) {
    		return "<invalid board>";
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	for (int y = this.numRows - 1; y >= 0; --y) {
        	for (int x = 0; x < this.numCols; ++x) {
        		sb.append(String.format(" %-2s |", Integer.toString(this.queryCell(x, y))));
        	}
        	sb.append("\n");
    	}
    	
    	return sb.toString();
    }

    /**
     * Number of columns in board.
     */
    public int countRows() {
    	return numRows;
    }
    
    /**
     * Number of rows in board.
     */
    public int countCols() {
    	return numCols;
    }

    /**
     * Return the value of cell at (x,y).
     * 
     * @param x column offset (0-based)
     * @param y row offset (0-based)
     * @return -1, 0, or 1 to indicate cell state.
     */
    public int queryCell(int x, int y) {
        int offset = (x + (y * this.numCols));
        return this.boardState[offset];
    }

    /**
     * Return the state of the cells in column x.
     * 
     * @param x column offset (0-based).
     * @return array of cell states, where the first element
     *         will be the "bottom" cell in this column.
     */
    public int[] getColumn(int x) {
        int[] col = new int[this.numRows];
        for(int i = 0; i < this.numRows; ++i) {
            col[i] = queryCell(x, i);
        }

        return col;
    }

    /**
     * Return the state of cells in row y.
     * 
     * @param y row offset (0-based).
     * @return array of cell states, where the first element
     *         will be the left-most cell in this row.
     */
    public int[] getRow(int y) {
        int[] row = new int[this.numCols];
        for(int i = 0; i < this.numCols; ++i) {
            row[i] = queryCell(i, y);
        }

        return row;
    }

    /**
     * Check to see if (x, y) is within the bounds of
     * this board.
     * 
     * @param x column offset (0-based)
     * @param y row offset (0-based)
     * @return true if cell is in bounds, else false.
     */
    public boolean inBounds(int x, int y) {
        return (x >= 0) && (x < this.numCols)
            && (y >= 0) && (y < this.numRows);
    }

    /**
     * Return a sequence of cell states representing a diagonal
     * in the board.
     * 
     * @param x starting column offset
     * @param y starting row offset
     * @param dx change in x (1 or -1), to pick a 'direction'
     * @param dy change in y (1 or -1), to pick a 'direction'
     * @return array representing cell states.
     */
    public int[] getDiagonal(int x, int y, int dx, int dy) {
        List<Integer> diagonal = new ArrayList<Integer>();

        int xpos = x, ypos = y;
        while(inBounds(xpos, ypos)) {
            diagonal.add(diagonal.size(), this.queryCell(xpos, ypos));
            xpos += dx;
            ypos += dy;
        }

        int xneg = (x - dx), yneg = (y - dy);
        while(inBounds(xneg, yneg)) {
            diagonal.add(0, this.queryCell(xneg, yneg));
            xneg -= dx;
            yneg -= dy;
        }

        int[] diag = new int[diagonal.size()];
        for(int i = 0; i < diag.length; ++i) {
            diag[i] = diagonal.get(i);
        }

        return diag;
    }

    /**
     * Return the "next" row to be occcupied by placing
     * a piece in the provided column. That is, the row
     * offset that the piece will drop into if you play
     * this column.
     * 
     * @param col column-offset, 0-based.
     * @return next occupied row offset.
     */
    public int nextRow(int col) {
        int[] colPieces = getColumn(col);
        int rowOffset = -1;
        for(int i = 0; i < colPieces.length; ++i) {
            if(colPieces[i] == 0) {
                rowOffset = i;
                break;
            }
        }

        return rowOffset;
    }

    /**
     * Return a new Board state instance representing how
     * the board would be if you played the given move.
     * 
     * @param col column to place a piece.
     * @return new board state instance, representing your move.
     * @throws Exception if your move isn't valid.
     */
    public Board withMove(int col) throws Exception {
        int[] newBoardState = (int[]) boardState.clone();
        int rowOffset = nextRow(col);
        if(rowOffset == -1) {
            throw new Exception("Bad move - column is full!");
        }

        newBoardState[(rowOffset * this.numCols) + col] = 1;
        return new Board(newBoardState, this.numRows, this.numCols);
    }

    /**
     * Check to see if the given sequence of cells indicate that
     * either player has won.
     * 
     * @param pieces sequence of cell states.
     * @param winLength length of consecutive pieces required to win in this game.
     * @return -1 if opponent has won, 1 if this player wins, else 0.
     */
    static int winningPiece(int[] pieces, int winLength) {
        int[] counters = {0, 0, 0};
        for(int i = 0; i < pieces.length; ++i) {
            int counterIndex = pieces[i] + 1;
            int count = counters[counterIndex] + 1;
            counters = new int[] {0, 0, 0};
            counters[counterIndex] = count;

            int token = counterIndex - 1;
            if (count == winLength && token != 0) {
                return token;
            }
        }

        return 0;
    }

    /**
     * Return true if playing a move in moveColumn will cause
     * the given player to win this game.
     * 
     * @param moveColumn column offset to place a piece (0-based)
     * @param winLength length of line needed win in this game.
     * @param player player's number (i.e. 1 for this player, -1 for other player).
     * @return true if the move will cause that player to win.
     * @throws Exception if the move isn't valid.
     */
    public boolean willWin(int moveColumn, int winLength, int player) throws Exception {
        Board nextState = this.withMove(moveColumn);

        int x = moveColumn, y = nextRow(moveColumn);
        return (winningPiece(nextState.getColumn(x), winLength) == player)
            || (winningPiece(nextState.getRow(y), winLength) == player)
            || (winningPiece(nextState.getDiagonal(x, y, 1, 1), winLength) == player)
            || (winningPiece(nextState.getDiagonal(x, y, 1, -1), winLength) == player);
    }

    /**
     * The opposite of willWin.
     * 
     * @param moveColumn column offset to place a piece (0-based)
     * @param winLength length of line needed win in this game.
     * @param player player's number (i.e. 1 for this player, -1 for other player).
     * @return true if the move will cause that player to lose.
     * @throws Exception if the move isn't valid.
     */
    public boolean willLose(int moveColumn, int winLength, int player) throws Exception {
        return willWin(moveColumn, winLength, player * -1);
    }

    /**
     * Check to see if the board is full.
     * 
     * @return true if the board is full, otherwise false.
     */
    public boolean isComplete() {
        for (int i = 0; i < this.numCols; ++i) {
            for (int j = 0; j < this.numRows; ++j) {
                if (this.getColumn(i)[j] == 0) {
                    return false;
                }
            }
        }
        
        return true;
    }
}
