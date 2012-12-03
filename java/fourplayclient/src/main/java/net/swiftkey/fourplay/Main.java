package net.swiftkey.fourplay;


public class Main {
	public static void main(String[] args) {
		GameLoop game = new GameLoop(new ServiceStub("localhost", 3000), new IdiotPlayer(), "Idiot Player");
		game.play(10);
	}
}
