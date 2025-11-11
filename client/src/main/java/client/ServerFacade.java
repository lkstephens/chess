package client;

import com.google.gson.Gson;
import datamodel.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

    public void clear() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + "/db"))
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception {
        var request = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws Exception {
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(String authToken) throws Exception {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws Exception {
        var request = buildRequest("POST", "/game", createGameRequest, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws Exception {
        var request = buildRequest("PUT", "/game", joinGameRequest, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ClientNetworkException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            throw new ClientNetworkException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass)
            throws Exception {

        var status = response.statusCode();

        // Unsuccessful Path
        if (!isSuccessful(status)) {

            var body = response.body();

            if (body != null) {
                var serializer = new Gson();
                // Check Status Codes
                if (status == 400) {
                    ClientErrorResponse errResponse = serializer.fromJson(body, ClientErrorResponse.class);
                    throw new ClientBadRequestException((errResponse.message()));
                } else if (status == 401) {
                    ClientErrorResponse errResponse = serializer.fromJson(body, ClientErrorResponse.class);
                    throw new ClientUnauthorizedException((errResponse.message()));
                } else if (status == 403) {
                    ClientErrorResponse errResponse = serializer.fromJson(body, ClientErrorResponse.class);
                    throw new ClientAlreadyTakenException((errResponse.message()));
                } else if (status >= 500) {
                    throw new ClientServerException("Internal Server Error");
                }
            }

            throw new Exception("other failure: " + status);
        }

        // Successful Path
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        // Returning Empty Json
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
