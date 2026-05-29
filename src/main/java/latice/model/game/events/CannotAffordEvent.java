package latice.model.game.events;

public class CannotAffordEvent implements GameEvent {
    private final String reason;


    public CannotAffordEvent(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
