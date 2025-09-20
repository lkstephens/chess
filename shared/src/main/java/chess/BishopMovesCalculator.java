package chess;

import java.util.Collection;

public class BishopMovesCalculator extends BaseMovesCalculator {

    public BishopMovesCalculator(int[][] directions, boolean distIteration) {
        super(directions, distIteration);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return super.pieceMoves(board,myPosition);
    }

}


