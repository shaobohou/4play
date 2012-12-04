package net.swiftkey.fourplay.bots;

import java.util.Random;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import net.swiftkey.fourplay.Board;

public class MinimaxPlayer implements Player {

    Player mIdiot = new IdiotPlayer();
    Random mRandom = new Random();
    int mDepth = 3;

    private static int cacheDepth = 5;

    public MinimaxPlayer(int depth) {
        mDepth = depth;
    }
	
    @Override
    public int move(Board b) {
        double startTime = System.currentTimeMillis();

        int[] scores = new int[b.countCols()];
        Map<String, Integer> cache = new HashMap<String, Integer>(10000);

        try {
            // find all valid moves
            int best = -1;
            for (int col = 0; col < b.countCols(); ++col) {
                scores[col] = -999;
                if (b.nextRow(col)>=0) {
                    // take the winning move
                    if(b.willWin(col, 4, 1)) {
                        // System.out.println("Found a winning move at column " + col);
                        return col;
                    }

                    // minimax search
                    scores[col] = -search(b.withMove(col).invert(), mDepth*2-1, cache);
                    if (best<0 || scores[col]>scores[best]) {
                        best = col;
                    }
                }
            }

            // System.out.println(Arrays.toString(scores));
            // double finishTime = System.currentTimeMillis();
            // System.out.println("took " + (finishTime-startTime));

            ArrayList<Integer> moves = new ArrayList<Integer>();
            if (best>=0) {
                for (int col = 0; col < b.countCols(); ++col) {
                    if (scores[col]==scores[best]) {
                        moves.add(col);
                    }
                }

                return moves.get(mRandom.nextInt(moves.size()));
            }
        } catch (Exception e) {
            System.out.println("Exception!");
        }

        return mIdiot.move(b);
    }


    int search(Board b, int depth, Map<String, Integer> cache) throws Exception {
        if(depth<=0) {
            return 0;
        }

        // if((mDepth*2-depth)<=cacheDepth) {
        //     String boardKey = b.toString();
        //     String otherKey = b.invert().toString();

        //     Integer cachedScore = null;
        //     if((cachedScore=cache.get(boardKey))!=null) {
        //         return  cachedScore;
        //     } else if((cachedScore=cache.get(otherKey))!=null) {
        //         return -cachedScore;
        //     }
        // }

        for (int col = 0; col < b.countCols(); ++col) {
            if (b.nextRow(col)>=0) {
                if (b.willWin(col, 4, 1)) {
                    return 100;
                }
            }
        }

        int best = -1;
        int[] scores = new int[b.countCols()];
        for (int col = 0; col < b.countCols(); ++col) {
            scores[col] = -999;
            if (b.nextRow(col)>=0) {
                scores[col] = -search(b.withMove(col).invert(), depth-1, cache);
                if (best<0 || scores[col]>scores[best]) {
                    best = col;
                }
            }
        }

        if (best>=0) {
            // if((mDepth*2-depth)<=cacheDepth) {
            //     if(!cache.containsKey(b.toString()) && !cache.containsKey(b.invert().toString())) {
            //         cache.put(b.toString(), scores[best]);
            //     }
            // }

            return scores[best];
        }

        return 0;
    }
}
