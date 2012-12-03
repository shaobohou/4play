package net.swiftkey.fourplay;

import java.util.Arrays;



public class Main
{
    private static final String PLAYER_NAME = "Player1";
	
    private static final int POLL_INTERVAL_MS = 50;
    private static final int TOTAL_GAMES = 10;

    // TODO - set this with a command line arg
    private static ServiceStub mServer = new ServiceStub("127.0.0.1", 3000);
	
    // TODO - Plug in a different solver!
    private static Player mPlayer;
    private static Player iPlayer = new IdiotPlayer();
    private static Player rPlayer = new RandomPlayer(10000, 10);
    private static Player mmPlayer1 = new MinimaxPlayer(1);
    private static Player mmPlayer2 = new MinimaxPlayer(2);
    private static Player mmPlayer3 = new MinimaxPlayer(3);
    private static Player mmPlayer4 = new MinimaxPlayer(4);

    private static GameState mState = null;
    private static int mCurrentGameId = -1;
    private static int mPlayedCount = 0;
    private static int mWinCount = 0;
    private static int mLoseCount = 0;
    private static int mDrawCount = 0;

    public static void main(String[] args) {
        // int[] state = {1, -1, -1,  1, -1, -1,  1,
        //                1, -1,  1,  1, -1,  1,  1,
        //                0,  1, -1,  1,  1, -1, -1,
        //                0, -1,  1, -1,  1,  1,  1,
        //                0, -1,  1, -1,  1, -1, -1,
        //                0, -1, -1,  1, -1,  1, -1};
        // Board tempBoard = new Board(state, 6, 7);

        Board tempBoard = new Board(new int[42], 6, 7);
        try {
            System.out.println(rPlayer.move(tempBoard));
            System.out.println(mmPlayer2.move(tempBoard.withMove(3).withMove(4)));
        } catch (Exception e) {
            System.out.println("Exception in the main testing code!");
        }

        System.out.println(mmPlayer3.move(tempBoard));
        // System.exit(1);

        int[] scores = {0, 0, 0};
        try {
            for(int i = 0; i < 100; ++i) {
                switch (deathMatch(tempBoard, mmPlayer1, mmPlayer2)) {
                case -1: ++scores[0]; break;
                case  0: ++scores[1]; break;
                case  1: ++scores[2]; break;
                }
            }
        } catch (Exception e) {
            System.out.println("some exception " + e.toString());
        }
        System.out.println(Arrays.toString(scores));
        System.out.println("Player one won " + scores[2] + " times");
        System.out.println("Player two won " + scores[0] + " times");
        System.out.println("They drew      " + scores[1] + " times");
        System.exit(1);

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

    private static int deathMatch(Board b, Player one, Player two) throws Exception {
        while(!b.isComplete()) {
            // System.out.println("\n\n\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            // System.out.println(b.toString());

            b = b.withMove(one.move(b));
            if (b.hasWon(1, 4) ) {
                System.out.println(b.toString());
                System.out.println("Player one won!");
                return 1;
                // break;
            }
            b = b.invert();

            // System.out.println(b.toString());

            b = b.withMove(two.move(b));
            if (b.hasWon(1, 4)) {
                System.out.println(b.toString());
                System.out.println("Player two won!");
                return -1;
                // break;
            }
            b = b.invert();

            // System.out.println(b.toString());
            // System.out.println("----------------------------------------------------------");
        }

        // System.out.println(b.toString());
        // System.exit(1);
        return 0;
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
