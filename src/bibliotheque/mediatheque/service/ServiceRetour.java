package bibliotheque.mediatheque.service;

import java.io.*;

public class ServiceRetour extends Service{

    public void run() {
        System.out.println("L'utilisateur s'est connecté");
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Bienvenue à la médiatheque, vous pouvez retourner un livre.");

            out.println("Voici la liste des commandes possible : ");

        }catch (IOException ex) {
            System.out.println("c'est qui le débile qui a pas mis a jour son client");
        }
        }
    }


