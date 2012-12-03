package net.swiftkey.fourplay.bots;

import net.swiftkey.fourplay.Board;

/**
 * Implement this interface to solve Connect 4 games.
 *
 */
public interface Player {
	/**
	 * Given the current board state, b, return an integer
	 * within the range [0 .. (b.countCols() - 1)] representing
	 * your chosen move.
	 * 
	 * @param b The state of the board now.
	 * @return Chosen move index.
	 */
	int move(Board b);
}
