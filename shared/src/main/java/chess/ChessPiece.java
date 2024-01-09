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
                moves = kingMoves(board, myPosition);
                break;
            case QUEEN:
                moves = queenMoves(board, myPosition);
                break;
            case BISHOP:
                moves = bishopMoves(board, myPosition);
                break;
            case KNIGHT:
//                moves = knightMoves(board, myPosition);
                break;
            case ROOK:
                moves = rookMoves(board, myPosition);
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

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ArrayList<ChessPosition>> lines = new ArrayList<ArrayList<ChessPosition>>();

        // Up
        ArrayList<ChessPosition> lineU = new ArrayList<ChessPosition>();
        ChessPosition posU = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        if (posU.isInBounds()) lineU.add(posU);
        lines.add(lineU);

        // Down
        ArrayList<ChessPosition> lineD = new ArrayList<ChessPosition>();
        ChessPosition posD = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        if (posD.isInBounds()) lineD.add(posD);
        lines.add(lineD);

        // Left
        ArrayList<ChessPosition> lineL = new ArrayList<ChessPosition>();
        ChessPosition posL = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
        if (posL.isInBounds()) lineL.add(posL);
        lines.add(lineL);

        // Right
        ArrayList<ChessPosition> lineR = new ArrayList<ChessPosition>();
        ChessPosition posR = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
        if (posR.isInBounds()) lineR.add(posR);
        lines.add(lineR);

        // Up Left
        ArrayList<ChessPosition> lineUL = new ArrayList<ChessPosition>();
        ChessPosition posUL = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        if (posUL.isInBounds()) lineUL.add(posUL);
        lines.add(lineUL);

        // Up Right
        ArrayList<ChessPosition> lineUR = new ArrayList<ChessPosition>();
        ChessPosition posUR = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        if (posUR.isInBounds()) lineUR.add(posUR);
        lines.add(lineUR);

        // Down Left
        ArrayList<ChessPosition> lineDL = new ArrayList<ChessPosition>();
        ChessPosition posDL = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
        if (posDL.isInBounds()) lineDL.add(posDL);
        lines.add(lineDL);

        // Down Right
        ArrayList<ChessPosition> lineDR = new ArrayList<ChessPosition>();
        ChessPosition posDR = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        if (posDR.isInBounds()) lineDR.add(posDR);
        lines.add(lineDR);

        Collection<ChessMove> moves = new HashSet<ChessMove>();
        lines.forEach(line -> {
            moves.addAll(processLine(board, myPosition, line));
        });

        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ArrayList<ChessPosition>> lines = new ArrayList<ArrayList<ChessPosition>>();

        // Up
        ArrayList<ChessPosition> lineU = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn());
            if (!newPos.isInBounds()) break;
            lineU.add(newPos);
        }
        lines.add(lineU);

        // Down
        ArrayList<ChessPosition> lineD = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
            if (!newPos.isInBounds()) break;
            lineD.add(newPos);
        }
        lines.add(lineD);

        // Left
        ArrayList<ChessPosition> lineL = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
            if (!newPos.isInBounds()) break;
            lineL.add(newPos);
        }
        lines.add(lineL);

        // Right
        ArrayList<ChessPosition> lineR = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i);
            if (!newPos.isInBounds()) break;
            lineR.add(newPos);
        }
        lines.add(lineR);

        // Up Left
        ArrayList<ChessPosition> lineUL = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
            if (!newPos.isInBounds()) break;
            lineUL.add(newPos);
        }
        lines.add(lineUL);

        // Up Right
        ArrayList<ChessPosition> lineUR = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
            if (!newPos.isInBounds()) break;
            lineUR.add(newPos);
        }
        lines.add(lineUR);

        // Down Left
        ArrayList<ChessPosition> lineDL = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
            if (!newPos.isInBounds()) break;
            lineDL.add(newPos);
        }
        lines.add(lineDL);

        // Down Right
        ArrayList<ChessPosition> lineDR = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
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

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ArrayList<ChessPosition>> lines = new ArrayList<ArrayList<ChessPosition>>();

        ArrayList<ChessPosition> lineU = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn());
            if (!newPos.isInBounds()) break;
            lineU.add(newPos);
        }
        lines.add(lineU);

        ArrayList<ChessPosition> lineD = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
            if (!newPos.isInBounds()) break;
            lineD.add(newPos);
        }
        lines.add(lineD);

        ArrayList<ChessPosition> lineL = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
            if (!newPos.isInBounds()) break;
            lineL.add(newPos);
        }
        lines.add(lineL);

        ArrayList<ChessPosition> lineR = new ArrayList<ChessPosition>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPos = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i);
            if (!newPos.isInBounds()) break;
            lineR.add(newPos);
        }
        lines.add(lineR);

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
