package client;

import ui.PreLoginClient;

public class ClientMain {
    public static void main(String[] args) {
        var serverURL = "http://localhost:8080";
        PreLoginClient preLoginClient = new PreLoginClient(serverURL);
        String goodbyeMessage = preLoginClient.run();
        System.out.println(goodbyeMessage);
    }
}