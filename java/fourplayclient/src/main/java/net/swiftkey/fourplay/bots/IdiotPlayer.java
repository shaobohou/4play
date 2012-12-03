package net.swiftkey.fourplay.bots;

import java.util.Random;

import net.swiftkey.fourplay.Board;

public class IdiotPlayer implements Player {

	Random mRandom = new Random();
	
	@Override
	public int move(Board b) {
		return mRandom.nextInt(b.countCols());
	}

}
