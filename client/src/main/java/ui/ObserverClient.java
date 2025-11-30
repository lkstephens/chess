package ui;

import client.ServerFacade;
import datamodel.GameData;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ObserverClient extends BaseGameClient implements ChessClient {

    public ObserverClient(GameData gameData, String authToken, ServerFacade server) {
        super(gameData, server, authToken);
    }

    @Override
    public String run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave") && !gameIsOver) {
            printPrompt();
            String line = scanner.nextLine();

            if (!gameIsOver) {
                break;
            }

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
        System.out.print("\n" + RESET_TEXT_COLOR + "[OBSERVING " + gameName + "] >>> ");
    }

    @Override
    public String help() {
        return
                RESET_TEXT_COLOR
              + "Type"
              + SET_TEXT_COLOR_BLUE + " leave "
              + RESET_TEXT_COLOR + "to stop observing " + "\n";
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (cmd.equals("leave")) {
            return leave(params);
        } else {
            return "\n" + help();
        }
    }

    public String leave(String... params) {
        if (params.length == 0) {
            webSocket.leave(authToken, gameID);
            webSocket.closeSession();
            return "leave";
        }
        return SET_TEXT_COLOR_RED + "Expected no parameters for \"leave\"";
    }

    @Override
    void drawBoard() {
        System.out.println(PostLoginClient.drawBoardWhite(board));
    }
}
