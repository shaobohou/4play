package net.swiftkey.fourplay;



public class Main
{
	private static final String PLAYER_NAME = "Player1";
	
	private static final int POLL_INTERVAL_MS = 50;
	private static final int TOTAL_GAMES = 10;

	// TODO - set this with a command line arg
	private static ServiceStub mServer = new ServiceStub("localhost", 3000);
	
	// TODO - Plug in a different solver!
	private static Player mPlayer = new IdiotPlayer();
	
	private static GameState mState = null;
	private static int mCurrentGameId = -1;
	private static int mPlayedCount = 0;
	private static int mWinCount = 0;
	private static int mLoseCount = 0;
	private static int mDrawCount = 0;
	
	public static void main(String[] args) {
    	while(mPlayedCount <= TOTAL_GAMES) {
    		if (mState != null) {
    			// game in progress, continue
        		switch(mState.getState()) {
        		case WAIT:
        			handleWait();
        			break;
        		case MOVE:
        			handleMove();
        			break;
        		case WIN:
        			handleWin();
        			break;
        		case LOSE:
        			handleLose();
        			break;
        		case DRAW:
        			handleDraw();
        			break;
        		}
    		} else {
    			// state not set yet, so new game.
    			startNewGame();
    		}
    	}
    	
    	printSummary();
    }
	
	private static void printSummary() {
		System.out.println("Played: " + mPlayedCount);
		System.out.println("Won:    " + mWinCount);
		System.out.println("Lost:   " + mLoseCount);
	}
	
	private static void startNewGame() {
		mPlayedCount += 1;
		mCurrentGameId = mServer.joinTournament(PLAYER_NAME);
		mState = GameState.WAIT_STATE;

		System.out.println("startNewGame: gameId=" + mCurrentGameId);
	}
	
	private static void handleWait() {
		try {
			Thread.sleep(POLL_INTERVAL_MS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mState = mServer.pollTournament(mCurrentGameId);
		
		System.out.println("handleWait: " + mState.toString());
	}
	
	private static void handleMove() {
		int move = mPlayer.move(mState.getBoard());
		mServer.moveTournament(mCurrentGameId, move);
		mState = GameState.WAIT_STATE;
		System.out.println("handleMove: move=" + move);
	}
	
	private static void handleWin() {
		mWinCount += 1;
		mState = GameState.WAIT_STATE;
		System.out.println("handleWin: mWinCount=" + mWinCount);
	}
	
	private static void handleLose() {
		mLoseCount += 1;
        mState = GameState.WAIT_STATE;
		System.out.println("handleLose: mLoseCount=" + mLoseCount);
	}
	
	private static void handleDraw() {
		mDrawCount += 1;
        mState = GameState.WAIT_STATE;
		System.out.println("handleDraw: mDrawCount=" + mDrawCount);
	}
}
