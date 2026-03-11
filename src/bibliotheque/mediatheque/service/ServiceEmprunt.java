package bibliotheque.mediatheque.service;

import bibliotheque.Abonne;
import bibliotheque.mediatheque.server.ServerMediatheque;

import java.io.*;
import java.util.HashMap;

public class ServiceEmprunt extends Service {
    private int id_abonnee;
    private HashMap<Integer, Abonne> abonnes;

    public void run() {
        abonnes = ServerMediatheque.getListeAbonne();
        System.out.println("L'utilisateur s'est connecté");
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Bienvenue à la médiatheque, vous pouvez emprunter un livre.");

            out.println("vous êtes qui svp ?? (et mentez par par pitié)");
            String line = in.readLine();
            try {
                id_abonnee = Integer.parseInt(line);

                if (!abonnes.containsKey(id_abonnee)) {
                    out.println("ID abonne inconnu.");
                    client.close();
                    return;
                }

                if (abonnes.get(id_abonnee).estBanni()) {
                    out.println("Vous etes actuellement banni.");
                    client.close();
                    return;
                }
            } catch (NumberFormatException e) {
                out.println("ID invalide.");
                client.close();
                return;
            }

            out.println("d'accord bonjoureuh vous là qui avez le numéro " + id_abonnee);//je vais vraiment te frapper raph
                                                                                        // >:(

            out.println("Voici la liste des commandes possible : ");
            out.println("- liste <type>: Voir la liste des objets, le type est falcultatif");
            out.println("- emprunter <id> <id_abonne>: ");

            // TODO: travailler (avec un switch case pour les commandes list/emprunter yaya).
        }
        catch (IOException ex) {
            System.out.println("c'est qui le débile qui a pas mis a jour son client");

        }
    }
}
