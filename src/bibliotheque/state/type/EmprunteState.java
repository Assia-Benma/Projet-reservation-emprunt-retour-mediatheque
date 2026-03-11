package bibliotheque.state.type;

import bibliotheque.document.Document;
import bibliotheque.Abonne;
import Exception.*;
import bibliotheque.state.DocumentState;

import java.time.LocalDate;

public class EmprunteState implements DocumentState {
    private LocalDate dateEmprunt;

    EmprunteState() {
        this.dateEmprunt = LocalDate.now();
    }

    @Override
    public boolean canTake(Document doc, Abonne ab){
        return false;
    }

    @Override
    public void reservation(Document doc, Abonne ab) throws ReservationException {
        throw new ReservationException();
    }

    @Override
    public void emprunt(Document doc, Abonne ab) throws EmpruntException {
        throw new EmpruntException();
    }

    @Override
    public void retour(Document doc) {
        doc.setState(new LibreState());
        if(Document.degradation() || LocalDate.now().isAfter(dateEmprunt.plusWeeks(2))){
            doc.getAbonneDeLaReservationDuDocument().bannir(30);
        }
        doc.setAbonneDeLaReservationDuDocument(null);
    }
}
