package bibliotheque.mediatheque.service;

import Exception.ReservationException;
import bibliotheque.Abonne;
import bibliotheque.document.Document;
import bibliotheque.mediatheque.server.ServerMediatheque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

public class ServiceReservation extends Service {
    private static final String DEFAULT_TEST_RECIPIENT = "jean-francois.brette@u-paris.fr";

    public void run() {
        Map<Integer, Abonne> abonnes = ServerMediatheque.getListeAbonne();
        Map<String, Document> docs = ServerMediatheque.getListeDocs();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Bienvenue sur le service de reservation (port 2000).");
            out.println("Entrez votre numero d'abonne:");

            String line = in.readLine();
            Integer idAbonne = parseAbonneId(line, abonnes, out);
            if (idAbonne == null) {
                client.close();
                return;
            }

            Abonne abonne = abonnes.get(idAbonne);

            out.println("Bonjour " + abonne.getNom() + " (#" + idAbonne + ")");
            out.println("Commandes: liste | reserver <id> | alerte <id> <email> | nuage-test [email] | quitter");

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
                    out.println("=== Catalogue reservation ===");
                    for (Document doc : docs.values()) {
                        out.println("- [" + doc.idDoc() + "] " + doc.getClass().getSimpleName() + " : " + getReservationStatus(doc, abonne));
                    }
                    continue;
                }

                if (lower.startsWith("reserver ")) {
                    String idDoc = trimmed.substring("reserver ".length()).trim();
                    Document document = docs.get(idDoc);
                    if (document == null) {
                        out.println("ERREUR: document [" + idDoc + "] introuvable.");
                        continue;
                    }

                    try {
                        document.reservation(abonne);
                        out.println("OK: document [" + idDoc + "] reserve jusqu'au " + document.getReservationDeadlineLabel() + ".");
                    } catch (ReservationException e) {
                        out.println("REFUS: " + e.getMessage());
                        if (document.isUnavailableForReservation()) {
                            out.println("Tip: utilisez 'alerte " + idDoc + " <email>' pour recevoir un signal de fumee au retour.");
                        }
                    }
                    continue;
                }

                if (lower.startsWith("alerte ")) {
                    String[] parts = trimmed.split("\\s+", 3);
                    if (parts.length < 3) {
                        out.println("Usage: alerte <id> <email>");
                        continue;
                    }

                    Document document = docs.get(parts[1]);
                    if (document == null) {
                        out.println("ERREUR: document [" + parts[1] + "] introuvable.");
                        continue;
                    }

                    if (!document.isUnavailableForReservation()) {
                        out.println("Le document est deja disponible, alerte inutile.");
                        continue;
                    }

                    boolean registered = document.registerAvailabilityAlert(parts[2]);
                    if (registered) {
                        out.println("OK: alerte enregistree pour [" + parts[1] + "] vers " + parts[2] + ".");
                    } else {
                        out.println("REFUS: email invalide ou deja enregistre.");
                    }
                    continue;
                }

                if (lower.startsWith("nuage-test")) {
                    String[] parts = trimmed.split("\\s+", 2);
                    String recipient = parts.length == 2 ? parts[1].trim() : DEFAULT_TEST_RECIPIENT;
                    ServerMediatheque.getSmokeSignalService().sendTestCloud(recipient);
                    out.println("OK: nuage de test envoye vers " + recipient + ".");
                    continue;
                }

                out.println("Commande inconnue. Commandes: liste | reserver <id> | alerte <id> <email> | nuage-test [email] | quitter");
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

    private String getReservationStatus(Document doc, Abonne abonne) {
        if (doc.isUnavailableForReservation()) {
            return "indisponible";
        }
        if (!doc.canTake(abonne)) {
            return "non autorise";
        }
        return "disponible";
    }
}

