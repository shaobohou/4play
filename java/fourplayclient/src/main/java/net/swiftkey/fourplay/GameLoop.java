package net.swiftkey.fourplay;

import net.swiftkey.fourplay.bots.Player;

public class GameLoop {
	private static final int POLL_INTERVAL_MS = 50;

	private final ServiceStub mServer;
	private final Player mPlayer;
	private final String mPlayerName;
	
	private GameState mState = null;
	private int mCurrentGameId = -1;
	private int mPlayedCount = 0;
	private int mWinCount = 0;
	private int mLoseCount = 0;
	private int mDrawCount = 0;
	
	public GameLoop(ServiceStub server, Player bot, String playerName) {
		mServer = server;
		mPlayer = bot;
		mPlayerName = playerName;
	}
	
	public void play(int gamesToPlay) {
    	while(mPlayedCount <= gamesToPlay) {
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
	
	private void printSummary() {
		System.out.println("Player:  " + mPlayerName);
		System.out.println("Game ID: " + mCurrentGameId);
		System.out.println("Played:  " + mPlayedCount);
		System.out.println("Won:     " + mWinCount);
		System.out.println("Lost:    " + mLoseCount);
        System.out.println("Draw:    " + mDrawCount);
	}
	
	private void startNewGame() {
		mPlayedCount += 1;
		mCurrentGameId = mServer.joinTournament(mPlayerName);
		mState = GameState.WAIT_STATE;

	}
	
	private void handleWait() {
		try {
			Thread.sleep(POLL_INTERVAL_MS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mState = mServer.pollTournament(mCurrentGameId);
		
	}
	
	private void handleMove() {
		int move = mPlayer.move(mState.getBoard());
		mServer.moveTournament(mCurrentGameId, move);
		mState = GameState.WAIT_STATE;
	}
	
	private void handleWin() {
		mWinCount += 1;
		mState = GameState.WAIT_STATE;
	}
	
	private void handleLose() {
		mLoseCount += 1;
        mState = GameState.WAIT_STATE;
	}
	
	private void handleDraw() {
		mDrawCount += 1;
        mState = GameState.WAIT_STATE;
	}
}
