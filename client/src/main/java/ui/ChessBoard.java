package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoard {

    public static void main(String[] args) {
        System.out.println(drawBoard(new ChessGame(), ChessGame.TeamColor.BLACK));
        System.out.println(drawBoard(new ChessGame(), ChessGame.TeamColor.WHITE));
    }

    public static String drawBoard(ChessGame game, ChessGame.TeamColor bottomColor) {
        String board = "";
        board += letterRow(bottomColor) + "\n";
        switch (bottomColor) {
            case WHITE -> {
                for (int row = 8; row >= 1; row--) {
                    board += pieceRow(game, row, bottomColor) + "\n";
                }
            }
            case BLACK -> {
                for (int row = 1; row <= 8; row++) {
                    board += pieceRow(game, row, bottomColor) + "\n";
                }
            }
        }
        board += letterRow(bottomColor);
        return board;
    }

    static String letterRow(ChessGame.TeamColor bottomColor) {
        String space = "\u2002\u2005\u200a\u200a";
        switch (bottomColor) {
            case WHITE -> {
                return "    \u2009a" + space + "b" + space + "c" + space + "d" + space + "e" + space + "f" + space + "g" + space + "h";
            }
            case BLACK -> {
                return "    \u2009h" + space + "g" + space + "f" + space + "e" + space + "d" + space + "c" + space + "b" + space + "a";
            }
        }
        return "";
    }

    static String pieceRow(ChessGame game, int row, ChessGame.TeamColor bottomColor) {
        chess.ChessBoard board = game.getBoard();
        String rowString = "";
        rowString += " " + row + " |";

        switch (bottomColor) {
            case BLACK -> {
                for (int col = 8; col >= 1; col--) {
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(position);
                    rowString += unicodePiece(piece);
                    rowString += "|";
                }
            }
            case WHITE -> {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(position);
                    rowString += unicodePiece(piece);
                    rowString += "|";
                }
            }
        }
        rowString += " " + row + " ";

        return rowString;
    }

    static String unicodePiece(ChessPiece piece) {
        if (piece != null) {
            switch (piece.getTeamColor()) {
                case WHITE -> {
                    switch (piece.getPieceType()) {
                        case PAWN -> {
                            return EscapeSequences.WHITE_PAWN;
                        }
                        case ROOK -> {
                            return EscapeSequences.WHITE_ROOK;
                        }
                        case KNIGHT -> {
                            return EscapeSequences.WHITE_KNIGHT;
                        }
                        case BISHOP -> {
                            return EscapeSequences.WHITE_BISHOP;
                        }
                        case QUEEN -> {
                            return EscapeSequences.WHITE_QUEEN;
                        }
                        case KING -> {
                            return EscapeSequences.WHITE_KING;
                        }
                    }
                }
                case BLACK -> {
                    switch (piece.getPieceType()) {
                        case PAWN -> {
                            return EscapeSequences.BLACK_PAWN;
                        }
                        case ROOK -> {
                            return EscapeSequences.BLACK_ROOK;
                        }
                        case KNIGHT -> {
                            return EscapeSequences.BLACK_KNIGHT;
                        }
                        case BISHOP -> {
                            return EscapeSequences.BLACK_BISHOP;
                        }
                        case QUEEN -> {
                            return EscapeSequences.BLACK_QUEEN;
                        }
                        case KING -> {
                            return EscapeSequences.BLACK_KING;
                        }
                    }
                }
            }
        }

        return EscapeSequences.EMPTY;
    }
}
