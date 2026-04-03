package bibliotheque.state.type;

import Exception.EmpruntException;
import Exception.ReservationException;
import bibliotheque.Abonne;
import Exception.RetourException;
import bibliotheque.document.Document;
import bibliotheque.state.DocumentState;

import java.time.LocalDateTime;

public class EmprunteState implements DocumentState {

    @Override
    public boolean canTake(Document doc, Abonne ab) {
        return false;
    }

    @Override
    public void reservation(Document doc, Abonne ab) {
        throw new ReservationException("Document deja emprunte : reservation impossible pour le moment.");
    }

    @Override
    public void emprunt(Document doc, Abonne ab) {
        throw new EmpruntException("Document deja emprunte : nouvel emprunt impossible.");
    }

    @Override
    public void retour(Document doc, boolean degradationConstatee) {
        Abonne emprunteur = doc.getEmprunteur();
        LocalDateTime dateEmprunt = doc.getEmpruntAt();

        boolean enRetard = dateEmprunt != null && LocalDateTime.now().isAfter(dateEmprunt.plusWeeks(2));
        if (emprunteur != null && (enRetard || degradationConstatee)) {
            emprunteur.bannir(30);
        }

        doc.markReturnedToShelf();
    }
}

