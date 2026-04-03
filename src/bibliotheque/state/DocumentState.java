package bibliotheque.state;

import Exception.EmpruntException;
import Exception.ReservationException;
import Exception.RetourException;
import bibliotheque.Abonne;
import bibliotheque.document.Document;

public interface DocumentState {
    boolean canTake(Document doc, Abonne ab);

    void reservation(Document doc, Abonne ab) throws ReservationException;

    void emprunt(Document doc, Abonne ab) throws EmpruntException;

    void retour(Document doc, boolean degradationConstatee) throws RetourException;
}

