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

    /**
     * creates the overlays
     * @param screenOverlay The local turn transition overlay.
     * @param overlayPlayerLabel The label showing the next player.
     * @param endTurnButton The button to disable while an overlay is visible.
     * @param lastPlayerLabel The label showing the previous player.
     * @param lastPlayerScoreLabel The label showing the previous turn score.
     * @param turnNumberLabel The label showing the current turn number.
     * @param lanScreenOverlay The LAN waiting overlay.
     * @param turnOfLabel The label showing whose turn it is in LAN mode.
     */
    public TurnOverlayView(StackPane screenOverlay, Label overlayPlayerLabel,
                           Button endTurnButton, Label lastPlayerLabel,
                           Label lastPlayerScoreLabel, Label turnNumberLabel,
                           StackPane lanScreenOverlay, Label turnOfLabel) {
        this.screenOverlay = Objects.requireNonNull(screenOverlay);
        this.overlayPlayerLabel = Objects.requireNonNull(overlayPlayerLabel);
        this.endTurnButton = Objects.requireNonNull(endTurnButton);
        this.lastPlayerLabel = Objects.requireNonNull(lastPlayerLabel);
        this.lastPlayerScoreLabel = Objects.requireNonNull(lastPlayerScoreLabel);
        this.turnNumberLabel = Objects.requireNonNull(turnNumberLabel);
        this.lanScreenOverlay = lanScreenOverlay;
        this.turnOfLabel = turnOfLabel;
    }

    /**
     * Show the transition overlay between two player turns
     * @param game the active game
     * @param nextPlayer the player whose turn starts
     * @param lastPlayer the player whose turn ended
     * @param lastTurnScore the score earned during the previous turn
     */
    public void showTurnOverlay(Game game, Player nextPlayer, Player lastPlayer,
                                int lastTurnScore) {
        LangManager lang = LangManager.getInstance();

        endTurnButton.setDisable(true);
        overlayPlayerLabel.setText(lang.format("menu.turnIndicator.currentPlayer",
                "name", nextPlayer.getName())
        );
        lastPlayerLabel.setText(lang.format("menu.turnIndicator.lastPlayerScoreHeader",
                "name", lastPlayer.getName())
        );
        lastPlayerScoreLabel.setText(Integer.toString(lastTurnScore));
        turnNumberLabel.setText(lang.format("menu.turnIndicator.turnIndicatorTitle",
                "turnNumber",
                (game.getTurnCount() / game.getPlayers().size() + 1) + "/" + Game.MAX_CYCLES)
        );

        showTurnOverlay();
    }

    /**
     * Hide the transition overlay and unlocks the end turn button
     */
    public void hideTurnOverlay() {
        setOverlayVisible(screenOverlay, false);
        endTurnButton.setDisable(false);
    }

    /**
     * Show the transition overlay and locks the end turn button
     */
    public void showTurnOverlay() {
        setOverlayVisible(screenOverlay, true);
        endTurnButton.setDisable(true);
    }

    /**
     * Show the LAN waiting overlay
     * @param currentPlayerName The player whose turn is active
     */
    public void showLanWaitingOverlay(String currentPlayerName) {
        if (lanScreenOverlay != null && turnOfLabel != null) {
            turnOfLabel.setText(LangManager.getInstance().format("menu.lanOverlay.turnOf",
                    "currentPlayer", currentPlayerName));
            setOverlayVisible(lanScreenOverlay, true);
        }
    }

    /**
     * Hides the LAN waiting overlay
     */
    public void hideLanWaitingOverlay() {
        if (lanScreenOverlay != null) {
            setOverlayVisible(lanScreenOverlay, false);
        }
    }

    /**
     * Shows an invalid move popup
     * @param reason The rejection reason to display
     */
    public void showInvalidMove(String reason) {
        LangManager lang = LangManager.getInstance();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(lang.get("popup.game.invalid.title"));
        alert.setHeaderText(lang.get("popup.game.invalid.header"));
        alert.setContentText(reason);
        alert.show();
    }

    /**
     * Shows the end-of-game popup
     * @param player The winning player, or null for a tie
     */
    public void showGameOver(Player player) {
        hideTurnOverlay();
        LangManager lang = LangManager.getInstance();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(lang.get("popup.game.over.title"));
        if (player != null) {
            alert.setHeaderText(lang.format("popup.game.over.winner.header",
                    "name", player.getName())
            );
            alert.setContentText(lang.format("popup.game.over.winner.content",
                    "name", player.getName(),
                    "score", String.valueOf(player.getScore()))
            );
        } else {
            alert.setHeaderText(lang.get("popup.game.over.tie.header"));
            alert.setContentText(lang.get("popup.game.over.tie.content"));
        }
        alert.show();
    }

    // TODO: docstring
    /**
     * @param overlay
     * @param visible
     */
    private void setOverlayVisible(StackPane overlay, boolean visible) {
        overlay.setVisible(visible);
        overlay.setManaged(visible);
    }
}
