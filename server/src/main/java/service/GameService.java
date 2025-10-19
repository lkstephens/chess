package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import datamodel.CreateGameRequest;
import datamodel.CreateGameResult;
import datamodel.JoinGameRequest;
import datamodel.ListGamesResult;
import model.AuthData;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(AuthDAO authDAO) {
        gameDAO = new MemoryGameDAO();
        this.authDAO = authDAO;
    }

    public void clear() {
        gameDAO.clear();
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException, DataAccessException{

        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new UnauthorizedException("Error: unauthorized");
            } else {
                return new ListGamesResult(gameDAO.listGames());
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error (TreeMap)", e);
        }
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws BadRequestException, UnauthorizedException, DataAccessException {

        String gameName = createGameRequest.gameName();

        // Check gameName for Bad Request (null, empty)
        if (gameName == null || gameName.isEmpty()) {
            throw new BadRequestException("Error: bad request");
        }

        try {

            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new UnauthorizedException("Error: unauthorized");
            } else {
                int gameID = gameDAO.createGame(gameName);

                return new CreateGameResult(gameID);

            }

        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error (TreeMap)", e);
        }
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws BadRequestException, UnauthorizedException, DataAccessException {

        String playerColor = joinGameRequest.playerColor();
        int gameID = joinGameRequest.gameID();

        if (playerColor == null || playerColor.isEmpty() ||
            !playerColor.equals("WHITE") && !playerColor.equals("BLACK") ||
            gameID < 1) {
            throw new BadRequestException("Error: bad request");
        }

        try {

            if (gameDAO.getGame(gameID) == null) {
                throw new BadRequestException("Error: game does not exist");
            }

            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new UnauthorizedException("Error: unauthorized");
            } else {
                String username = authData.username();
                gameDAO.updateGame(gameID, username, playerColor);

            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error (TreeMap)", e);
        }
    }
}
