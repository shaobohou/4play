package net.swiftkey.fourplay;

import java.util.Random;

public class IdiotPlayer implements Player {

	Random mRandom = new Random();
	
	@Override
	public int move(Board b) {
		return mRandom.nextInt(b.countCols());
	}

}
