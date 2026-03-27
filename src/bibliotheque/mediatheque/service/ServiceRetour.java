package bibliotheque.mediatheque.service;

import bibliotheque.document.Document;
import bibliotheque.mediatheque.server.ServerMediatheque;
import Exception.*;

import java.io.*;
import java.util.HashMap;

public class ServiceRetour extends Service {

    public void run() {
        HashMap<String, Document> docs = ServerMediatheque.getListeDocs();
        System.out.println("L'utilisateur s'est connecté");

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Bienvenue à la médiatheque, vous pouvez retourner un livre.");

            out.println("Voici la liste des commandes possible : ");
            out.println("- retourner <id> : Retourner un document par son ID");
            out.println("- quitter : Fermer la connexion");

            String commande;
            while ((commande = in.readLine()) != null) {
                commande = commande.trim();

                switch (commande.toLowerCase()) {
                    case "quitter":
                        out.println("Au revoir !");
                        client.close();
                        return;

                    default:
                        if (commande.toLowerCase().startsWith("retourner ")) {
                            String idDoc = commande.substring("retourner ".length()).trim();
                            if (!docs.containsKey(idDoc)) {
                                out.println("Document [" + idDoc + "] introuvable.");
                            } else {
                                try {
                                    docs.get(idDoc).retour();
                                    out.println("OK : document [" + idDoc + "] retourné avec succès.");
                                } catch (RetourException e) {
                                    out.println("Ce document n'est pas emprunté.");
                                }
                            }
                        } else {
                            out.println("Commande inconnue. Essayez : retourner <id>, quitter.");
                        }
                }
            }

            client.close();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}