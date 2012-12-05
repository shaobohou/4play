package net.swiftkey.fourplay;

/**
 * Deserialised game state returned from the server through polling.
 */
public class GameState {
	private State mState;
	private Board mBoard;
	
	public static GameState WAIT_STATE = new GameState("WAIT", new int[] {0}, 1, 1);
	
	public enum State {
		WAIT,
		MOVE,
		WIN,
		LOSE,
		DRAW
	}
	
	public GameState(String state, int[] board, int rows, int cols) {
		mState = translateState(state);
		mBoard = new Board(board, rows, cols);
	}
	
	public State getState() {
		return mState;
	}
	
	public Board getBoard() {
		return mBoard;
	}
	
	private State translateState(String state) {
		if (state.equals("WAIT")) {
			return State.WAIT;
		} else if (state.equals("MOVE")) {
			return State.MOVE;
		} else if (state.equals("WON")) {
			return State.WIN;
		} else if (state.equals("LOST")) {
			return State.LOSE;
		} else if (state.equals("DRAW")) {
                        return State.DRAW;
                } else throw new IllegalArgumentException("Unknown state: " + state);
	}
	
	public String toString() {
		return "GameState: state=" + mState + " board: \n" + mBoard.toString();
	}
}
