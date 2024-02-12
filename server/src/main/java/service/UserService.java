package service;

import model.AuthData;
import model.UserData;

record LoginResult(String username, String authToken) {}

public class UserService {
    public AuthData register(UserData user) {}
    public AuthData login(UserData user) {}
    public void logout(UserData user) {}
}
