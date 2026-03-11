package bibliotheque.mediatheque.service;

import java.net.Socket;

public abstract class Service implements Runnable {
    protected Socket client;

    public void setSocket(Socket socket) {
        this.client = socket;
    }

    public Socket getSocket() {
        return client;
    }
}