package bibliotheque.mediatheque.server;

import bibliotheque.mediatheque.service.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReservation implements Runnable {
    private final ServerSocket listenSocket;
    private final Class<? extends Service> serviceClass;

    public ServerReservation(Class<? extends Service> serviceClass, int port) throws IOException {
        this.serviceClass = serviceClass;
        this.listenSocket = new ServerSocket(port);
    }

    public void run() {
        System.err.println("Serveur reservation actif sur le port " + listenSocket.getLocalPort());
        try {
            while (true) {
                Socket clientSocket = listenSocket.accept();
                try {
                    Service service = serviceClass.getDeclaredConstructor().newInstance();
                    service.setSocket(clientSocket);
                    new Thread(service).start();
                } catch (ReflectiveOperationException e) {
                    System.err.println("Erreur lors de la creation du service: " + e.getMessage());
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Serveur reservation arrete: " + e.getMessage());
        }
    }
}

