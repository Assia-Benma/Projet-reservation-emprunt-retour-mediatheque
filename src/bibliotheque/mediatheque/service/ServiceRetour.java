package bibliotheque.mediatheque.service;

import Exception.RetourException;
import bibliotheque.document.Document;
import bibliotheque.mediatheque.server.ServerMediatheque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServiceRetour extends Service {

    public void run() {
        Map<String, Document> docs = ServerMediatheque.getListeDocs();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Bienvenue sur le service de retour (port 2002).");
            out.println("Commandes: retourner <id> [degrade] | quitter");

            String commande;
            while ((commande = in.readLine()) != null) {
                String trimmed = commande.trim();
                String lower = trimmed.toLowerCase(Locale.ROOT);

                if (lower.equals("quitter")) {
                    out.println("Connexion fermee.");
                    client.close();
                    return;
                }

                if (lower.startsWith("retourner ")) {
                    String[] parts = trimmed.split("\\s+");
                    if (parts.length < 2) {
                        out.println("Usage: retourner <id> [degrade]");
                        continue;
                    }

                    String idDoc = parts[1];
                    boolean degradationConstatee = parts.length >= 3 && parts[2].equalsIgnoreCase("degrade");
                    Document document = docs.get(idDoc);
                    if (document == null) {
                        out.println("ERREUR: document [" + idDoc + "] introuvable.");
                        continue;
                    }

                    try {
                        document.retour(degradationConstatee);
                        out.println("OK: document [" + idDoc + "] retourne avec succes.");

                        List<String> alerts = document.drainAvailabilityAlerts();
                        if (!alerts.isEmpty()) {
                            ServerMediatheque.getSmokeSignalService().sendAvailabilityAlerts(document, alerts);
                            out.println("INFO: " + alerts.size() + " alerte(s) de disponibilite envoyee(s).");
                        }
                    } catch (RetourException e) {
                        out.println("REFUS: " + e.getMessage());
                    }
                    continue;
                }

                out.println("Commande inconnue. Commandes: retourner <id> [degrade] | quitter");
            }

            client.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

