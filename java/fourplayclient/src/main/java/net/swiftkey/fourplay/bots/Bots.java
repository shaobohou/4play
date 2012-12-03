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
	}
	
}
