package bibliotheque.state.type;

import Exception.EmpruntException;
import Exception.ReservationException;
import Exception.RetourException;
import bibliotheque.Abonne;
import bibliotheque.document.Document;
import bibliotheque.state.DocumentState;

public class ReserveState implements DocumentState {

    @Override
    public boolean canTake(Document doc, Abonne ab) {
        if (ab.estBanni()) {
            return false;
        }
        return doc.getReservationAbonne() == ab;
    }

    @Override
    public void reservation(Document doc, Abonne ab) {
        if (ab.estBanni()) {
            throw new ReservationException("Vous etes banni, reservation impossible.");
        }

        if (doc.getReservationAbonne() == ab) {
            throw new ReservationException("Ce document est deja reserve a votre nom jusqu'au " + doc.getReservationDeadlineLabel() + ".");
        }

        long remainingSeconds = doc.getReservationRemainingSeconds();
        if (remainingSeconds <= 60) {
            Document.WaitOutcome outcome = doc.waitForReservationRelease(remainingSeconds + 1);
            if (outcome == Document.WaitOutcome.AVAILABLE) {
                doc.reserveFor(ab);
                return;
            }
            if (outcome == Document.WaitOutcome.BORROWED_DURING_WAIT) {
                throw new ReservationException("Le document a ete emprunte pendant l'attente de la reservation.");
            }
            if (outcome == Document.WaitOutcome.INTERRUPTED) {
                throw new ReservationException("Attente interrompue pendant la reservation Grand Chaman.");
            }
        }

        throw new ReservationException("Ce document est reserve jusqu'au " + doc.getReservationDeadlineLabel() + ".");
    }

    @Override
    public void emprunt(Document doc, Abonne ab) {
        if (ab.estBanni()) {
            throw new EmpruntException("Vous etes banni, emprunt impossible.");
        }

        if (doc.getReservationAbonne() != ab) {
            throw new EmpruntException("Ce document est reserve pour un autre abonne jusqu'au " + doc.getReservationDeadlineLabel() + ".");
        }

        doc.markBorrowedBy(ab);
    }

    @Override
    public void retour(Document doc, boolean degradationConstatee) {
        throw new RetourException("Le document est reserve, pas emprunte : retour impossible.");
    }
}

