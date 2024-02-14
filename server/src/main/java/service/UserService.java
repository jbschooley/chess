package service;

import model.AuthData;
import model.UserData;

record LoginResult(String username, String authToken) {}

public class UserService {
    public AuthData register(UserData user) {
        return null;
    }
    public AuthData login(UserData user) {
        return null;
    }
    public void logout(UserData user) {}
}
