package net.swiftkey.fourplay.bots;

import java.util.Random;
import net.swiftkey.fourplay.Board;
public class IdiotPlayer implements Player {

	Random mRandom = new Random();
	
	@Override
	public int move(Board b) {
	    int row = -1;
	    int col = -1;
	    
	    while (row == -1) {
	        col = mRandom.nextInt(b.countCols());
	        row = b.nextRow(col);
	    }
	    
		return col;
	}

}
