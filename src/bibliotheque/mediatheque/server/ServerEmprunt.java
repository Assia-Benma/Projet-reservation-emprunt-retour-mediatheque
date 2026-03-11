package bibliotheque.mediatheque.server;

import bibliotheque.mediatheque.service.Service;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class ServerEmprunt implements Runnable{
    private ServerSocket listenSocket;
    private final Class<? extends Service> serviceClass;

    public ServerEmprunt(Class<? extends Service> serviceClass, int port) throws IOException {
        this.serviceClass = serviceClass;
        this.listenSocket = new ServerSocket(port);
    }

    public void run() {
        System.err.println("Serveur démarré sur le port " + listenSocket.getLocalPort());
        try {
            while (true) {
                Socket clientSocket = listenSocket.accept();

                try {
                    Service service = serviceClass.newInstance();
                    service.setSocket(clientSocket);
                    new Thread(service).start();

                } catch (InstantiationException | IllegalAccessException e) {
                    System.err.println("Erreur lors de la création du service: " + e.getMessage());
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Serveur arrêté: " + e.getMessage());
        }
    }
}
