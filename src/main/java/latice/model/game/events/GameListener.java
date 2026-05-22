package latice.model.game.events;

public interface GameListener {
    void onTilePlaced(TilePlacedEvent event);
    void onTurnEnded(TurnEndedEvent event);
    void onGameOver(GameOverEvent event);
    void onInvalidMove(InvalidMoveEvent event);
    void onRackExchanged(RackExchangedEvent event);
    void onTurnPassed(TurnPassedEvent event);
    void onGameStarted(GameStartedEvent event);
    void onActionBought(ActionBoughtEvent event);
}
