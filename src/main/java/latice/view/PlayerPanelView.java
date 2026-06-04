package latice.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import latice.model.player.Player;
import latice.model.tile.Tile;
import latice.utils.LangManager;

import java.util.List;
import java.util.Objects;

public class PlayerPanelView {
    private final List<ImageView> rackImageViews;
    private final Label currentPlayerLabel;
    private final Label currentPlayerScoreLabel;
    private final Label playerTileRemainingLabel;
    private final Button actionButton;

    /**
     * creates the panel view
     * @param rackImageViews the image slots used to render the rack
     * @param currentPlayerLabel the label showing the active player name
     * @param currentPlayerScoreLabel the label showing the active player score
     * @param playerTileRemainingLabel the label showing remaining tiles
     * @param actionButton the button used to buy an extra action
     */
    public PlayerPanelView(List<ImageView> rackImageViews, Label currentPlayerLabel,
                           Label currentPlayerScoreLabel, Label playerTileRemainingLabel,
                           Button actionButton) {
        this.rackImageViews = List.copyOf(rackImageViews);
        this.currentPlayerLabel = Objects.requireNonNull(currentPlayerLabel);
        this.currentPlayerScoreLabel = Objects.requireNonNull(currentPlayerScoreLabel);
        this.playerTileRemainingLabel = Objects.requireNonNull(playerTileRemainingLabel);
        this.actionButton = Objects.requireNonNull(actionButton);
    }

    /**
     * Refreshe the rack images of the player.
     *
     * @param player The player whose rack must be displayed.
     */
    public void updateRack(Player player) {
        List<Tile> tiles = player.getRack().getTiles();
        for (int i = 0; i < rackImageViews.size(); i++) {
            ImageView rackImageView = rackImageViews.get(i);
            if (i < tiles.size()) {
                rackImageView.setImage(new Image(tiles.get(i).getTileImgPath()));
                rackImageView.setVisible(true);
            } else {
                rackImageView.setVisible(false);
            }
        }
    }

    /**
     * Refreshe the displayed information of the player
     * @param player player whose information will be displayed
     */
    public void updatePlayerInfo(Player player) {
        int remainingTiles = player.getDeck().size() + player.getRack().getTiles().size();
        LangManager lang = LangManager.getInstance();

        currentPlayerLabel.setText(lang.format("menu.game.player.currentPlayer",
                "name", player.getName()));
        currentPlayerScoreLabel.setText(lang.format("menu.game.player.score",
                "score", String.valueOf(player.getScore()))
        );
        playerTileRemainingLabel.setText(lang.format("menu.game.player.remaining",
                "count", String.valueOf(remainingTiles))
        );
    }

    /**
     * Enables or Disables the action button
     * @param disabled true when the action button must be disabled
     */
    public void setActionButtonDisabled(boolean disabled) {
        actionButton.setDisable(disabled);
    }
}
