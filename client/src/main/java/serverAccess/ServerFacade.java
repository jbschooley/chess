package serverAccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

record ListGamesResponse(Collection<GameData> games) {}

public class ServerFacade {

    String host = "localhost";
    int port;
    String baseURI;

    public ServerFacade(int port) {
        this.port = port;
        this.baseURI = "http://%s:%d/".formatted(host, port);
    }

    public void clear() throws Exception {
        URI uri = new URI(baseURI + "db");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("DELETE");
        http.connect();
    }

    public AuthData register(String username, String password, String email) throws Exception {
        URI uri = new URI(baseURI + "user");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        Map<String, String> body = Map.of(
                "username", username,
                "password", password,
                "email", email
        );

        try (var outputStream = http.getOutputStream()) {
            var jsonBody = new Gson().toJson(body);
            outputStream.write(jsonBody.getBytes());
        }

        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            return new Gson().fromJson(inputStreamReader, AuthData.class);
        }

    }

    public AuthData login(String username, String password) throws Exception {
        URI uri = new URI(baseURI + "session");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        Map<String, String> body = Map.of(
                "username", username,
                "password", password
        );

        try (var outputStream = http.getOutputStream()) {
            var jsonBody = new Gson().toJson(body);
            outputStream.write(jsonBody.getBytes());
        }

        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            return new Gson().fromJson(inputStreamReader, AuthData.class);
        }
    }

    public void logout(String authToken) throws Exception {
        URI uri = new URI(baseURI + "session");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("DELETE");
        http.setRequestProperty("Authorization", authToken);
        http.connect();
    }

    public Collection<GameData> listGames(String authToken) throws Exception {
        URI uri = new URI(baseURI + "game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("Authorization", authToken);
        http.connect();

        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            ListGamesResponse gameList = new Gson().fromJson(inputStreamReader, ListGamesResponse.class);
            return gameList.games();
        }
    }
}
