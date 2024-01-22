package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = new HashSet<ChessMove>();

        // invalid if no piece at this position
        if (piece == null) return validMoves;

        // Calculate all moves for the piece at startPosition
        Collection<ChessMove> allMovesForPiece = piece.pieceMoves(board, startPosition);

        // Filter out moves that would put the team in check/checkmate
        // (make move, check if in check, undo move)
        for (ChessMove m : allMovesForPiece) {
            // make move
            ChessPiece pieceAtTarget = board.getPiece(m.getEndPosition());
            board.addPiece(m.getEndPosition(), piece);
            board.addPiece(m.getStartPosition(), null);

            // check if in check
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(m);
            }

            // undo move
            board.addPiece(m.getStartPosition(), piece);
            board.addPiece(m.getEndPosition(), pieceAtTarget);

        }

        // Return the valid moves
        System.out.println(validMoves);
        return validMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (getTeamTurn() != piece.getTeamColor()) {
            throw new InvalidMoveException("Not the corresponding team's turn");
        }

        Collection<ChessMove> validMovesForPiece = validMoves(move.getStartPosition());

        if (!validMovesForPiece.contains(move)) {
            throw new InvalidMoveException("Piece cannot move there or leaves the king in danger");
        }

        // if tests pass, make move
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);

        // promote piece
        if (move.getPromotionPiece() != null) piece.setPieceType(move.getPromotionPiece());

        // change team color
        setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // for all pieces on the board that belong to the other team
        // get all possible moves
        // if any of them are current team's king, given team is in check

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                    for (ChessMove move : moves) {
                        ChessPiece target = board.getPiece(move.getEndPosition());
                        if (target != null && target.getPieceType() == ChessPiece.PieceType.KING) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // check if team is in check
        if (!isInCheck(teamColor)) return false;

        // if king has no valid moves, team is in checkmate
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    Collection<ChessMove> validMovesForPiece = validMoves(pos);
                    return validMovesForPiece.isEmpty();
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // for all pieces on given team
        // if any have valid moves, team is not in stalemate

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) return false;
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
