package bibliotheque.mediatheque.service;

import Exception.EmpruntException;
import bibliotheque.Abonne;
import bibliotheque.document.Document;
import bibliotheque.mediatheque.server.ServerMediatheque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

public class ServiceEmprunt extends Service {
    public void run() {
        Map<Integer, Abonne> abonnes = ServerMediatheque.getListeAbonne();
        Map<String, Document> docs = ServerMediatheque.getListeDocs();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Bienvenue sur le service d'emprunt (port 2001).");
            out.println("Entrez votre numero d'abonne:");

            String line = in.readLine();
            Integer idAbonne = parseAbonneId(line, abonnes, out);
            if (idAbonne == null) {
                client.close();
                return;
            }

            Abonne abonne = abonnes.get(idAbonne);

            out.println("Bonjour " + abonne.getNom() + " (#" + idAbonne + ")");
            out.println("Commandes: liste | emprunter <id> | quitter");

            String commande;
            while ((commande = in.readLine()) != null) {
                String trimmed = commande.trim();
                String lower = trimmed.toLowerCase(Locale.ROOT);

                if (lower.equals("quitter")) {
                    out.println("Connexion fermee.");
                    client.close();
                    return;
                }

                if (lower.equals("liste")) {
                    out.println("=== Catalogue emprunt ===");
                    for (Document doc : docs.values()) {
                        out.println("- [" + doc.idDoc() + "] " + doc.getClass().getSimpleName() + " : " + (doc.canTake(abonne) ? "emprunt possible" : "emprunt impossible"));
                    }
                    continue;
                }

                if (lower.startsWith("emprunter ")) {
                    String idDoc = trimmed.substring("emprunter ".length()).trim();
                    Document document = docs.get(idDoc);
                    if (document == null) {
                        out.println("ERREUR: document [" + idDoc + "] introuvable.");
                        continue;
                    }

                    try {
                        document.emprunt(abonne);
                        out.println("OK: document [" + idDoc + "] emprunte avec succes.");
                    } catch (EmpruntException e) {
                        out.println("REFUS: " + e.getMessage());
                    }
                    continue;
                }

                out.println("Commande inconnue. Commandes: liste | emprunter <id> | quitter");
            }

            client.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Integer parseAbonneId(String line, Map<Integer, Abonne> abonnes, PrintWriter out) {
        int idAbonne;
        try {
            idAbonne = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            out.println("ID invalide.");
            return null;
        }

        Abonne abonne = abonnes.get(idAbonne);
        if (abonne == null) {
            out.println("ID abonne inconnu.");
            return null;
        }
        if (abonne.estBanni()) {
            out.println("Vous etes banni jusqu'au " + abonne.getDateFinBan() + ".");
            return null;
        }
        return idAbonne;
    }
}

