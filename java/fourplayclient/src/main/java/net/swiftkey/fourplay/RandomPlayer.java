package net.swiftkey.fourplay;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class RandomPlayer implements Player {

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
            ArrayList<Integer> validCols = new ArrayList<Integer>();
            for (int col = 0; col < b.countCols(); ++col) {
                if (b.nextRow(col)>=0) {
                    validCols.add(col);
                }
            }

            // simulate moves
            for (int i = 0; i < mSamples; ++i) {
                int col = validCols.get(mRandom.nextInt(validCols.size()));
                scores[col] += simulate(b.withMove(col), mDepth);
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
                scores[col] /= counts[col];
                total += counts[col];
                if(best<0 || scores[col]>scores[best]) {
                    best = col;
                }
            }
        }
        System.out.println(Arrays.toString(scores));

        double finishTime = System.currentTimeMillis();
        System.out.println("took " + (finishTime-startTime) + " for " + total + " iterations");

        if(best>=0) {
            return best;
        }
        
        return mRandom.nextInt(b.countCols());
    }

    public double simulate(Board b, int depth) throws Exception {
        for(int d = 0; d < depth; ++d) {
            if(b.isComplete()) break;

            // player move
            int nextMove = mRandom.nextInt(b.countCols());
            while(b.nextRow(nextMove)<0) { nextMove = mRandom.nextInt(b.countCols()); }
            if (b.willWin(nextMove, 4, 1)) { return 1.0; }
            b = b.withMove(nextMove);

            if(b.isComplete()) break;

            // opponent move
            nextMove = mRandom.nextInt(b.countCols());
            while(b.nextRow(nextMove)<0) { nextMove = mRandom.nextInt(b.countCols()); }
            if (b.opponentMovePlayerLose(nextMove, 4, 1)) { return 0.0; }
            b = b.withOpponentMove(nextMove);
        }

        // System.out.println(b.toString());

        return 0.5;
    }
}
