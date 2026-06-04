package latice.view;

import latice.model.player.Player;
import latice.model.tile.Tile;

public interface GameView {

    /**
     * Refreshe the displayed rack of the player
     * @param player player whose rack will be displayed
     */
    void updateRack(Player player);

    /**
     * Refreshe the displayed information of the player
     * @param player player whose information will be displayed
     */
    void updatePlayerInfo(Player player);

    /**
     * Displays a tile placed on the board
     * @param tile The ONE placed tile
     * @param targetX the board column
     * @param targetY the board row
     * @param player the player who placed the tile
     */
    void placeTileOnBoard(Tile tile, int targetX, int targetY, Player player);

    /**
     * Shows the turn transition overlay
     * @param nextPlayer player whose turn starts
     * @param lastPlayer player whose turn ended
     * @param lastTurnScore score earned during the previous turn
     */
    void showOverlayForPlayer(Player nextPlayer, Player lastPlayer, int lastTurnScore);

    /**
     * Stores the next overlay information until shown
     * @param lastPlayer player whose turn ended
     * @param lastTurnScore score earned during the previous turn
     */
    void setPendingOverlayInfo(Player lastPlayer, int lastTurnScore);

    /**
     * Shows an invalid move message
     * @param reason the rejection reason to display
     */
    void showInvalidMove(String reason);

    /**
     * Shows the game over message
     * @param player The winning player, or null for a tie
     */
    void showGameOver(Player player);

    /**
     * Ends the current UI game
     */
    void endGame();

    /**
     * Updates whether the active player has already placed a tile in the turn
     * @param value true when a tile was already placed
     */
    void setHasAlreadyPlaced(boolean value);

    /**
     * Enables or Disables the action button
     * @param disabled true when the action button must be disabled
     */
    void setActionButtonDisabled(boolean disabled);

    /**
     * Tells whether the active game still allows an action for the player
     * @return True when there is still another action
     */
    boolean hasRemainingAction();
}
