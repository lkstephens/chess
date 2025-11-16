package ui;

import client.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class GameplayClient implements ChessClient {

    private final ServerFacade server;
    private final String authToken;
    private final String gameName;

    public GameplayClient(ServerFacade server, String authToken, String gameName) {
        this.server = server;
        this.authToken = authToken;
        this.gameName = gameName;
    }

    @Override
    public String run() {
        System.out.print("\n\n" + help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);

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
        return "";
    }

    @Override
    public String eval(String input) {
        return "";
    }
}
