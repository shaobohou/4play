package net.swiftkey.fourplay.bots;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.swiftkey.fourplay.Board;

public class GroupABot implements Player {

    @Override
    public int move(Board b) {
        try {
            //find immediate ways of winning or potential losses
            for (int i = 0; i < b.countRows(); i++) {
                if(validMove(b, i)) {
                    boolean lose = b.willWin(i, 4, -1);
                    boolean hasSpace = (b.queryCell(i, b.nextRow(i))==0);
                    if (b.inBounds(i, b.nextRow(i))&&hasSpace&&(b.willWin(i, 4, 1)||lose)) {
                        return i;
                    }
                }
            }
            
            Map<Integer, Integer> scoresMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < b.countCols(); i++) {
                if (validMove(b,i)) {
                    scoresMap.put(i, score(i,b,2));
                }
            }
            int maxKey = 0;
            int maxValue = 0;
            for (Entry<Integer, Integer> s : scoresMap.entrySet()) {
                if (s.getValue() > maxValue) {
                    maxKey = s.getKey();
                    maxValue = s.getValue();
                }
            }
            
            return maxKey;
        } catch (Exception e) {
            e.printStackTrace();
        }       

        System.out.println("RANDOM MOVE!");
        return randomValidMove(b);
    }
    
    private boolean validMove(Board b, int i) {
        return b.nextRow(i) != -1;
    }
    
    private int chainScores(Board b, int i, int depth) throws Exception {
        int ourMove = 0;
        int theirMove = 0;

        for (int chainLength = 4; chainLength > 1; chainLength--) {
            if(validMove(b, i) && b.willWin(i, chainLength, 1)) {
                ourMove += Math.pow(chainLength, 2);
            }
            
            if(validMove(b, i) && b.willWin(i, chainLength, -1)) {
                theirMove += Math.pow(chainLength, 2); 
            }
        }
        
        if (depth > 0) {
            int acc = 0;
            for (int col = 0; col < b.countCols(); col++) {
                for (int x = 0; x < b.countCols(); ++x) {
                    if(validMove(b, col)) {
                        acc += chainScores(b.withOpponentMove(col), x, depth - 1);
                    }
                }
            }
            
            return ourMove + theirMove + acc;
        } else {
            return ourMove + theirMove;
        }
    }
    
    private int score(int i, Board b, int d) throws Exception {
        return chainScores(b,i,d);
    }
    
    private int randomValidMove(Board b) {
        int row = -1;
        int col = -1;
        Random mRandom = new Random();
        
        while (row == -1) {
            col = mRandom.nextInt(b.countCols());
            row = b.nextRow(col);
        }
        
        return col;
    }

}
