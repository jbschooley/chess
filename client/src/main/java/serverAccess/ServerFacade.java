package serverAccess;

public class ServerFacade {

    String host = "localhost";
    int port;
    String baseUrl;

    public ServerFacade(int port) {
        this.port = port;
        this.baseUrl = "http://%s:%d/".formatted(host, port);
    }
}
