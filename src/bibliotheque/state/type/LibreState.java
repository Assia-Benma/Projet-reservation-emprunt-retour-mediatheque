package bibliotheque.state.type;

import Exception.EmpruntException;
import Exception.ReservationException;
import Exception.RetourException;
import bibliotheque.Abonne;
import bibliotheque.document.Document;
import bibliotheque.state.DocumentState;

public class LibreState implements DocumentState {

    @Override
    public boolean canTake(Document doc, Abonne ab) {
        return !ab.estBanni();
    }

    @Override
    public void reservation(Document doc, Abonne ab) {
        if (ab.estBanni()) {
            throw new ReservationException("Vous etes banni, reservation impossible.");
        }
        doc.reserveFor(ab);
    }

    @Override
    public void emprunt(Document doc, Abonne ab) {
        if (ab.estBanni()) {
            throw new EmpruntException("Vous etes banni, emprunt impossible.");
        }
        doc.markBorrowedBy(ab);
    }

    @Override
    public void retour(Document doc, boolean degradationConstatee) {
        throw new RetourException("Ce document est deja disponible en rayon.");
    }
}

