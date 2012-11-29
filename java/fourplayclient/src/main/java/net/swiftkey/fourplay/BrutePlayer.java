package net.swiftkey.fourplay;

import java.util.Random;
import java.util.ArrayList;

public class BrutePlayer implements Player {

    Random mRandom = new Random();
    int mDepth = 0;

    public BrutePlayer(int depth) {
        mDepth = depth;
    }
	
    @Override
    public int move(Board b) {
        
        try {
            double startTime = System.currentTimeMillis();
            
            int bestRow = -1;
            double bestScore = -1.0;
            double worstScore = 1.0;
            ArrayList<Integer> moves = new ArrayList<Integer>();
            ArrayList<Double> scores = new ArrayList<Double>();
            if(mDepth>0) {
                for (int row = 0; row < b.countCols(); ++row) {
                    if (b.nextRow(row)>=0) {
                        double score = scoreMove(b, row, mDepth);

                        moves.add(row);
                        scores.add(score);
                        
                        if(score > bestScore) {
                            bestScore = score;
                            bestRow = row;
                        }

                        if(score < worstScore) {
                            worstScore = score;
                        }
                    }
                }
            }

            double finishTime = System.currentTimeMillis();

            // System.out.println(moves.toString());
            // System.out.println(scores.toString());
            
            // System.out.println("best score = " + bestScore);
            // System.out.println("took " + (finishTime-startTime));

            if(bestRow>=0) {
                if(java.lang.Math.abs(bestScore-worstScore)<1e-3) {
                    return moves.get(mRandom.nextInt(moves.size()));
                } else {
                    return bestRow;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in BrutePlayer");
            // do nothing
        }

        int move = mRandom.nextInt(b.countCols());
        while(b.nextRow(move)<0) {
            move = mRandom.nextInt(b.countCols());
        }
            
        return move;
    }

    public double scoreMove(Board b, int row, int depth) throws Exception {
        // win
        if(b.willWin(row, 4, 1)) {
            return 1.0;
        }

        // not lose
        if(b.opponentMovePlayerLose(row, 4, 1)) {
            // System.out.println("will lose");
            return 0.5;
        }

        // lose if opponent has no non-winning moves
        ArrayList<Integer> otherMoves = simulateOpponent(b.withMove(row));
        // System.out.println(row + " has " + otherMoves.size());
        if(otherMoves.size()==0) {
            return 0.0;
        }

        double score = 0.0;
        for(Integer otherRow : otherMoves) {
            score += scoreBoard(b.withMove(row).withOpponentMove(otherRow), depth-1);
        }

        score /= otherMoves.size();
        return score;
    }

    public double scoreBoard(Board b, int depth) throws Exception {
        if(depth<1) {
            return 0.1;
        }

        int count = 0;
        double score = 0.0;
        for (int r = 0; r < b.countCols(); ++r) {
            if (b.nextRow(r)>=0) {
                score += scoreMove(b, r, depth);
                count++;
            }
        }

        if(count>0) {
            return score/count;
        } else {
            return 0.0;
        }
    }

    // non-winning opponent moves
    public ArrayList<Integer> simulateOpponent(Board b) throws Exception {
        // System.out.println(b.toString());
        ArrayList<Integer> moves = new ArrayList<Integer>();
        for (int r = 0; r < b.countCols(); ++r) {
            if (b.nextRow(r)>=0) {
                if (b.opponentMovePlayerWin(r, 4, -1)) { // opponent wins
                    // System.out.println(b.withOpponentMove(r));
                    return new ArrayList<Integer>();
                }

                moves.add(r);
            }
        }

        return moves;
    }
}
