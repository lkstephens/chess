package ui;


import client.ServerFacade;

public class PostLoginClient implements ChessClient {

    private final ServerFacade server;

    public PostLoginClient(ServerFacade server) {
        this.server = server;
    }

    public void run() {
        System.out.println("running post-login");
    }
}
