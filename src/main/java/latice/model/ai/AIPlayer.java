package latice.model.ai;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.board.cell.SunCell;
import latice.model.player.Rack;
import latice.model.referee.Referee;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente l'intelligence artificielle du jeu.
 * Évalue les séquences de coups pour maximiser les points et éviter d'avantager l'adversaire.
 */
public class AIPlayer {

    private final Referee referee;

    /**
     * Crée un joueur IA avec l'arbitre donné.
     * * @param referee L'arbitre utilisé pour valider les mouvements et calculer les scores.
     */
    public AIPlayer(Referee referee) {
        this.referee = referee;
    }

    /**
     * Simule l'avenir pour déterminer si acheter une action supplémentaire est rentable
     * dès le début du tour, avec le solde de points actuel (avant toute pose de tuile).
     * * @param board Le plateau de jeu actuel.
     * @param rack Le chevalet contenant les tuiles de l'IA.
     * @param currentScore Le score actuel de l'IA.
     * @param turnsRemaining Le nombre de tours restants dans la partie.
     * @return {@code true} si l'achat d'une action rapporte plus de points ou aide à finir la partie, {@code false} sinon.
     */
    public boolean isDoublePlayWorthIt(Board board, Rack rack, int currentScore, int turnsRemaining) {
        // Si l'IA n'a pas les moyens initiaux ou pas assez de tuiles, ça ne vaut pas le coup
        if (currentScore < Referee.EXTRA_ACTION_COST || rack.getTiles().size() < 2) {
            return false;
        }

        // Simuler le premier mouvement
        AIMove firstMove = findBestMove(board, rack, currentScore, turnsRemaining);
        if (firstMove == null) return false;

        Board simBoard = BoardSimulator.deepCopy(board);
        simBoard.placeTile(firstMove.getTile(), firstMove.getX(), firstMove.getY());
        Rack simRack = copyRackWithout(rack, firstMove.getTile());

        // Simuler le second mouvement possible grâce à l'achat
        AIMove secondMove = findBestMove(simBoard, simRack, currentScore - Referee.EXTRA_ACTION_COST + firstMove.getScore(), turnsRemaining);

        if (secondMove != null) {
            int totalScoreGain = firstMove.getScore() + secondMove.getScore();
            // L'achat est rentable si on regagne au moins les 2 points dépensés,
            // ou si on est à la toute fin de la partie (pour vider son chevalet vite)
            if (totalScoreGain >= 2 || turnsRemaining <= 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Trouve le meilleur mouvement possible pour le plateau et le chevalet actuels.
     * * @param board Le plateau de jeu actuel.
     * @param rack Le chevalet de l'IA.
     * @param currentScore Le score actuel de l'IA.
     * @param turnsRemaining Le nombre de tours restants.
     * @return L'objet {@link AIMove} représentant le meilleur coup, ou {@code null} si aucun coup n'est possible.
     */
    public AIMove findBestMove(Board board, Rack rack, int currentScore, int turnsRemaining) {
        List<AIMove> validMoves = buildValidMoves(board, rack);
        if (validMoves.isEmpty()) {
            return null;
        }

        AIMove bestMove = null;
        int bestEval = Integer.MIN_VALUE;

        for (AIMove move : validMoves) {
            int eval = evaluateMove(board, rack, move, turnsRemaining);
            if (eval > bestEval) {
                bestEval = eval;
                bestMove = move;
            }
        }

        return bestMove != null ? bestMove : validMoves.get(0);
    }

    /**
     * Évalue un mouvement spécifique en lui attribuant un score de pertinence,
     * en prenant en compte les ouvertures dangereuses (ex: offrir une case Soleil à l'adversaire).
     * * @param board Le plateau de jeu.
     * @param rack Le chevalet de l'IA.
     * @param move Le mouvement à évaluer.
     * @param turnsRemaining Le nombre de tours restants.
     * @return Le score d'évaluation calculé.
     */
    private int evaluateMove(Board board, Rack rack, AIMove move, int turnsRemaining) {
        int score = move.getScore();
        int eval = score * 10;

        int openSunsBefore = countOpenSunCells(board);
        Board simBoard = BoardSimulator.deepCopy(board);
        simBoard.placeTile(move.getTile(), move.getX(), move.getY());

        if (turnsRemaining > 2) {
            int openSunsAfter = countOpenSunCells(simBoard);
            int sunsOpened = openSunsAfter - openSunsBefore;

            if (sunsOpened > 0) {
                if (score < 2) {
                    eval -= 10000; // Forte pénalité si on ouvre un soleil pour l'adversaire sans marquer beaucoup
                } else if (sunsOpened > 1) {
                    eval -= 10000;
                } else {
                    eval -= 5;
                }
            }
        }

        return eval;
    }

    /**
     * Compte le nombre de cellules Soleil vides qui sont adjacentes à au moins une tuile posée.
     * Ce sont des cases dangereuses car l'adversaire pourrait y jouer facilement.
     * * @param board Le plateau à analyser.
     * @return Le nombre de cases Soleil exposées.
     */
    private int countOpenSunCells(Board board) {
        int count = 0;
        for (int x = 0; x < Board.GRID_SIZE; x++) {
            for (int y = 0; y < Board.GRID_SIZE; y++) {
                if (board.getCell(x, y) instanceof SunCell && board.getCell(x, y).isEmpty()) {
                    for (Cell n : board.getNeighbors(x, y)) {
                        if (!n.isEmpty()) {
                            count++;
                            break;
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Construit la liste de tous les mouvements légaux et valides possibles avec le chevalet actuel.
     * * @param board Le plateau de jeu.
     * @param rack Le chevalet contenant les tuiles disponibles.
     * @return Une liste contenant tous les objets {@link AIMove} valides.
     */
    private List<AIMove> buildValidMoves(Board board, Rack rack) {
        List<AIMove> moves = new ArrayList<>();
        for (Tile tile : rack.getTiles()) {
            for (int x = 0; x < Board.GRID_SIZE; x++) {
                for (int y = 0; y < Board.GRID_SIZE; y++) {
                    if (board.getCell(x, y).isEmpty() && referee.isValidMove(board, tile, x, y)) {
                        int score = referee.calculateScore(board, tile, x, y);
                        moves.add(new AIMove(tile, x, y, score));
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Crée une copie indépendante d'un chevalet en y retirant la première occurrence d'une tuile spécifiée.
     * Utilisé pour la simulation des coups successifs.
     * * @param original Le chevalet d'origine.
     * @param played La tuile à retirer.
     * @return Le nouveau chevalet simulé.
     */
    private Rack copyRackWithout(Rack original, Tile played) {
        Rack copy = new Rack();
        boolean removed = false;
        for (Tile tile : original.getTiles()) {
            if (!removed && tile.equals(played)) {
                removed = true;
            } else {
                copy.addTile(tile);
            }
        }
        return copy;
    }
}