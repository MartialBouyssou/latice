package latice.model.player;

import latice.model.deck.Deck;

public class Player {
    private final String name;
    private final int score;
    private Rack rack;
    private final Deck deck;


    public Player(String name, int score, Deck deck, Rack rack) {
        this.name = name;
        this.score = score;
        this.deck = deck;
        this.rack = rack;

        // TODO: peut être reprendre en init ou factory
        rack.refill(deck);
    }

    public Player(String name, int score, Deck deck) {
        this(name, score, deck, new Rack());
    }

    public void drawTile() {
        rack.addTile(deck.draw());
    }
}
