package bibliotheque.document;

import Exception.EmpruntException;
import Exception.ReservationException;
import Exception.RetourException;
import bibliotheque.Abonne;
import bibliotheque.state.DocumentState;
import bibliotheque.state.type.EmprunteState;
import bibliotheque.state.type.LibreState;
import bibliotheque.state.type.ReserveState;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class Document implements IDocument {
    private static final Duration RESERVATION_DURATION = Duration.ofHours(2);
    private static final DateTimeFormatter RESERVATION_FORMATTER = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");

    private final String id;
    private final String titre;
    protected DocumentState state;

    private Abonne reservationAbonne;
    private LocalDateTime reservationExpireAt;
    private Abonne emprunteur;
    private LocalDateTime empruntAt;

    private final List<String> alertEmails = new ArrayList<>();

    public enum WaitOutcome {
        AVAILABLE,
        BORROWED_DURING_WAIT,
        STILL_RESERVED,
        INTERRUPTED
    }

    public Document(String id, String titre) {
        this.id = id;
        this.titre = titre;
        this.state = new LibreState();
    }

    @Override
    public synchronized String idDoc() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public synchronized DocumentState getState() {
        return state;
    }

    public synchronized void setState(DocumentState state) {
        this.state = state;
    }

    public synchronized Abonne getReservationAbonne() {
        return reservationAbonne;
    }

    public synchronized Abonne getEmprunteur() {
        return emprunteur;
    }

    public synchronized LocalDateTime getReservationExpireAt() {
        return reservationExpireAt;
    }

    public synchronized LocalDateTime getEmpruntAt() {
        return empruntAt;
    }

    public synchronized long getReservationRemainingSeconds() {
        if (reservationExpireAt == null) {
            return 0;
        }
        long millis = Duration.between(LocalDateTime.now(), reservationExpireAt).toMillis();
        if (millis <= 0) {
            return 0;
        }
        return (millis + 999) / 1000;
    }

    public synchronized String getReservationDeadlineLabel() {
        return reservationExpireAt == null ? "inconnue" : reservationExpireAt.format(RESERVATION_FORMATTER);
    }

    public synchronized void reserveFor(Abonne ab) {
        reservationAbonne = ab;
        reservationExpireAt = LocalDateTime.now().plus(RESERVATION_DURATION);
        emprunteur = null;
        empruntAt = null;
        setState(new ReserveState());
        notifyAll();
    }

    public synchronized void markBorrowedBy(Abonne ab) {
        emprunteur = ab;
        empruntAt = LocalDateTime.now();
        reservationAbonne = null;
        reservationExpireAt = null;
        setState(new EmprunteState());
        notifyAll();
    }

    public synchronized void markReturnedToShelf() {
        reservationAbonne = null;
        reservationExpireAt = null;
        emprunteur = null;
        empruntAt = null;
        setState(new LibreState());
        notifyAll();
    }

    public synchronized void refreshReservationIfExpired() {
        if (!(state instanceof ReserveState)) {
            return;
        }

        if (reservationExpireAt != null && !LocalDateTime.now().isBefore(reservationExpireAt)) {
            reservationAbonne = null;
            reservationExpireAt = null;
            setState(new LibreState());
            notifyAll();
        }
    }

    public synchronized WaitOutcome waitForReservationRelease(long waitSeconds) {
        long boundedWaitSeconds = Math.max(1, waitSeconds);
        long deadlineMillis = System.currentTimeMillis() + (boundedWaitSeconds * 1000L);

        while (System.currentTimeMillis() < deadlineMillis) {
            refreshReservationIfExpired();

            if (state instanceof LibreState) {
                return WaitOutcome.AVAILABLE;
            }
            if (state instanceof EmprunteState) {
                return WaitOutcome.BORROWED_DURING_WAIT;
            }

            long msLeft = deadlineMillis - System.currentTimeMillis();
            if (msLeft <= 0) {
                break;
            }

            long reservationMsLeft = reservationExpireAt == null ? msLeft : Math.max(1, Duration.between(LocalDateTime.now(), reservationExpireAt).toMillis());
            long waitMs = Math.min(msLeft, reservationMsLeft);
            try {
                wait(waitMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return WaitOutcome.INTERRUPTED;
            }
        }

        refreshReservationIfExpired();
        if (state instanceof LibreState) {
            return WaitOutcome.AVAILABLE;
        }
        if (state instanceof EmprunteState) {
            return WaitOutcome.BORROWED_DURING_WAIT;
        }
        return WaitOutcome.STILL_RESERVED;
    }

    public synchronized boolean registerAvailabilityAlert(String email) {
        if (email == null) {
            return false;
        }
        String cleanEmail = email.trim();
        if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
            return false;
        }
        if (!alertEmails.contains(cleanEmail)) {
            alertEmails.add(cleanEmail);
            return true;
        }
        return false;
    }

    public synchronized List<String> drainAvailabilityAlerts() {
        List<String> result = new ArrayList<>(alertEmails);
        alertEmails.clear();
        return result;
    }

    public synchronized boolean isUnavailableForReservation() {
        refreshReservationIfExpired();
        return state instanceof ReserveState || state instanceof EmprunteState;
    }

    @Override
    public synchronized void reservation(Abonne ab) throws ReservationException {
        refreshReservationIfExpired();
        state.reservation(this, ab);
    }

    @Override
    public synchronized void emprunt(Abonne ab) throws EmpruntException {
        refreshReservationIfExpired();
        state.emprunt(this, ab);
    }

    @Override
    public synchronized void retour() throws RetourException {
        retour(false);
    }

    public synchronized void retour(boolean degradationConstatee) throws RetourException {
        refreshReservationIfExpired();
        state.retour(this, degradationConstatee);
    }

    public synchronized boolean canTake(Abonne ab) {
        refreshReservationIfExpired();
        return state.canTake(this, ab);
    }

    public synchronized void setReservationExpireAtForTest(LocalDateTime reservationExpireAt) {
        this.reservationExpireAt = reservationExpireAt;
    }

    public synchronized void setEmpruntAtForTest(LocalDateTime empruntAt) {
        this.empruntAt = empruntAt;
    }
}

