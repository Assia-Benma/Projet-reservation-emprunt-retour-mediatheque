package bibliotheque.document.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import bibliotheque.Abonne;
import Exception.*;
import bibliotheque.document.type.DVD;
import bibliotheque.document.type.Livre;

import java.time.LocalDate;

public class DocumentTest {

    @Test
    public void testScenarioLivre() throws Exception {
        Abonne abonne1 = new Abonne(1, "Alice", LocalDate.of(2000, 1, 1));
        Abonne abonne2 = new Abonne(2, "Bob", LocalDate.of(2000, 1, 1));

        Livre livre = new Livre("L1", 200, "Design Patterns");

        //Le livre est libre, quelqu'un le réserve
        livre.reservation(abonne1);
        assertFalse(livre.canTake(abonne2)); // une autre personne ne peut pas le prendre

        //Une autre personne veut le réserver -> exception
        assertThrows(ReservationException.class, () -> {
            livre.reservation(abonne2);
        });

        //Une autre personne veut l'emprunter -> exception
        assertThrows(EmpruntException.class, () -> {
            livre.emprunt(abonne2);
        });

        //La personne qui l'a réservé veut l'emprunter -> OK
        livre.emprunt(abonne1);
        assertFalse(livre.canTake(abonne1)); // car maintenant emprunté

        //Elle le retourne
        livre.retour();
        assertTrue(livre.canTake(abonne1)); // redevenu libre

        //Une autre personne peut l'emprunter
        livre.emprunt(abonne2);
        assertFalse(livre.canTake(abonne1));
    }

    @Test
    public void testDVDRestrictions() throws Exception {
        // Abonné mineur (moins de 16 ans)
        Abonne mineur = new Abonne(1, "Tom", LocalDate.now().minusYears(10));
        // Abonné adulte (plus de 16 ans)
        Abonne adulte = new Abonne(2, "Alice", LocalDate.now().minusYears(20));

        // DVD adulte et DVD tout public
        DVD dvdAdulte = new DVD("D1", "Film interdit", true);
        DVD dvdToutPublic = new DVD("D2", "Film familial", false);

        // Mineur veut emprunter DVD adulte -> impossible
        assertFalse(dvdAdulte.canTake(mineur));
        assertThrows(EmpruntException.class, () -> {
            dvdAdulte.emprunt(mineur);
        });

        // Mineur veut emprunter DVD tout public -> possible
        assertTrue(dvdToutPublic.canTake(mineur));
        dvdToutPublic.emprunt(mineur);

        // retour du DVD
        dvdToutPublic.retour();

        // Adulte veut emprunter DVD adulte -> possible
        assertTrue(dvdAdulte.canTake(adulte));
        dvdAdulte.emprunt(adulte);

        // retour
        dvdAdulte.retour();

        // Adulte veut emprunter DVD tout public -> possible
        assertTrue(dvdToutPublic.canTake(adulte));
        dvdToutPublic.emprunt(adulte);
    }
}