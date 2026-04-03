package bibliotheque.mediatheque.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class BaseClient {
    protected abstract int defaultPort();

    protected abstract String clientName();

    public void run(String[] args) {
        String host = args.length >= 1 ? args[0] : "127.0.0.1";
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : defaultPort();

        System.out.println("Connexion " + clientName() + " vers " + host + ":" + port);
        try (Socket socket = new Socket(host, port);
             BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = socketIn.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ignored) {
                }
            }, clientName() + "-reader");
            reader.setDaemon(true);
            reader.start();

            String input;
            while ((input = console.readLine()) != null) {
                socketOut.println(input);
                if ("quitter".equalsIgnoreCase(input.trim())) {
                    break;
                }
            }

            reader.join(500);
        } catch (IOException e) {
            System.err.println("Erreur client " + clientName() + ": " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Client " + clientName() + " interrompu.");
            Thread.currentThread().interrupt();
        }
    }
}
