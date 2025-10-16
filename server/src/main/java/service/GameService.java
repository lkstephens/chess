package service;

import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;

public class GameService {

    private final GameDAO gameDAO;

    public GameService() {
        gameDAO = new MemoryGameDAO();
    }

    public void clear() {
        gameDAO.clear();
    }
}
