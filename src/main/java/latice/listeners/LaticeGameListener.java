package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.model.player.Player;
import latice.utils.LangManager;
import latice.view.GameView;
import latice.view.controller.GameController;

public class LaticeGameListener implements GameListener {

    private final GameView gameView;
    private final boolean isLanMode;

    /**
     * Creates a game listener bound to the given view.
     *
     * @param gameView  The view to update in response to game events.
     * @param isLanMode Whether the game is running in LAN multiplayer mode.
     */
    public LaticeGameListener(GameView gameView, boolean isLanMode) {
        this.gameView = gameView;
        this.isLanMode = isLanMode;
    }

    /**
     * Returns whether the given player is controlled by the AI.
     *
     * @param player The player to check.
     * @return {@code true} if the player's name matches the AI player name, {@code false} otherwise.
     */
    private boolean isAI(Player player) {
        return player != null && player.getName().equals(LangManager.getInstance().get("menu.username.aiPlayerName"));
    }

    /**
     * Called when the game starts. Updates the rack and player info for the first player.
     *
     * @param event The game started event.
     */
    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameView.updateRack(event.getPlayer());
        gameView.updatePlayerInfo(event.getPlayer());
    }

    /**
     * Called when a player buys an extra action. Updates the player info
     * and disables the action button if a play-twice action was purchased.
     *
     * @param event The action bought event.
     */
    @Override
    public void onActionBought(ActionBoughtEvent event) {
        gameView.updatePlayerInfo(event.getPlayer());
        if (event.getActionType() == ActionType.PLAY_TWICE) {
            gameView.setActionButtonDisabled(true);
        }
    }

    /**
     * Called when a player's turn ends. Resets placement state and shows
     * the turn overlay for the next player, or stores pending overlay info in LAN mode.
     *
     * @param event The turn ended event.
     */
    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        gameView.setHasAlreadyPlaced(false);
        gameView.setActionButtonDisabled(false);
        if (gameView instanceof GameController gc) {
            gc.setAiActionBoughtThisTurn(false);
        }

        if (isLanMode) {
            gameView.setPendingOverlayInfo(event.getLastPlayer(), event.getLastTurnScore());
        } else {
            gameView.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                    event.getLastTurnScore());
            gameView.updateRack(event.getNextPlayer());
            gameView.updatePlayerInfo(event.getNextPlayer());
        }
    }

    /**
     * Called when a tile is successfully placed on the board. Updates the board,
     * player info, and rack. Also triggers the AI turn if applicable.
     *
     * @param event The tile placed event.
     */
    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        gameView.placeTileOnBoard(
                event.getTile(),
                event.getX(),
                event.getY(),
                event.getPlayer()
        );
        gameView.updatePlayerInfo(event.getPlayer());
        gameView.updateRack(event.getPlayer());

        if (!isLanMode) {
            boolean hasRemainingAction = gameView.hasRemainingAction();
            gameView.setHasAlreadyPlaced(!hasRemainingAction);
            if (hasRemainingAction) {
                gameView.setActionButtonDisabled(false);
            }

            if (isAI(event.getPlayer()) && gameView instanceof GameController gc) {
                gc.triggerAITurnIfNeeded();
            }
        }
    }

    /**
     * Called when a player passes their turn. Resets placement state and shows
     * the turn overlay, or stores pending overlay info in LAN mode.
     *
     * @param event The turn passed event.
     */
    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        gameView.setHasAlreadyPlaced(false);
        gameView.setActionButtonDisabled(false);
        if (gameView instanceof GameController gc) {
            gc.setAiActionBoughtThisTurn(false);
        }

        if (isLanMode) {
            gameView.setPendingOverlayInfo(event.getLastPlayer(), event.getLastTurnScore());
        } else {
            gameView.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                    event.getLastTurnScore());
            gameView.updateRack(event.getNextPlayer());
            gameView.updatePlayerInfo(event.getNextPlayer());
        }
    }

    /**
     * Called when a player's rack is exchanged. Updates the rack view
     * and triggers the AI turn if applicable.
     *
     * @param event The rack exchanged event.
     */
    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        gameView.updateRack(event.getPlayer());
        if (!isLanMode) {
            boolean hasRemainingAction = gameView.hasRemainingAction();
            gameView.setHasAlreadyPlaced(!hasRemainingAction);
            if (hasRemainingAction) {
                gameView.setActionButtonDisabled(false);
            }

            if (isAI(event.getPlayer()) && gameView instanceof GameController gc) {
                gc.triggerAITurnIfNeeded();
            }
        }
    }

    /**
     * Called when the game is over. Shows the game over screen and ends the game.
     *
     * @param event The game over event.
     */
    @Override
    public void onGameOver(GameOverEvent event) {
        gameView.showGameOver(event.getWinner());
        gameView.endGame();
    }

    /**
     * Called when a player attempts an invalid move. Shows an error popup
     * only if the player is not the AI.
     *
     * @param event The invalid move event.
     */
    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        if (!isAI(event.getPlayer())) {
            // gameView.showInvalidMove(event.getReason());
        }
    }

    /**
     * Called when a player cannot afford to buy an action. Shows an error popup
     * only if the player is not the AI.
     *
     * @param event The cannot afford event.
     */
    @Override
    public void onCannotAfford(CannotAffordEvent event) {
        if (!isAI(event.getPlayer())) {
            gameView.showInvalidMove(event.getReason());
        }
    }
}