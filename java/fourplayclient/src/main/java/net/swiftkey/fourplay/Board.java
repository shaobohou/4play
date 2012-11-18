package net.swiftkey.fourplay;

import java.util.List;
import java.util.ArrayList;

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
    
    public String toString() {
    	if (this.boardState == null) {
    		return "<invalid board>";
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	for (int y = this.numRows - 1; y >= 0; --y) {
        	for (int x = 0; x < this.numCols; ++x) {
        		sb.append(this.queryCell(x, y));
        		sb.append(" | ");
        	}
        	sb.append("\n");
    	}
    	
    	return sb.toString();
    }

    public int countRows() {
    	return numRows;
    }
    
    public int countCols() {
    	return numCols;
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

    public boolean inBounds(int x, int y) {
        return (x >= 0) && (x < this.numCols)
            && (y >= 0) && (y < this.numRows);
    }

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

    public Board withMove(int col) throws Exception {
        int[] newBoardState = (int[]) boardState.clone();
        int rowOffset = nextRow(col);
        if(rowOffset == -1) {
            throw new Exception("Bad move - column is full!");
        }

        newBoardState[(rowOffset * this.numCols) + col] = 1;
        return new Board(newBoardState, this.numRows, this.numCols);
    }

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

    public boolean willWin(int moveColumn, int winLength, int player) throws Exception {
        Board nextState = this.withMove(moveColumn);

        int x = moveColumn, y = nextRow(moveColumn);
        return (winningPiece(nextState.getColumn(x), winLength) == player)
            || (winningPiece(nextState.getRow(y), winLength) == player)
            || (winningPiece(nextState.getDiagonal(x, y, 1, 1), winLength) == player)
            || (winningPiece(nextState.getDiagonal(x, y, 1, -1), winLength) == player);
    }

    public boolean willLose(int moveColumn, int winLength, int player) throws Exception {
        return willWin(moveColumn, winLength, player * -1);
    }

}
