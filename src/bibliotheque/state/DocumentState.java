package bibliotheque.state;

import bibliotheque.Abonne;
import bibliotheque.document.Document;
import Exception.*;

public interface DocumentState {
    boolean canTake(Document doc, Abonne ab);

    void reservation(Document doc, Abonne ab) throws ReservationException;

    void emprunt(Document doc, Abonne ab) throws EmpruntException;

    void retour(Document doc) throws RetourException;
}