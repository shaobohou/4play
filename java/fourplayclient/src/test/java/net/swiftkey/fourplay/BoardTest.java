package net.swiftkey.fourplay;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import net.swiftkey.fourplay.Board;

public class BoardTest extends TestCase {

    private void assertArrayEquals(int[] a, int[] b) {
        assertEquals(a.length, b.length);
        for(int i = 0; i < a.length; ++i) {
            assertEquals(a[i], b[i]);
        }
    }

    public void testQueryCell() {
        int[] testPieces = {1, -1, 1, 0, -1, 1, 0, 0, 0};
        Board b = new Board(testPieces, 3, 3);

        assertEquals(1, b.queryCell(0, 0));
        assertEquals(-1, b.queryCell(1, 0));
        assertEquals(0, b.queryCell(0, 1));
        assertEquals(-1, b.queryCell(1, 1));
    }

    public void testGetColumn() {
        int[] testPieces = {1, -1, 1, 0, -1, 1, 0, 0, 0};
        Board b = new Board(testPieces, 3, 3);

        assertArrayEquals(new int[] {1, 0, 0}, b.getColumn(0));
        assertArrayEquals(new int[] {-1, -1, 0}, b.getColumn(1));
        assertArrayEquals(new int[] {1, 1, 0}, b.getColumn(2));
    }

    public void testGetRow() {
        int[] testPieces = {1, -1, 1, 0, -1, 1, 0, 0, 0};
        Board b = new Board(testPieces, 3, 3);

        assertArrayEquals(new int[] {1, -1, 1}, b.getRow(0));
        assertArrayEquals(new int[] {0, -1, 1}, b.getRow(1));
        assertArrayEquals(new int[] {0, 0, 0}, b.getRow(2));
    }

    public void testGetDiagonal() {
        int[] testPieces = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        Board b = new Board(testPieces, 3, 3);

        assertArrayEquals(new int[] {0, 4, 8}, b.getDiagonal(1, 1, 1, 1));
        assertArrayEquals(new int[] {6, 4, 2}, b.getDiagonal(1, 1, 1, -1));
        assertArrayEquals(new int[] {0, 4, 8}, b.getDiagonal(0, 0, 1, 1));
        assertArrayEquals(new int[] {6, 4, 2}, b.getDiagonal(0, 2, 1, -1));
        assertArrayEquals(new int[] {7, 5}, b.getDiagonal(1, 2, 1, -1));
    }

    public void testInBounds() {
        int[] testPieces = {1, -1, 1, 0, -1, 1, 0, 0, 0};
        Board b = new Board(testPieces, 3, 3);

        assertTrue(b.inBounds(0, 0));
        assertTrue(b.inBounds(1, 1));
        assertTrue(b.inBounds(2, 2));
        assertFalse(b.inBounds(3, 0));
        assertFalse(b.inBounds(0, 3));
    }

    public void testWithMove() throws Exception {
        int[] testPieces = {1, -1, 1, 0, -1, 1, 0, 0, 0};
        Board b = new Board(testPieces, 3, 3);

        assertEquals(1, b.withMove(0).queryCell(0, 1));
        assertEquals(1, b.withMove(1).queryCell(1, 2));
    }

    public void testWinningPiece() {
        assertEquals(1, Board.winningPiece(new int[] {0, 1, 1, 1, 1, 0, 0}, 4));
        assertEquals(0, Board.winningPiece(new int[] {0, 1, -1, -1, 1, 0, 0}, 4));
        assertEquals(-1, Board.winningPiece(new int[] {0, -1, -1, -1, -1, 1, 1}, 4));
    }

}
