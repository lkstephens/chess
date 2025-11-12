package ui;

public interface ChessClient {
    String run();
    void printPrompt();
    String help();
    String eval(String input);
}
