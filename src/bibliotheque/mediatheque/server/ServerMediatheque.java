package bibliotheque.mediatheque.server;

import bibliotheque.Abonne;
import bibliotheque.document.Document;
import bibliotheque.document.type.DVD;
import bibliotheque.document.type.Livre;
import bibliotheque.mediatheque.service.ServiceEmprunt;
import bibliotheque.mediatheque.service.ServiceReservation;
import bibliotheque.mediatheque.service.ServiceRetour;
import bibliotheque.notification.SmokeSignalService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMediatheque {
    private static final Map<Integer, Abonne> listeAbonne = new ConcurrentHashMap<>();
    private static final Map<String, Document> listeDocs = new ConcurrentHashMap<>();
    private static final SmokeSignalService smokeSignalService = new SmokeSignalService();

    private static final int RESERVER_PORT = 2000;
    private static final int EMPRUNTER_PORT = 2001;
    private static final int RETOUR_PORT = 2002;

    public static void main(String[] args) {
        initialiserAbonnes();
        initialiserDocuments();

        try {
            new Thread(new ServerReservation(ServiceReservation.class, RESERVER_PORT), "server-reservation").start();
            System.out.println("Serveur reservation demarre sur le port " + RESERVER_PORT);

            new Thread(new ServerEmprunt(ServiceEmprunt.class, EMPRUNTER_PORT), "server-emprunt").start();
            System.out.println("Serveur emprunt demarre sur le port " + EMPRUNTER_PORT);

            new Thread(new ServerRetour(ServiceRetour.class, RETOUR_PORT), "server-retour").start();
            System.out.println("Serveur retour demarre sur le port " + RETOUR_PORT);

        } catch (IOException e) {
            System.err.println("Erreur lors du demarrage: " + e.getMessage());
        }
    }

    public static Map<Integer, Abonne> getListeAbonne() {
        return listeAbonne;
    }

    public static Map<String, Document> getListeDocs() {
        return listeDocs;
    }

    public static SmokeSignalService getSmokeSignalService() {
        return smokeSignalService;
    }

    private static void initialiserAbonnes() {
        listeAbonne.clear();
        listeAbonne.put(1001, new Abonne(1001, "Assia Assiette", LocalDate.of(1995, 3, 12)));
        listeAbonne.put(1002, new Abonne(1002, "Raph Miku", LocalDate.of(2010, 7, 22)));
        listeAbonne.put(1003, new Abonne(1003, "Alicia FanDePhainon", LocalDate.of(1988, 11, 5)));
    }

    private static void initialiserDocuments() {
        listeDocs.clear();
        listeDocs.put("L1", new Livre("L1", 200, "Livre trop cool"));
        listeDocs.put("L2", new Livre("L2", 350, "Autre livre tres cool"));
        listeDocs.put("D1", new DVD("D1", "Trap", false));
        listeDocs.put("D2", new DVD("D2", "Chainsaw man : Reze Arc", true));
    }
}

