package net.swiftkey.fourplay;

import java.util.Random;

public class IdiotPlayer implements Player {

    Random mRandom = new Random();
	
    @Override
    public int move(Board b) {
        int move = mRandom.nextInt(b.countCols());
        while(b.nextRow(move)<0) {
            move = mRandom.nextInt(b.countCols());
        }

        return move;
    }

}
