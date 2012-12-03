package net.swiftkey.fourplay;

import java.util.Random;

import net.swiftkey.fourplay.bots.Bots;
import net.swiftkey.fourplay.bots.Player;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


public class Main {
	
	private static void playGame(CommandLine cmd) {
		String host = cmd.hasOption("s") ? cmd.getOptionValue("s") : "localhost";
		int port = Integer.parseInt(cmd.hasOption("p") ? cmd.getOptionValue("p") : "3000");
		String botName = cmd.hasOption("b") ? cmd.getOptionValue("b") : "idiot";
		int ngames = Integer.parseInt(cmd.hasOption("n") ? cmd.getOptionValue("n") : "100");
		String alias = cmd.hasOption("a") ? cmd.getOptionValue("a") : "anonymous_bot" 
				+ (new Random()).nextInt();

		if (! Bots.sBots.containsKey(botName)) {
			System.err.println("Unknown bot name: " + botName);
			System.err.println("Check net.swiftkey.fourplay.Bots!");
			System.exit(1);
		}
		
		ServiceStub server = new ServiceStub(host, port);
		Player bot = Bots.sBots.get(botName);
		
		// Start the game!
		GameLoop game = new GameLoop(server, bot, alias);
		game.play(ngames);
	}
	
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("h", "help", false, "print this help message");
		options.addOption("s", "server", true, "server host address");
		options.addOption("p", "port", true, "port to connect to");
		options.addOption("b", "bot", true, "name of the bot class to play as, see Bots class");
		options.addOption("n", "ngames", true, "number of games to play");
		options.addOption("a", "alias", true, "the player name to register with the server");
		
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			
			if(cmd.hasOption("h")) {
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("java -jar <jar> -h <server address> -p <port> -b <bot name>",
						     "Start a Connect 4 client session.", 
						     options, 
						     "See net.swiftkey.fourplay.bots.Bots for available bot options.");
				System.exit(0);
			} else {
				playGame(cmd);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
