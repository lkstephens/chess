package ui;

import chess.ChessBoard;
import client.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class GameplayClient implements ChessClient {

    private final ServerFacade server;
    private final String authToken;
    private final String gameName;
    private ChessBoard board;
    private final String color;

    public GameplayClient(ChessBoard board, String gameName, String color, ServerFacade server, String authToken) {
        this.board = board;
        this.gameName = gameName;
        this.color = color;
        this.server = server;
        this.authToken = authToken;
    }

    @Override
    public String run() {
        System.out.print("\n" + help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (!result.equals("leave")) {
                    System.out.print(result);
                }

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        return result;
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "["+gameName+"] " + ">>> ");
    }

    @Override
    public String help() {
        return
                SET_TEXT_COLOR_BLUE + "redraw"
              + RESET_TEXT_COLOR + " - chess board\n"

              + SET_TEXT_COLOR_BLUE + "highlight "
              + SET_TEXT_COLOR_MAGENTA + "<a1>"
              + RESET_TEXT_COLOR + " - legal moves\n"

              + SET_TEXT_COLOR_BLUE + "makemove "
              + SET_TEXT_COLOR_MAGENTA + "<a1> <a1>"
              + RESET_TEXT_COLOR + " - to make a move\n"

              + SET_TEXT_COLOR_BLUE + "resign"
              + RESET_TEXT_COLOR + " - to forfeit game\n"

              + SET_TEXT_COLOR_BLUE + "leave"
              + RESET_TEXT_COLOR + " - game\n"

              + SET_TEXT_COLOR_BLUE + "help "
              + RESET_TEXT_COLOR + " - with possible commands\n";
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "redraw" -> redrawBoard(params);
            case "highlight" -> highlight(params);
            case "makemove" -> makeMove(params);
            case "resign" -> resign(params);
            case "leave" -> leave(params);
            default -> "\n" + help();
        };
    }

    public String redrawBoard(String... params) {
        if (params.length == 0) {
            if (color.equals("WHITE")) {
                return PostLoginClient.drawBoardWhite(board);
            } else {
                return PostLoginClient.drawBoardBlack(board);
            }
        }

        return SET_TEXT_COLOR_RED + "Expected no parameters for \"redraw\".";
    }

    public String highlight(String... params) {
        return "highlighting moves";
    }

    public String makeMove(String... params) {
        return "making a move";
    }

    public String resign(String... params) {
        return "resigning";
    }

    public String leave(String... params) {
        // does this need to remove the user from the game?
        return "leave";
    }
}
