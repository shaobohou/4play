package net.swiftkey.fourplay.bots;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the registry of bots available from the command line.
 * 
 * Add your bot, along with some sensible alias to the map below
 * to make it available as a -b flag from the CLI!
 *
 */
public class Bots {
	public static Map<String, Player> sBots = new HashMap<String, Player>();
	
	static {
		sBots.put("idiot", new IdiotPlayer());
                sBots.put("shaobou_random", new RandomPlayer(10000, 10));
                sBots.put("shaobou_minimax", new MinimaxPlayer(3));  
                sBots.put("shaobo_betterrandom", new BetterRandomLearningPlayer(200, 32));              
                sBots.put("checkall", new GroupBCheckAllPlayer(2));
        sBots.put("checkall3", new GroupBCheckAllPlayer(3));	
 sBots.put("group_a", new GroupABot());
       }
	
}
