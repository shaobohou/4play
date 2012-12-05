package net.swiftkey.fourplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.swiftkey.fourplay.bots.Player;

public class TrainingGame {
    
    private final List<Player> mPlayers;
    private final Random mRandom = new Random();
    
    public TrainingGame(Player bot, Player opponent) {
        mPlayers = new ArrayList<Player>();
        mPlayers.add(bot);
        mPlayers.add(opponent);
    }
    
    private void summary(int played, int win, int lose, int draw) {
        System.out.println(String.format("%s vs. %s - results:",
                mPlayers.get(0).getClass().getSimpleName(),
                mPlayers.get(1).getClass().getSimpleName()));
        System.out.println("Played: " + --played);
        System.out.println("Won:    " + win);
        System.out.println("Lost:   " + lose);
        System.out.println("Draw:   " + draw);
    }
    
    public void play(int gamesToPlay) {
        int played = 0, win = 0, lose = 0, draw = 0;
        int next = -1;
        Board state = null;
        
        while (played <= gamesToPlay) {
            if (next == -1 
                    || state == null 
                    || state.hasWon(1, 4)
                    || state.hasWon(-1, 4)
                    || state.isDraw(4)) {
                if (state != null) {
                    if(state.isDraw(4)) ++draw;
                    if(state.hasWon(1, 4)) ++win;
                    if(state.hasWon(-1, 4)) ++lose;
                }
                
                next = mRandom.nextInt(mPlayers.size());
                state = Board.empty(8, 8);
                ++played;
                
            } else if (next == 0) {
                try {
                    state = state.withMove(mPlayers.get(next).move(state));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            } else if (next == 1) {
                try {
                    state = state.withOpponentMove(mPlayers.get(next).move(state.invert()));
                } catch (Exception e) {
                    System.out.println(state.invert().toString());
                    e.printStackTrace();
                }
                
            }
            
            next = (next + 1) % mPlayers.size();
        }
        
        summary(played, win, lose, draw);
    }
}
