package ui;


import client.*;
import datamodel.CreateGameRequest;

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
            //case "list" -> listGames();
            //case "join" -> joinGame(params);
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
