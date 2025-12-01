package ui;

import chess.ChessPosition;
import client.ServerFacade;
import datamodel.GameData;

import java.util.*;

import static ui.EscapeSequences.*;

public class ObserverClient extends BaseGameClient implements ChessClient {

    public ObserverClient(GameData gameData, String authToken, ServerFacade server) {
        super(gameData, server, authToken);
    }

    @Override
    public String run() {
        String result = repl();
        if (result.equals("gameover")) {
            return "leave";
        }
        return result;
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[OBSERVING " + gameName + "] >>> ");
    }

    @Override
    public String help() {
        return
                SET_TEXT_COLOR_BLUE + "redraw"
              + RESET_TEXT_COLOR + " - chess board\n"

              + SET_TEXT_COLOR_BLUE + "highlight "
              + SET_TEXT_COLOR_MAGENTA + "<a-h1-8>"
              + RESET_TEXT_COLOR + " - legal moves\n"

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
            case "leave" -> leave(params);
            default -> "\n" + help();
        };
    }

    public String redrawBoard(String... params) {
        if (params.length == 0) {
            System.out.println();
            return PostLoginClient.drawBoardWhite(board);
        }

        return SET_TEXT_COLOR_RED + "Expected no parameters for \"redraw\".";
    }

    @Override
    void drawBoard() {
        System.out.println(PostLoginClient.drawBoardWhite(board));
    }

    @Override
    String drawBoardHighlight(ChessPosition[] highlightPosArray) {
        return PostLoginClient.drawBoardWhite(board, highlightPosArray);
    }
}
