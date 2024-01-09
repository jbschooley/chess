package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = null;
        switch (type) {
            case KING:
//                moves = kingMoves(board, myPosition);
                break;
            case QUEEN:
//                moves = queenMoves(board, myPosition);
                break;
            case BISHOP:
                moves = bishopMoves(board, myPosition);
                break;
            case KNIGHT:
//                moves = knightMoves(board, myPosition);
                break;
            case ROOK:
//                moves = rookMoves(board, myPosition);
                break;
            case PAWN:
//                moves = pawnMoves(board, myPosition);
                break;
        }
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ArrayList<ChessPosition>> lines = new ArrayList<ArrayList<ChessPosition>>();

        ArrayList<ChessPosition> lineUR = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
            if (!newPos.isInBounds()) break;
            lineUR.add(newPos);
        }
        lines.add(lineUR);

        ArrayList<ChessPosition> lineDL = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
            if (!newPos.isInBounds()) break;
            lineDL.add(newPos);
        }
        lines.add(lineDL);

        ArrayList<ChessPosition> lineUL = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
            if (!newPos.isInBounds()) break;
            lineUL.add(newPos);
        }
        lines.add(lineUL);

        ArrayList<ChessPosition> lineDR = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
            if (!newPos.isInBounds()) break;
            lineDR.add(newPos);
        }
        lines.add(lineDR);

        Collection<ChessMove> moves = new HashSet<ChessMove>();
        lines.forEach(line -> {
            moves.addAll(processLine(board, myPosition, line));
        });

        return moves;
    }

    private ChessMove isValidMove(ChessBoard board, ChessPosition myPosition, ChessPosition newPos) {
        if (newPos.getRow() < 1 || newPos.getRow() > 8 || newPos.getColumn() < 1 || newPos.getColumn() > 8) {
            return null;
        }

        if (board.getPiece(newPos) == null) {
            return new ChessMove(myPosition, newPos, null);
        }

        return null;
    }

    private ChessMove isValidCapture(ChessBoard board, ChessPosition myPosition, ChessPosition newPos) {
        if (newPos.getRow() < 1 || newPos.getRow() > 8 || newPos.getColumn() < 1 || newPos.getColumn() > 8) {
            return null;
        }

        if (board.getPiece(newPos) != null) {
            if (board.getPiece(newPos).getTeamColor() != this.getTeamColor()) {
                return new ChessMove(myPosition, newPos, null);
            }
        }
        return null;
    }

    private Collection<ChessMove> processLine(ChessBoard board, ChessPosition myPosition, ArrayList<ChessPosition> line) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        for (ChessPosition pos : line) {
            ChessMove move = isValidMove(board, myPosition, pos);
            if (move != null) {
                moves.add(move);
            } else {
                ChessMove capture = isValidCapture(board, myPosition, pos);
                if (capture != null) {
                    moves.add(capture);
                }
                break;
            }
        };
        return moves;
    }
}
