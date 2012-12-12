package net.swiftkey.fourplay.bots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.swiftkey.fourplay.Board;

public class GroupBCheckAllPlayer implements Player {
	Player mIdiot = new IdiotPlayer();
	Random mRandom = new Random();
	int mDepth = 3;

	private static int cacheDepth = 5;

	public GroupBCheckAllPlayer(int depth) {
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
//            System.out.println("Exception!");
            e.printStackTrace();
        }

        return mIdiot.move(b);
	}
	
	int search(Board b, int depth, Map<String, Integer> cache) throws Exception {
        if(depth<=0) {
        	return getHeuristic(b);
        }

        for (int col = 0; col < b.countCols(); ++col) {
            if (b.nextRow(col)>=0) {
                if (b.willWin(col, 4, 1)) {
                    return 10000;
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
            return scores[best];
        }

        return 0;
    }
	
	private int getHeuristic(Board b)
	{
		int n = 0;
		//horizontal
		for(int x = 0; x <= 4; x++) 
		{
			for(int y = 0; y < b.countCols(); y++) {
				n+=getConnRow(b, x, y);
			}
		}
		
		//vertical
		for(int x = 0; x < b.countRows(); x++) 
		{
			for(int y = 0; y <= 4; y++) {
				n+=getConnCol(b, x, y);
			}
		}
		
		//back diagonal
		for(int x = 3; x < b.countCols(); x++) 
		{
			for(int y = 0; y <= 4; y++) {
				n+=getConnBackDiag(b, x, y);
			}
		}
		
		//fwd diagonal
		for(int x = 0; x <= 4; x++) 
		{
			for(int y = 0; y <= 4; y++) {
				n+=getConnBackDiag(b, x, y);
			}
		}
		
		return n;//(int) Math.pow(n, 3);
	}
	
	private static int getConnRow(Board b, int x, int y)
	{
		if (x > 4)
			return 0;
		
		int[] row = b.getRow(y);
		int[] ns = new int[4];
		
		for (int i = x; i < x + 4; ++i)
		{
			ns[i - x] = row[x];
		}
		return getNumber(ns);
	}

	private static int getConnCol(Board b, int x, int y)
	{
		if (x > 4)
			return 0;
		
		int[] row = b.getColumn(x);
		int[] ns = new int[4];
		
		for (int i = y; i < y + 4; ++i)
		{
			ns[i - y] = row[y];
		}
		return getNumber(ns);
	}
	
	private static int getConnBackDiag(Board b, int x, int y)
	{
		int[] ns = new int[4];
		ns[0] = b.queryCell(x, y);
		ns[1] = b.queryCell(x-1, y+1);
		ns[2] = b.queryCell(x-2, y+2);
		ns[3] = b.queryCell(x-3, y+3);
		return getNumber(ns);
		
	}
	
	private static int getConnFwdDiag(Board b, int x, int y)
	{
		int[] ns = new int[4];
		ns[0] = b.queryCell(x, y);
		ns[1] = b.queryCell(x+1, y+1);
		ns[2] = b.queryCell(x+2, y+2);
		ns[3] = b.queryCell(x+3, y+3);
		return getNumber(ns);
		
	}
	
	private static int getNumber(int[] ns) {
		int n = 0;
		for (int i = 0; i < ns.length; ++i)
		{
			if (ns[i] == 1)
			{
				if(n<0) {
					return 0;
				} 
				else {
					n++;
				}
			} 
			else if (ns[i] == -1) {
				if(n>0) {
					return 0;
				} 
				else {
					n--;
				}
			}
		}
		
		return n;
//		return n > 0 ? n * n : -n * n;
//		return (int) Math.pow(n,3);
	}
}
