package latice.model.game.events;

import latice.model.player.ActionType;
import latice.model.player.Player;

public class ActionBoughtEvent implements GameEvent {
    public Player player;
    public ActionType actionType;

    public ActionBoughtEvent(Player player, ActionType actionType) {
        this.player = player;
        this.actionType = actionType;
    }

    public Player getPlayer() {
        return player;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
