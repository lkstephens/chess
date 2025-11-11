package ui;

import client.*;
import datamodel.LoginRequest;
import datamodel.RegisterRequest;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.State.*;

public class PreLoginClient implements ChessClient {

    private final ServerFacade server;
    private String authToken;
    private State state = LOGGED_OUT;

    public PreLoginClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public String run() {
        System.out.println("\n♕ Welcome to 240 Chess. Type help to get started. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);

                if (state == LOGGED_IN) {
                    PostLoginClient postLoginClient = new PostLoginClient(server, authToken);
                    result = eval(postLoginClient.run());

                    state = LOGGED_OUT;

                    if (result.equals("logout")) {
                        System.out.print(help());
                    }
                }

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
        return "\nGoodbye.";
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED OUT] " + ">>> ");
    }

    public String help() {

        return    SET_TEXT_COLOR_BLUE + "register "
                + SET_TEXT_COLOR_MAGENTA + "<USERNAME> <PASSWORD> <EMAIL>"
                + RESET_TEXT_COLOR + " - to create an account\n"

                + SET_TEXT_COLOR_BLUE + "login "
                + SET_TEXT_COLOR_MAGENTA + "<USERNAME> <PASSWORD>"
                + RESET_TEXT_COLOR + " - to play chess\n"

                + SET_TEXT_COLOR_BLUE + "quit"
                + RESET_TEXT_COLOR + " - playing chess\n"

                + SET_TEXT_COLOR_BLUE + "help"
                + RESET_TEXT_COLOR + " - with possible commands\n";
     }

     public String eval(String input) {
         String[] tokens = input.toLowerCase().split(" ");
         String cmd = (tokens.length > 0) ? tokens[0] : "help";
         String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

         return switch (cmd) {
             case "register" -> register(params);
             case "login" -> login(params);
             case "quit" -> "quit";
             default -> help();
         };
     }

     public String register(String... params) {
         if (params.length == 3) {
             String username = params[0];
             String password = params[1];
             String email = params[2];
             RegisterRequest request = new RegisterRequest(username, password, email);
             try {
                 var result = server.register(request);
                 state = LOGGED_IN;
                 authToken = result.authToken();
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

    public String login(String... params) {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);
            try {
                var result = server.login(request);
                state = LOGGED_IN;
                authToken = result.authToken();
                return RESET_TEXT_COLOR + "Successfully logged in.";

            } catch (ClientBadRequestException ex) {
                return SET_TEXT_COLOR_RED + "Username and password required.";
            } catch (ClientUnauthorizedException ex) {
                return SET_TEXT_COLOR_RED + "Incorrect username or password.";
            } catch (ClientServerException ex) {
                return SET_TEXT_COLOR_RED + "Internal server error. Please try again later.";
            } catch (ClientNetworkException ex) {
                return SET_TEXT_COLOR_RED + "Connection Error. Please try again later.";
            } catch (Exception ex) {
                return SET_TEXT_COLOR_RED + "Unknown Error. Please try again later.";
            }
        } else {
            return SET_TEXT_COLOR_RED + "Expected: <username> <password>";
        }

    }
 }
