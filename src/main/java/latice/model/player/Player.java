package latice.model.player;

import latice.model.deck.Deck;

public class Player {
    private final String name;
    private final int score;
    private final Rack rack;
    private final Deck deck;

    private Player(String name, int score, Deck deck, Rack rack) {
        this.name = name;
        this.score = score;
        this.deck = deck;
        this.rack = rack;
    }

    public static Player create(String name, Deck deck) {
        Rack rack = Rack.create();
        rack.refill(deck);
        return new Player(name, 0, deck, rack);
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public Rack getRack() {
        return rack;
    }

    public Deck getDeck() {
        return deck;
    }

    @Override
    public String toString() {
        return name + ":\n - score: " + score + "\n - rack:\n" + rack;
    }

    public void drawTile() {
        rack.addTile(deck.draw());
    }
}
