package net.swiftkey.fourplay;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class RandomPlayer implements Player {

    Player mIdiot = new IdiotPlayer();
    Random mRandom = new Random();
    int mSamples = 10000;
    int mDepth = 10;

    public RandomPlayer(int samples, int depth) {
        mSamples = samples;
        mDepth = depth;
    }
    
    @Override
    public int move(Board b) {
        double startTime = System.currentTimeMillis();

        int[] counts = new int[b.countCols()];
        double[] scores = new double[b.countCols()];

        try {
            // find all valid moves
            ArrayList<Integer> moves = new ArrayList<Integer>();
            for (int col = 0; col < b.countCols(); ++col) {
                if (b.nextRow(col)>=0) {
                    moves.add(col);
                    if (b.willWin(col, 4, 1)) {
                        return col;
                    }
                }
            }

            // simulate moves
            for (int i = 0; i < mSamples; ++i) {
                int col = moves.get(mRandom.nextInt(moves.size()));
                scores[col] += simulate(b.withMove(col).invert(), mDepth);
                ++counts[col];
            }
        } catch (Exception e) {
            System.out.println("Exception!");
        }

        // System.out.println(Arrays.toString(scores));
        // System.out.println(Arrays.toString(counts));

        int best = -1;
        int total = 0;
        for (int col = 0; col < b.countCols(); ++col) {
            if (counts[col]>0) {
                total += counts[col];
                scores[col] /= counts[col];
                if(best<0 || scores[col]>scores[best]) {
                    best = col;
                }
            }
        }

        // double finishTime = System.currentTimeMillis();
        // System.out.println("took " + (finishTime-startTime) + " for " + total + " iterations");
        // System.out.println(Arrays.toString(scores));

        if(best>=0) {
            return best;
        }
        
        return mIdiot.move(b);
    }

    public double simulate(Board b, int depth) throws Exception {
        // assume starting with the opponent
        int score = 0;

        // simulate equal numbers of moves for each player
        for(int d = 0; d < depth*2; ++d) {
            if(b.isComplete()) {
                break;
            }

            // make any valid move
            int nextMove = mIdiot.move(b);
            if (b.willWin(nextMove, 4, 1)) {
                return score;
            }

            // invert the board and the score for the opposing player
            b = b.withMove(nextMove).invert();
            score = 1-score;
        }

        // System.out.println(b.toString());

        return 0.5;
    }
}
