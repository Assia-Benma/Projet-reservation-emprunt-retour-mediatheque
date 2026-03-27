package bibliotheque.mediatheque.service;

import bibliotheque.Abonne;
import bibliotheque.document.Document;
import bibliotheque.mediatheque.server.ServerMediatheque;
import Exception.*;

import java.io.*;
import java.util.HashMap;

public class ServiceReservation extends Service {
    private int id_abonnee;
    private HashMap<Integer, Abonne> abonnes;

    public void run() {
        abonnes = ServerMediatheque.getListeAbonne();
        HashMap<String, Document> docs = ServerMediatheque.getListeDocs();
        System.out.println("L'utilisateur s'est connecté");

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Bienvenue à la médiatheque, vous pouvez emprunter un livre.");

            out.println("Qui êtes vous ?");
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

            out.println("Bonjour ! Personne ayant l'identifiant :  " + id_abonnee);

            out.println("Voici la liste des commandes possible : ");
            out.println("- liste : Voir la liste des documents");
            out.println("- reserver <id> : Réserver un document par son ID");
            out.println("- quitter : Fermer la connexion");

            Abonne abonne = abonnes.get(id_abonnee);
            String commande;
            while ((commande = in.readLine()) != null) {
                commande = commande.trim();

                switch (commande.toLowerCase()) {
                    case "quitter":
                        out.println("Au revoir !");
                        client.close();
                        return;

                    case "liste":
                        out.println("=== Catalogue ===");
                        for (Document doc : docs.values()) {
                            out.println("- [" + doc.idDoc() + "] "
                                    + doc.getClass().getSimpleName()
                                    + (doc.canTake(abonne) ? " (disponible)" : " (indisponible)"));
                        }
                        break;

                    default:
                        if (commande.toLowerCase().startsWith("reserver ")) {
                            String idDoc = commande.substring("reserver ".length()).trim();
                            if (!docs.containsKey(idDoc)) {
                                out.println("ERREUR : document [" + idDoc + "] introuvable.");
                            } else {
                                try {
                                    docs.get(idDoc).reservation(abonne);
                                    out.println("OK : document [" + idDoc + "] réservé avec succès.");
                                } catch (ReservationException e) {
                                    out.println("ERREUR : " + e.getMessage());
                                }
                            }
                        } else {
                            out.println("Commande inconnue. Essayez : liste, reserver <id>, quitter.");
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