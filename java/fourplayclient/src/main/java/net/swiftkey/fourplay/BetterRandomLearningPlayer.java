package net.swiftkey.fourplay;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class BetterRandomLearningPlayer implements Player {

    public class Move {
        public int[] mCounts;
        public double [] mScores;

        public Move(int[] counts, double[] scores) {
            mCounts = Arrays.copyOf(counts, counts.length);
            mScores = Arrays.copyOf(scores, scores.length);
        }

        public Move accumulate(int[] counts, double[] scores) {
            for(int i = 0; i < counts.length; ++i) {
                mCounts[i] += counts[i];
                mScores[i] += scores[i];
            }

            return new Move(mCounts, mScores);
        }
    }


    Player mTrainer = new MinimaxPlayer(1);
    Random mRandom = new Random();
    int mSamples = 10000;
    int mDepth = 10;

    HashMap<String, Move> cache = new HashMap<String, Move>();

    public BetterRandomLearningPlayer(int samples, int depth) {
        mSamples = samples;
        mDepth = depth;
    }
    
    @Override
    public int move(Board b) {
        double startTime = System.currentTimeMillis();

        Move newMove = null;

        try {
            int[] counts = new int[b.countCols()];
            double[] scores = new double[b.countCols()];

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

            if (cache.containsKey(b.toString())) {
                newMove = cache.get(b.toString()).accumulate(counts, scores);
            } else {
                cache.put(b.toString(), new Move(counts, scores));
            }
        } catch (Exception e) {
            System.out.println("Exception!");
        }

        // System.out.println(Arrays.toString(scores));
        // System.out.println(Arrays.toString(counts));

        newMove = cache.get(b.toString());
        int[] counts = Arrays.copyOf(newMove.mCounts, newMove.mCounts.length);
        double[] scores = Arrays.copyOf(newMove.mScores, newMove.mScores.length);

        // System.out.println(cache.size());
        // System.out.println(Arrays.toString(newMove.mCounts));
        // System.out.println(Arrays.toString(newMove.mScores));

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
        
        return mTrainer.move(b);
    }

    // public int idiotMove(Board b) {
    //     int move = mRandom.nextInt(b.countCols());
    //     while(b.nextRow(move)<0) {
    //         move = mRandom.nextInt(b.countCols());
    //     }

    //     return move;
    // }

    public double simulate(Board b, int depth) throws Exception {
        // assume starting with the opponent
        double score = 0;

        // simulate equal numbers of moves for each player
        for(int d = 0; d < depth*2; ++d) {
            if(b.isComplete()) {
                break;
            }

            // make any valid move
            int nextMove = mTrainer.move(b);
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
