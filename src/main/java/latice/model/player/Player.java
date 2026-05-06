package latice.model.player;

import latice.model.deck.Deck;

public class Player {
    private final String name;
    private int score;
    private final Rack rack;
    private final Deck deck;
    private int tilesPlayed;

    private Player(String name, int score, Deck deck, Rack rack) {
        this.name = name;
        this.score = score;
        this.deck = deck;
        this.rack = rack;
        this.tilesPlayed = 0;
    }

    public static Player create(String name, Deck deck) {
        Rack rack = new Rack();
        rack.refill(deck);
        return new Player(name, 0, deck, rack);
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    // V6
    public void addScore(int points) {
        this.score += points;
    }

    // V7
    public boolean canAfford(int cost) {
        return score >= cost;
    }

    // V7
    public void spendScore(int cost) {
        if (!canAfford(cost)) {
            throw new IllegalStateException("Player " + name + " cannot afford cost of " + cost);
        }
        this.score -= cost;
    }

    // V6
    public int getTilesPlayed() {
        return tilesPlayed;
    }

    // V5
    public void incrementTilesPlayed() {
        tilesPlayed++;
    }

    public Rack getRack() {
        return rack;
    }

    public Deck getDeck() {
        return deck;
    }

    public void drawTile() {
        rack.addTile(deck.draw());
    }

    @Override
    public String toString() {
        return name + ":\n - score: " + score + "\n - tilesPlayed: " + tilesPlayed + "\n - rack:\n" + rack;
    }
}
