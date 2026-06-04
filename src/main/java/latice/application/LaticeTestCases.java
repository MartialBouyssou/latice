package latice.application;

import latice.listeners.ConsoleListener;
import latice.model.board.Board;
import latice.model.game.Game;
import latice.model.referee.Referee;
import latice.model.referee.rule.*;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;

import static latice.application.LaticeApplicationConsole.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LaticeTestCases {

	/**
	 * Entry point that runs the manual test scenarios for the board and game.
	 * @param args The command line arguments (unused).
	 */
	public static void main(String[] args) {
		testBoardCreation();
		testBoardCellTypes();
		testBoardDisplay();

		displayHeader();
		Game game = getNewGame();
		eventExampleInGameContext(game);

		testGetTileImgPath();
	}

	/**
	 * Creates a new game configured with the standard set of rules and a
	 * console listener attached.
	 * @return A newly created game ready to be started.
	 */
	private static Game getNewGame() {
		List<IRule> rules = new ArrayList<>();
		rules.add(new MatchingRule());
		rules.add(new AdjacencyRule());
		rules.add(new PositionRule());
		rules.add(new FirstTurnRule());

		Game game = new Game(Referee.create(rules));
		game.addListener(new ConsoleListener());

		return game;
	}

	/**
	 * Runs an interactive example turn, reading coordinates from standard input
	 * and placing tiles until an action is played, then ending the turn.
	 * @param game The game in which the example turn is played.
	 */
	private static void eventExampleInGameContext(Game game) {
		game.startGame(); // here is call onGameStarted from ConsoleListener
		message(game.getBoard().toString());

		//  first turn
		Scanner scanner = new Scanner(System.in);

		do {
			System.out.print("Enter x coordinate where you want to place a tile: ");
			int x = scanner.nextInt();
			System.out.print("Enter y coordinate where you want to place a tile: ");
			int y = scanner.nextInt();

			Tile playedTile = game.getCurrentPlayer().getRack().getTiles().get(0);
			game.playTile(playedTile, x, y); // here is call onTilePlayed from ConsoleListener

		} while (!game.isActionPlayedThisTurn());

		game.endTurn(); // here is call onTurnEnded from ConsoleListener

		message(game.getBoard().toString());
	}

	/**
	 * Tests board creation and prints the type of a few representative cells.
	 */
	static void testBoardCreation() {
		titre("BOARD CREATE");
		Board board = Board.create();
		message("Board created: " + (board != null));
		message("Cell (4,4) - MoonCell: " + board.getCell(4, 4).getClass().getSimpleName());
		message("Cell (0,0) - SunCell: " + board.getCell(0, 0).getClass().getSimpleName());
		message("Cell (1,0) - NormalCell: " + board.getCell(1, 0).getClass().getSimpleName());
	}

	/**
	 * Tests the board by counting the number of sun, moon and normal cells
	 * and checking that every cell is empty.
	 */
	static void testBoardCellTypes() {
		titre("BOARD CELL TYPES");
		Board board = Board.create();

		int sunCount = 0;
		int moonCount = 0;
		int normalCount = 0;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				String type = board.getCell(i, j).getClass().getSimpleName();
				switch (type) {
					case "SunCell" -> sunCount++;
					case "MoonCell" -> moonCount++;
					default -> normalCount++;
				}
			}
		}

		message("SunCells  : " + sunCount + " (expected 16)");
		message("MoonCells : " + moonCount + " (expected 1)");
		message("NormalCells: " + normalCount + " (expected 64)");
		message("All cells empty: " + allEmpty(board));
	}

	/**
	 * Tests the board display by creating a board and printing it to the console.
	 */
	static void testBoardDisplay() {
		titre("BOARD DISPLAY");
		Board board = Board.create();
		displayBoard(board);
	}

	static boolean allEmpty(Board board) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (!board.getCell(i, j).isEmpty()) return false;
			}
		}
		return true;
	}

	static void testGetTileImgPath(){
		Tile tile = new Tile(Shape.BIRD, Color.GREEN);
		message(tile.getTileImgPath());
	}
}