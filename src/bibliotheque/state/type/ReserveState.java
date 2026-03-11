package bibliotheque.state.type;
import bibliotheque.Abonne;
import bibliotheque.document.Document;
import Exception.*;
import bibliotheque.state.DocumentState;

import java.time.LocalDate;

public class ReserveState implements DocumentState {
    private LocalDate dateReservation;

    ReserveState() {
        dateReservation = LocalDate.now();
    }

    @Override
    public boolean canTake(Document doc, Abonne ab){
        if(ab.estBanni()){
            return false;
        }
        return doc.getAbonneDeLaReservationDuDocument() == ab;
    }

    @Override
    public void reservation(Document doc, Abonne ab) {
        throw new ReservationException();
    }

    @Override
    public void emprunt(Document doc, Abonne ab) {
        if(canTake(doc, ab)){
            doc.setState(new EmprunteState());
            doc.setAbonneDeLaReservationDuDocument(ab);
        }
        else{
            throw new EmpruntException();
        }
    }

    @Override
    public void retour(Document doc) throws RetourException {
        throw new RetourException();
    }
}
