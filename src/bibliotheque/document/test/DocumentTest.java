package bibliotheque.document.test;

import Exception.EmpruntException;
import Exception.ReservationException;
import bibliotheque.Abonne;
import bibliotheque.document.type.DVD;
import bibliotheque.document.type.Livre;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentTest {

    @Test
    public void testScenarioLivreReservationEmpruntRetour() {
        Abonne abonne1 = new Abonne(1, "Alice", LocalDate.of(2000, 1, 1));
        Abonne abonne2 = new Abonne(2, "Bob", LocalDate.of(2000, 1, 1));

        Livre livre = new Livre("L1", 200, "Design Patterns");

        livre.reservation(abonne1);
        assertFalse(livre.canTake(abonne2));

        assertThrows(ReservationException.class, () -> livre.reservation(abonne2));
        assertThrows(EmpruntException.class, () -> livre.emprunt(abonne2));

        livre.emprunt(abonne1);
        assertFalse(livre.canTake(abonne1));

        livre.retour();
        assertTrue(livre.canTake(abonne1));

        livre.emprunt(abonne2);
        assertFalse(livre.canTake(abonne1));
    }

    @Test
    public void testReservationExpireApresDeuxHeures() {
        Abonne abonne1 = new Abonne(1, "Alice", LocalDate.of(2000, 1, 1));
        Abonne abonne2 = new Abonne(2, "Bob", LocalDate.of(2000, 1, 1));
        Livre livre = new Livre("L1", 200, "Design Patterns");

        livre.reservation(abonne1);
        livre.setReservationExpireAtForTest(LocalDateTime.now().minusSeconds(1));

        assertTrue(livre.canTake(abonne2));
        livre.reservation(abonne2);
        assertEquals(abonne2, livre.getReservationAbonne());
    }

    @Test
    public void testGrandChamanAttentePuisReservationReussie() throws InterruptedException {
        Abonne abonne1 = new Abonne(1, "Alice", LocalDate.of(2000, 1, 1));
        Abonne abonne2 = new Abonne(2, "Bob", LocalDate.of(2000, 1, 1));
        Livre livre = new Livre("L1", 200, "Design Patterns");

        livre.reservation(abonne1);
        livre.setReservationExpireAtForTest(LocalDateTime.now().plusSeconds(1));

        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread attente = new Thread(() -> {
            try {
                livre.reservation(abonne2);
            } catch (Throwable e) {
                failure.set(e);
            }
        });

        attente.start();
        attente.join(3000);

        assertFalse(attente.isAlive(), "La reservation Grand Chaman aurait du se terminer en moins de 3s");
        assertNull(failure.get(), "La reservation Grand Chaman aurait du reussir");
        assertEquals(abonne2, livre.getReservationAbonne());
    }

    @Test
    public void testGrandChamanEchecSiEmpruntePendantAttente() throws InterruptedException {
        Abonne abonne1 = new Abonne(1, "Alice", LocalDate.of(2000, 1, 1));
        Abonne abonne2 = new Abonne(2, "Bob", LocalDate.of(2000, 1, 1));
        Livre livre = new Livre("L1", 200, "Design Patterns");

        livre.reservation(abonne1);
        livre.setReservationExpireAtForTest(LocalDateTime.now().plusSeconds(2));

        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread attente = new Thread(() -> {
            try {
                livre.reservation(abonne2);
            } catch (Throwable e) {
                failure.set(e);
            }
        });

        attente.start();
        Thread.sleep(300);
        livre.emprunt(abonne1);
        attente.join(3000);

        assertFalse(attente.isAlive(), "La reservation en attente aurait du se terminer");
        assertNotNull(failure.get(), "La reservation aurait du echouer car le doc a ete emprunte");
        assertTrue(failure.get() instanceof ReservationException);
        assertTrue(failure.get().getMessage().contains("emprunte pendant l'attente"));
    }

    @Test
    public void testGeronimoBanRetardSuperieurADeuxSemaines() {
        Abonne abonne = new Abonne(1, "Alice", LocalDate.of(2000, 1, 1));
        Livre livre = new Livre("L1", 200, "Design Patterns");

        livre.emprunt(abonne);
        livre.setEmpruntAtForTest(LocalDateTime.now().minusWeeks(3));
        livre.retour(false);

        assertTrue(abonne.estBanni());
    }

    @Test
    public void testGeronimoBanSiDegradationConstatee() {
        Abonne abonne = new Abonne(1, "Alice", LocalDate.of(2000, 1, 1));
        Livre livre = new Livre("L1", 200, "Design Patterns");

        livre.emprunt(abonne);
        livre.retour(true);

        assertTrue(abonne.estBanni());
    }

    @Test
    public void testDVDRestrictions() {
        Abonne mineur = new Abonne(1, "Tom", LocalDate.now().minusYears(10));
        Abonne adulte = new Abonne(2, "Alice", LocalDate.now().minusYears(20));

        DVD dvdAdulte = new DVD("D1", "Film interdit", true);
        DVD dvdToutPublic = new DVD("D2", "Film familial", false);

        assertFalse(dvdAdulte.canTake(mineur));
        assertThrows(EmpruntException.class, () -> dvdAdulte.emprunt(mineur));

        assertTrue(dvdToutPublic.canTake(mineur));
        dvdToutPublic.emprunt(mineur);
        dvdToutPublic.retour();

        assertTrue(dvdAdulte.canTake(adulte));
        dvdAdulte.emprunt(adulte);
        dvdAdulte.retour();
    }

    @Test
    public void testSittingBullAlertRegistration() {
        Abonne abonne = new Abonne(1, "Alice", LocalDate.of(2000, 1, 1));
        Livre livre = new Livre("L1", 200, "Design Patterns");

        livre.emprunt(abonne);
        assertTrue(livre.isUnavailableForReservation());

        livre.registerAvailabilityAlert("test@example.com");
        livre.registerAvailabilityAlert("test@example.com");
        livre.registerAvailabilityAlert("autre@example.com");

        List<String> alerts = livre.drainAvailabilityAlerts();
        assertEquals(2, alerts.size());
        assertTrue(livre.drainAvailabilityAlerts().isEmpty());
    }
}

