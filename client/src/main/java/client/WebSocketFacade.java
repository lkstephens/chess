package client;

public class WebSocketFacade {

    private ServerMessageObserver observer;

    public WebSocketFacade(ServerMessageObserver observer) {
        this.observer = observer;
    }

}
