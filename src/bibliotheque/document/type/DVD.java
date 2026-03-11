package bibliotheque.document.type;
import Exception.*;
import bibliotheque.Abonne;
import bibliotheque.document.Document;

public class DVD extends Document {
    private boolean adulte;

    public DVD(String id, String titre, boolean adulte) {
        super(id, titre);
        this.adulte = adulte;
    }

    public boolean getAdulte() {
        return adulte;
    }

    @Override
    public boolean canTake(Abonne ab) {
        if (this.adulte) {
            if (ab.isAdult()) {
                return state.canTake(this, ab);
            }
            else{
                return false;
            }
        }
        else {
            return state.canTake(this, ab);
        }
    }

    @Override
    public void reservation(Abonne ab) throws ReservationException {
        if(this.canTake(ab)){
            state.reservation(this, ab);
        }
        else{
            throw new ReservationException();
        }
    }

    @Override
    public void emprunt(Abonne ab) throws EmpruntException {
        if(this.canTake(ab)){
            state.emprunt(this, ab);
        }
        else{
            throw new EmpruntException();
        }
    }
}
