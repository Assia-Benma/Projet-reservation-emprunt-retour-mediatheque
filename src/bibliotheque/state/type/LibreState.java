package bibliotheque.state.type;
import bibliotheque.Abonne;
import bibliotheque.document.Document;
import Exception.*;
import bibliotheque.state.DocumentState;

public class LibreState implements DocumentState {

    @Override
    public boolean canTake(Document doc, Abonne ab){
        return !ab.estBanni();
    }

    @Override
    public void reservation(Document doc, Abonne ab) {
        if(canTake(doc, ab)){
            doc.setState(new ReserveState());
            doc.setAbonneDeLaReservationDuDocument(ab);
        }
    }

    @Override
    public void emprunt(Document doc, Abonne ab) {
        if(canTake(doc, ab)){
            doc.setState(new EmprunteState());
            doc.setAbonneDeLaReservationDuDocument(ab);
        }
    }

    @Override
    public void retour(Document doc) throws RetourException {
        throw new RetourException();
    }
}
