package latice.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import latice.model.game.Game;
import latice.model.player.Player;
import latice.utils.LangManager;

import java.util.Objects;

public class TurnOverlayView {
    private final StackPane screenOverlay;
    private final Label overlayPlayerLabel;
    private final Button endTurnButton;
    private final Label lastPlayerLabel;
    private final Label lastPlayerScoreLabel;
    private final Label turnNumberLabel;
    private final StackPane lanScreenOverlay;
    private final Label turnOfLabel;
    private final Button overlayButton;

    public TurnOverlayView(StackPane screenOverlay, Label overlayPlayerLabel,
                           Button endTurnButton, Label lastPlayerLabel,
                           Label lastPlayerScoreLabel, Label turnNumberLabel,
                           StackPane lanScreenOverlay, Label turnOfLabel,
                           Button overlayButton) {
        this.screenOverlay = Objects.requireNonNull(screenOverlay);
        this.overlayPlayerLabel = Objects.requireNonNull(overlayPlayerLabel);
        this.endTurnButton = Objects.requireNonNull(endTurnButton);
        this.lastPlayerLabel = Objects.requireNonNull(lastPlayerLabel);
        this.lastPlayerScoreLabel = Objects.requireNonNull(lastPlayerScoreLabel);
        this.turnNumberLabel = Objects.requireNonNull(turnNumberLabel);
        this.lanScreenOverlay = lanScreenOverlay;
        this.turnOfLabel = turnOfLabel;
        this.overlayButton = overlayButton;
    }

    public void showTurnOverlay(Game game, Player nextPlayer, Player lastPlayer, int lastTurnScore) {
        LangManager lang = LangManager.getInstance();
        endTurnButton.setDisable(true);
        overlayPlayerLabel.setText(lang.format("menu.turnIndicator.currentPlayer", "name", nextPlayer.getName()));
        lastPlayerLabel.setText(lang.format("menu.turnIndicator.lastPlayerScoreHeader", "name", lastPlayer.getName()));
        lastPlayerScoreLabel.setText(Integer.toString(lastTurnScore));
        turnNumberLabel.setText(lang.format("menu.turnIndicator.turnIndicatorTitle", "turnNumber", (game.getTurnCount() / game.getPlayers().size() + 1) + "/" + Game.MAX_CYCLES));
        showTurnOverlay();
    }

    public void hideTurnOverlay() {
        setOverlayVisible(screenOverlay, false);
        endTurnButton.setDisable(false);
    }

    public void showTurnOverlay() {
        setOverlayVisible(screenOverlay, true);
        endTurnButton.setDisable(true);
    }

    public void showLanWaitingOverlay(String currentPlayerName) {
        if (lanScreenOverlay != null && turnOfLabel != null) {
            turnOfLabel.setText(LangManager.getInstance().format("menu.lanOverlay.turnOf", "currentPlayer", currentPlayerName));
            setOverlayVisible(lanScreenOverlay, true);
        }
    }

    public void hideLanWaitingOverlay() {
        if (lanScreenOverlay != null) {
            setOverlayVisible(lanScreenOverlay, false);
        }
    }

    public void showInvalidMove(String reason) {
        LangManager lang = LangManager.getInstance();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(lang.get("popup.game.invalid.title"));
        alert.setHeaderText(lang.get("popup.game.invalid.header"));
        alert.setContentText(reason);
        alert.show();
    }

    public void showGameOver(Player player) {
        hideTurnOverlay();
        LangManager lang = LangManager.getInstance();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(lang.get("popup.game.over.title"));
        if (player != null) {
            alert.setHeaderText(lang.format("popup.game.over.winner.header", "name", player.getName()));
            alert.setContentText(lang.format("popup.game.over.winner.content", "name", player.getName(), "score", String.valueOf(player.getScore())));
        } else {
            alert.setHeaderText(lang.get("popup.game.over.tie.header"));
            alert.setContentText(lang.get("popup.game.over.tie.content"));
        }
        alert.show();
    }

    public void setReadyButtonVisible(boolean visible) {
        if (overlayButton != null) {
            overlayButton.setVisible(visible);
            overlayButton.setManaged(visible);
        }
    }

    private void setOverlayVisible(StackPane overlay, boolean visible) {
        overlay.setVisible(visible);
        overlay.setManaged(visible);
    }
}