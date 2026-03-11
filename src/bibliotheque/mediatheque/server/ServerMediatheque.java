package bibliotheque.mediatheque.server;

import bibliotheque.Abonne;
import bibliotheque.mediatheque.service.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Random;

public class ServerMediatheque {
    protected static HashMap<Integer, Abonne> listeAbonne;
    private ServerReservation reserver;
    private ServerEmprunt emprunter;
    private ServerRetour retourne;

    private static final int RESERVER_PORT = 2000;
    private static final int EMPRUNTER_PORT = 2001;
    private static final int RETOUR_PORT = 2002;

    public static void main(String[] args) {




        try {
            new Thread(new ServerReservation(Service.class, RESERVER_PORT)).start();
            System.out.println("Serveur reserver started on port " + RESERVER_PORT);
            new Thread(new ServerEmprunt(Service.class, EMPRUNTER_PORT)).start();
            System.out.println("Serveur emprunter started on port " + EMPRUNTER_PORT);
            new Thread(new ServerRetour(Service.class, RETOUR_PORT)).start();
            System.out.println("Serveur retour started on port " + EMPRUNTER_PORT);


            System.out.println("Serveur lancé sur le port " + RETOUR_PORT);

        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur: " + e.getMessage());
        }
    }

    public static HashMap<Integer, Abonne> getListeAbonne() {
        return listeAbonne;
    }

    private static void initialiserAbonnesAleatoires(int nb) {
        Random random = new Random();
        while (listeAbonne.size() < nb) {
            int numero = 1000 + random.nextInt(9000); // 1000..9999
            if (listeAbonne.containsKey(numero)) {
                continue; // unicite
            }

            String nom = "Abonne_" + numero;

            int age = 16 + random.nextInt(55);
            LocalDate dateNaissance = LocalDate.now()
                    .minusYears(age)
                    .minusDays(random.nextInt(365));

            listeAbonne.put(numero, new Abonne(numero, nom, dateNaissance));
        }
    }
}
