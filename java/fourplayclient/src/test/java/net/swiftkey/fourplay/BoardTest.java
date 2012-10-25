package net.swiftkey.fourplay;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import net.swiftkey.fourplay.Board;

public class BoardTest extends TestCase {

  public void testQueryCell() throws Exception {
      int[] testPieces = {1, -1, 1, 0, -1, 1, 0, 0, 0};
      Board b = new Board(testPieces);

      assertEquals(1, b.queryCell(0, 0));
      assertEquals(-1, b.queryCell(1, 0));
      assertEquals(0, b.queryCell(0, 1));
      assertEquals(0, b.queryCell(1, 1));
  }

}
