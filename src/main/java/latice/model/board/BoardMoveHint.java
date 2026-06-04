package latice.model.board;

/**
 * Represents a playable board position and the score earned by placing a tile there.
 *
 * @param x The board column.
 * @param y The board row.
 * @param score The score preview for this move.
 */
public record BoardMoveHint(int x, int y, int score) {
}
