package latice.model.player;

import latice.model.deck.Deck;

public class Player {
    private final String name;
    private int score;
    private final Rack rack;
    private final Deck deck;
    private int tilesPlayed;

    /**
     * Creates a player with the given attributes and no tiles played.
     * @param name The name of the player.
     * @param score The initial score of the player.
     * @param deck The deck assigned to the player.
     * @param rack The rack assigned to the player.
     */
    private Player(String name, int score, Deck deck, Rack rack) {
        this.name = name;
        this.score = score;
        this.deck = deck;
        this.rack = rack;
        this.tilesPlayed = 0;
    }

    /**
     * Creates a new player with a starting score of zero and a refilled rack.
     * @param name The name of the player.
     * @param deck The deck used to fill the player's rack.
     * @return A new player initialized with the given name and deck.
     */
    public static Player create(String name, Deck deck) {
        Rack rack = new Rack();
        rack.refill(deck);
        return new Player(name, 0, deck, rack);
    }

    /**
     * Returns the name of the player.
     * @return The player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current score of the player.
     * @return The player's score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Adds the given number of points to the player's score.
     * @param points The number of points to add.
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Checks whether the player has enough score to cover the given cost.
     * @param cost The cost to check against the player's score.
     * @return True if the player's score is at least the cost, false otherwise.
     */
    public boolean canAfford(int cost) {
        return score >= cost;
    }

    /**
     * Deducts the given cost from the player's score.
     * @param cost The amount of score to spend.
     */
    public void spendScore(int cost) {
        if (!canAfford(cost)) {
            throw new IllegalStateException("Player " + name + " cannot afford cost of " + cost);
        }
        this.score -= cost;
    }

    /**
     * Returns the number of tiles the player has played.
     * @return The count of tiles played.
     */
    public int getTilesPlayed() {
        return tilesPlayed;
    }

    /**
     * Increments the number of tiles the player has played by one.
     */
    public void incrementTilesPlayed() {
        tilesPlayed++;
    }

    /**
     * Returns the rack belonging to the player.
     * @return The player's rack.
     */
    public Rack getRack() {
        return rack;
    }

    /**
     * Returns the deck belonging to the player.
     * @return The player's deck.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Draws a tile from the player's deck and adds it to the player's rack.
     */
    public void drawTile() {
        rack.addTile(deck.draw());
    }

    /**
     * Returns a textual representation of the player, including score, tiles played and rack.
     * @return A string describing the player's state.
     */
    @Override
    public String toString() {
        return name + ":\n - score: " + score + "\n - tilesPlayed: " + tilesPlayed + "\n - rack:\n" + rack;
    }
}
