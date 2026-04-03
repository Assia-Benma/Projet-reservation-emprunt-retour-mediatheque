package bibliotheque.document.type;

import Exception.EmpruntException;
import Exception.ReservationException;
import bibliotheque.Abonne;
import bibliotheque.document.Document;

public class DVD extends Document {
    private final boolean adulte;

    public DVD(String id, String titre, boolean adulte) {
        super(id, titre);
        this.adulte = adulte;
    }

    public boolean isAdulte() {
        return adulte;
    }

    @Override
    public boolean canTake(Abonne ab) {
        if (adulte && !ab.isAdult()) {
            return false;
        }
        return super.canTake(ab);
    }

    @Override
    public void reservation(Abonne ab) throws ReservationException {
        if (adulte && !ab.isAdult()) {
            throw new ReservationException("Vous n'avez pas l'age requis (16+) pour reserver ce DVD adulte.");
        }
        super.reservation(ab);
    }

    @Override
    public void emprunt(Abonne ab) throws EmpruntException {
        if (adulte && !ab.isAdult()) {
            throw new EmpruntException("Vous n'avez pas l'age requis (16+) pour emprunter ce DVD adulte.");
        }
        super.emprunt(ab);
    }
}

