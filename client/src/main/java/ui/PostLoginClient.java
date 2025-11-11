package ui;


import client.ClientNetworkException;
import client.ClientServerException;
import client.ClientUnauthorizedException;
import client.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginClient implements ChessClient {

    private final ServerFacade server;
    private String authToken;

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
                System.out.print(result);

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
        return result;
    }

    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED IN] " + ">>> ");
    }

    public String help() {
        return """
               create <GAMENAME> - a game
               list - games
               join <game#> [WHITE|BLACK] - a game
               observe <game#> - a game
               logout - when you are done
               quit - playing chess
               help - with possible commands
               """;
    }

    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            //case "create" -> createGame(params);
            //case "list" -> listGames();
            //case "join" -> joinGame(params);
            //case "observe" -> observeGame(params);
            case "logout" -> logout();
            case "quit" -> "quit";
            default -> SET_TEXT_COLOR_BLUE + help();
        };
    }

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
}
