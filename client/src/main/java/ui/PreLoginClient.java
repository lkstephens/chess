package ui;

import client.*;
import datamodel.RegisterRequest;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginClient implements ChessClient {

    private final ServerFacade server;
    private boolean isLoggedIn = false;

    public PreLoginClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);

                if (isLoggedIn) {
                    PostLoginClient postLoginClient = new PostLoginClient(server);
                    postLoginClient.run();
                }

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED OUT] " + ">>> ");
    }

    public String help() {
        return """
               register <USERNAME> <PASSWORD> <EMAIL> - to create an account
               login <USERNAME> <PASSWORD> - to play chess
               quit - playing chess
               help - with possible commands
               """;
    }

    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "register" -> register(params);
            //case "login" -> login(params);
            case "quit" -> "quit";
            default -> SET_TEXT_COLOR_BLUE + help();
        };
    }

    public String register(String... params) {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            RegisterRequest request = new RegisterRequest(username, password, email);
            try {
                server.register(request);
                isLoggedIn = true;
                return RESET_TEXT_COLOR + "Successfully registered.";
            } catch (ClientBadRequestException ex) {
                return SET_TEXT_COLOR_RED + "Username, password, and valid email (email@domain.com) required.";
            } catch (ClientAlreadyTakenException ex) {
                return SET_TEXT_COLOR_RED + "Username already in use.";
            } catch (ClientServerException ex) {
                return SET_TEXT_COLOR_RED + "Internal server error. Please try again later.";
            } catch (ClientNetworkException ex) {
                return SET_TEXT_COLOR_RED + "Connection Error. Please try again later.";
            } catch (Exception ex) {
                return SET_TEXT_COLOR_RED + "Unknown Error. Please try again later.";
            }
        } else {
            return SET_TEXT_COLOR_RED + "Expected: <username> <password> <email>";
        }

    }
}
