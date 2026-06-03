package latice.view;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MenuView {
    private final StackPane startMenuStackpane;
    private final BorderPane gameBoardPane;
    private final VBox playMenu;
    private final VBox usernameMenu;
    private final VBox rulesMenu;
    private final VBox themeMenu;
    private final VBox lanMenu;
    private final VBox searchLanMenu;
    private final VBox lanLobbyMenu;

    /**
     * Creates the menu view
     * @param startMenuStackpane The root menu stack.
     * @param gameBoardPane The game board pane.
     * @param playMenu The main play menu.
     * @param usernameMenu The username selection menu.
     * @param rulesMenu The rules menu.
     * @param themeMenu The theme selection menu.
     * @param lanMenu The LAN menu.
     * @param searchLanMenu The LAN search menu.
     * @param lanLobbyMenu The LAN lobby menu.
     */
    public MenuView(StackPane startMenuStackpane, BorderPane gameBoardPane, VBox playMenu,
                    VBox usernameMenu, VBox rulesMenu, VBox themeMenu, VBox lanMenu,
                    VBox searchLanMenu, VBox lanLobbyMenu) {
        this.startMenuStackpane = startMenuStackpane;
        this.gameBoardPane = gameBoardPane;
        this.playMenu = playMenu;
        this.usernameMenu = usernameMenu;
        this.rulesMenu = rulesMenu;
        this.themeMenu = themeMenu;
        this.lanMenu = lanMenu;
        this.searchLanMenu = searchLanMenu;
        this.lanLobbyMenu = lanLobbyMenu;
    }

    /**
     * Show the username selection menu
     */
    public void showUsernameChoiceMenu() {
        showOnly(usernameMenu);
    }

    /**
     * Show the main play menu
     */
    public void showPlayMenu() {
        showOnly(playMenu);
    }

    /**
     * Show the LAN menu
     */
    public void showLanMenu() {
        showOnly(lanMenu);
    }

    /**
     * show the LAN search menu
     */
    public void showLanSearchMenu() {
        showOnly(searchLanMenu);
    }

    /**
     * Show the LAN lobby menu
     */
    public void showLanLobbyMenu() {
        showOnly(lanLobbyMenu);
    }

    /**
     * Show the rules menu
     */
    public void showRulesMenu() {
        showOnly(rulesMenu);
    }

    /**
     * Show the theme menu
     */
    public void showThemeMenu() {
        showOnly(themeMenu);
    }

    /**
     * Show the game board and hides the start menu
     */
    public void showGameBoard() {
        setVisibleManaged(startMenuStackpane, false);
        setVisibleManaged(gameBoardPane, true);
    }

    /**
     * Show the start menu and hides the game board
     */
    public void showStartMenu() {
        setVisibleManaged(gameBoardPane, false);
        setVisibleManaged(startMenuStackpane, true);
    }

    // this docstring is done with AI
    /**
     * Toggles the visibility and layout management of the UI menus to display only the selected one
     * @param selectedMenu selectedMenu The specific {@code VBox} menu pane that should be
     * brought into focus.
     */
    private void showOnly(VBox selectedMenu) {
        setVisibleManaged(playMenu, playMenu == selectedMenu);
        setVisibleManaged(usernameMenu, usernameMenu == selectedMenu);
        setVisibleManaged(rulesMenu, rulesMenu == selectedMenu);
        setVisibleManaged(themeMenu, themeMenu == selectedMenu);
        setVisibleManaged(lanMenu, lanMenu == selectedMenu);
        setVisibleManaged(searchLanMenu, searchLanMenu == selectedMenu);
        setVisibleManaged(lanLobbyMenu, lanLobbyMenu == selectedMenu);
    }

    // this docstring is done with AI
    /**
     * Updates both the visibility and layout management status of a UI node.
     * @param node The UI component to update (safely ignores {@code null}).
     * @param visible visible {@code true} to render and include the node in the layout;
     * {@code false} to hide it and reclaim its layout space.
     */
    private void setVisibleManaged(Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }
}
