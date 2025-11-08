package client;

public class ClientAlreadyTakenException extends Exception {
    public ClientAlreadyTakenException(String message) {
        super(message);
    }
}
