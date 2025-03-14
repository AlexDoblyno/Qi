package ui;

import java.net.URI;
import java.net.http.HttpClient;

public class Client {
    private final ServerFacade serverFacade;
    private final String port;

    public Client(String serverPort) {
        port = serverPort;
        serverFacade = new ServerFacade(port);
    }

    public String getPort() {
        return port;
    }

    public ServerFacade getServerFacade() {
        return serverFacade;
    }
}
