package latice.model.game.events;

import latice.model.player.Player;

public class GameOverEvent implements GameEvent {
    private final Player winner;


    public GameOverEvent(Player winner) {
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }
}
