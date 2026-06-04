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

    public LaticeGameListener(GameView gameView, boolean isLanMode) {
        this.gameView = gameView;
        this.isLanMode = isLanMode;
    }

    private boolean isAI(Player player) {
        return player != null && player.getName().equals(LangManager.getInstance().get("menu.username.aiPlayerName"));
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameView.updateRack(event.getPlayer());
        gameView.updatePlayerInfo(event.getPlayer());
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {
        gameView.updatePlayerInfo(event.getPlayer());
        if (event.getActionType() == ActionType.PLAY_TWICE) {
            gameView.setActionButtonDisabled(true);
        }
    }

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

    @Override
    public void onGameOver(GameOverEvent event) {
        gameView.showGameOver(event.getWinner());
        gameView.endGame();
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        if (!isAI(event.getPlayer())) {
            // gameView.showInvalidMove(event.getReason());
        }
    }

    @Override
    public void onCannotAfford(CannotAffordEvent event) {
        gameView.showInvalidMove(event.getReason());
    }
}