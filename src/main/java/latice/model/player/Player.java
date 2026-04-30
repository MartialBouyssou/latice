package latice.model.player;

import latice.model.deck.Deck;

public class Player {
    private final String name;
    private final int score;
    private Rack rack;
    private final Deck deck;


    private Player(String name, int score, Deck deck, Rack rack) {
        this.name = name;
        this.score = score;
        this.deck = deck;
        this.rack = rack;
    }

    public static Player create(String name, Deck deck) {
        Rack rack = new Rack();
        rack.refill(deck);
        return new Player(name, 0, deck, rack);
    }

    public Player(String name, int score, Deck deck) {
        this(name, score, deck, new Rack());
    }

    public void drawTile() {
        rack.addTile(deck.draw());
    }
}
