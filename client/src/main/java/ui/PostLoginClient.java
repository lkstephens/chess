package ui;


import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.*;
import datamodel.CreateGameRequest;
import datamodel.JoinGameRequest;
import datamodel.ListGamesResult;

import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginClient implements ChessClient {

    private final ServerFacade server;
    private String authToken;
    private ArrayList<Integer> gameIDs = new ArrayList<>();
    private ArrayList<ChessGame> chessGames = new ArrayList<>();

    public PostLoginClient(ServerFacade server, String authToken) {
        this.server = server;
        this.authToken = authToken;
    }

    public String run() {
        System.out.print("\n\n" + help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit") && !result.equals("logout")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (!result.equals("logout") && !result.equals("quit")) {
                    System.out.print(result);
                }

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        return result;
    }

    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED IN] " + ">>> ");
    }

    public String help() {
        return
                 SET_TEXT_COLOR_BLUE + "create "
               + SET_TEXT_COLOR_MAGENTA + "<GAMENAME>"
               + RESET_TEXT_COLOR + " - a game\n"

               + SET_TEXT_COLOR_BLUE + "list"
               + RESET_TEXT_COLOR + " - games\n"

               + SET_TEXT_COLOR_BLUE + "join "
               + SET_TEXT_COLOR_MAGENTA + "<game#> [WHITE|BLACK]"
               + RESET_TEXT_COLOR + " - a game\n"

               + SET_TEXT_COLOR_BLUE + "observe "
               + SET_TEXT_COLOR_MAGENTA + "<game#>"
               + RESET_TEXT_COLOR + " - a game\n"

               + SET_TEXT_COLOR_BLUE + "logout "
               + RESET_TEXT_COLOR + " - when you are done\n"

               + SET_TEXT_COLOR_BLUE + "quit "
               + RESET_TEXT_COLOR + " - playing chess\n"

               + SET_TEXT_COLOR_BLUE + "help "
               + RESET_TEXT_COLOR + " - with possible commands\n";
    }

    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "join" -> joinGame(params);
            //case "observe" -> observeGame(params);
            case "logout" -> logout();
            case "quit" -> "quit";
            default -> "\n" + help();
        };
    }

    public String createGame(String... params) {
        if (params.length == 1) {
            String gameName = params[0];
            CreateGameRequest request = new CreateGameRequest(gameName);
            try {
                var result = server.createGame(authToken, request);
                return SET_TEXT_COLOR_GREEN + "Successfully created game: " + gameName + "\n";

            } catch (ClientBadRequestException ex) {
                return SET_TEXT_COLOR_RED + "Game name required.";
            } catch (ClientUnauthorizedException ex) {
                return SET_TEXT_COLOR_RED + "Sign in to create a game.";
            } catch (ClientServerException ex) {
                return SET_TEXT_COLOR_RED + "Internal server error. Please try again later.";
            } catch (ClientNetworkException ex) {
                return SET_TEXT_COLOR_RED + "Connection Error. Please try again later.";
            } catch (Exception ex) {
                return SET_TEXT_COLOR_RED + "Unknown Error. Please try again later.";
            }
        } else {
            return SET_TEXT_COLOR_RED + "Expected: <gamename>";
        }
    }

    public String listGames() {
        try {
            ListGamesResult listGames = server.listGames(authToken);
            gameIDs.clear();
            var games = listGames.games();

            if (games.isEmpty()) {
                System.out.println("No existing games. Create a new one!");
            }

            for (int i = 0; i < games.size(); i++) {
                var currGame = games.get(i);
                gameIDs.add(currGame.gameID());
                chessGames.add(currGame.game());

                var whiteUserPrint = (currGame.whiteUsername() == null) ? "" : currGame.whiteUsername();
                var blackUserPrint = (currGame.blackUsername() == null) ? "" : currGame.blackUsername();

                System.out.print((i+1) + ". Game Name: " + currGame.gameName() + "\t\t"
                                       + "White: " + whiteUserPrint + "\t\t"
                                       + "Black: " + blackUserPrint + "\n");
            }

            return "";

        } catch (ClientUnauthorizedException ex) {
            return SET_TEXT_COLOR_RED + "You must sign in before retrieving a list of games.";
        } catch (ClientServerException ex) {
            return SET_TEXT_COLOR_RED + "Internal server error. Please try again later.";
        } catch (ClientNetworkException ex) {
            return SET_TEXT_COLOR_RED + "Connection Error. Please try again later.";
        } catch (Exception ex) {
            return SET_TEXT_COLOR_RED + "Unknown Error. Please try again later.";
        }
    }

    public String joinGame(String...params) {
        if (params.length == 2) {

            int gameNum = Integer.parseInt(params[0]);
            if (gameNum < 1 || gameNum > gameIDs.size()) {
                return SET_TEXT_COLOR_RED + "Game number does not exist. Try a different number.";
            }

            int gameID = gameIDs.get(gameNum - 1);
            String playerColor = params[1].toUpperCase();
            JoinGameRequest request = new JoinGameRequest(playerColor, gameID);

            ChessGame game = chessGames.get(gameNum-1);
            ChessBoard board = game.getBoard();

            try {
                server.joinGame(authToken, request);

                if (playerColor.equals("WHITE")) {
                    System.out.print(drawBoardWhite(board));
                } else {
                    System.out.print(drawBoardWhite(board));
                }

                return SET_TEXT_COLOR_GREEN + "Successfully joined game as " + playerColor + "\n";

            } catch (ClientBadRequestException ex) {
                return SET_TEXT_COLOR_RED + "Make sure to enter a valid game number and player color.";
            } catch (ClientUnauthorizedException ex) {
                return SET_TEXT_COLOR_RED + "You must sign in before joining a game";
            } catch (ClientAlreadyTakenException ex) {
                return SET_TEXT_COLOR_RED + playerColor + " has already been taken.";
            } catch (ClientServerException ex) {
                return SET_TEXT_COLOR_RED + "Internal server error. Please try again later.";
            } catch (ClientNetworkException ex) {
                return SET_TEXT_COLOR_RED + "Connection Error. Please try again later.";
            } catch (Exception ex) {
                return SET_TEXT_COLOR_RED + "Unknown Error. Please try again later.";
            }
        }
        return SET_TEXT_COLOR_RED + "Expected: <game#> <WHITE|BLACK>";
    }

//    public String observeGame(String...params) {
//        if (params.length == 1) {
//            int gameNum = Integer.parseInt(params[0]);
//        } else {
//            return SET_TEXT_COLOR_RED + "Expected: <game#>";
//        }
//    }

    public String logout() {
        try {
            server.logout(authToken);
            return "logout";

        } catch (ClientUnauthorizedException ex) {
            return SET_TEXT_COLOR_RED + "Failed to logout";
        } catch (ClientServerException ex) {
            return SET_TEXT_COLOR_RED + "Internal server error. Please try again later.";
        } catch (ClientNetworkException ex) {
            return SET_TEXT_COLOR_RED + "Connection Error. Please try again later.";
        } catch (Exception ex) {
            return SET_TEXT_COLOR_RED + "Unknown Error. Please try again later.";
        }
    }

    public String drawBoardWhite(ChessBoard board) {

        StringBuilder out = new StringBuilder();
        // i: rows, j: cols
        for (int i = 9; i >= 0; i--) {
            for (int j = 0; j <= 9; j++) {

                if (i == 0 || i == 9) {
                    out.append(RESET_BG_COLOR + RESET_TEXT_COLOR +
                               SET_TEXT_COLOR_BLUE + SET_BG_COLOR_LIGHT_GREY +
                               EMPTY + " a " + " b " + " c " + " d " + " e " + " f " + " g " + " h " + EMPTY + "\n");
                    break;

                } else {

                    if (j == 0 || j == 9) {
                        out.append(RESET_BG_COLOR + RESET_TEXT_COLOR +
                                SET_TEXT_COLOR_BLUE + SET_BG_COLOR_LIGHT_GREY);
                        out.append(" ");
                        out.append(8 - i + 1);
                        out.append(" ");
                    } else {
                        ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                        ChessGame.TeamColor color = piece.getTeamColor();
                        ChessPiece.PieceType type = piece.getPieceType();
                    }
                    if (j == 9) {
                        out.append("\n");
                    }
                }
            }
        }

//                SET_BG_COLOR_BROWN + BLACK_KNIGHT +
//                SET_BG_COLOR_TAN + BLACK_BISHOP +
//                SET_BG_COLOR_BROWN + BLACK_QUEEN +
//                SET_BG_COLOR_TAN + BLACK_KING +
//                SET_BG_COLOR_BROWN + BLACK_BISHOP +
//                SET_BG_COLOR_TAN + BLACK_KNIGHT +
//                SET_BG_COLOR_BROWN + BLACK_ROOK +
//                SET_BG_COLOR_LIGHT_GREY + " 8 " + RESET_BG_COLOR + "\n" +
//
//                SET_TEXT_COLOR_BLUE + SET_BG_COLOR_LIGHT_GREY + " 7 " + SET_TEXT_COLOR_BLACK +
//                SET_BG_COLOR_BROWN + BLACK_PAWN +
//                SET_BG_COLOR_TAN + BLACK_PAWN +
//                SET_BG_COLOR_BROWN + BLACK_PAWN +
//                SET_BG_COLOR_TAN + BLACK_PAWN +
//                SET_BG_COLOR_BROWN + BLACK_PAWN +
//                SET_BG_COLOR_TAN + BLACK_PAWN +
//                SET_BG_COLOR_BROWN + BLACK_PAWN +
//                SET_BG_COLOR_TAN + BLACK_PAWN + RESET_BG_COLOR + " 7 " + RESET_BG_COLOR + "\n" +
//
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR + "\n" +
//
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR + "\n" +
//
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR + "\n" +
//
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + EMPTY + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + EMPTY + RESET_BG_COLOR + "\n" +
//
//                SET_TEXT_COLOR_WHITE + SET_BG_COLOR_TAN + WHITE_PAWN + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + WHITE_PAWN + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + WHITE_PAWN + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + WHITE_PAWN + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + WHITE_PAWN + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + WHITE_PAWN + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + WHITE_PAWN + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + WHITE_PAWN + RESET_BG_COLOR + "\n" +
//
//                SET_BG_COLOR_BROWN + WHITE_ROOK + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + WHITE_KNIGHT + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + WHITE_BISHOP + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + WHITE_QUEEN + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + WHITE_KING + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + WHITE_BISHOP + RESET_BG_COLOR +
//                SET_BG_COLOR_BROWN + WHITE_KNIGHT + RESET_BG_COLOR +
//                SET_BG_COLOR_TAN + WHITE_ROOK + RESET_BG_COLOR + "\n";

        return "CHANGEME";
    }

//    public String getPieceUnicode(ChessPiece piece) {
//
//    }
}
