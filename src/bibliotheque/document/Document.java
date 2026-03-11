package bibliotheque.document;

import bibliotheque.Abonne;
import bibliotheque.state.DocumentState;
import bibliotheque.state.type.LibreState;
import Exception.*;

public abstract class Document implements IDocument {
    private String id;
    private String titre;
    protected DocumentState state;
    private Abonne abonneDeLaReservationDuDocument;

    public Document(String id, String titre) {
        this.id = id;
        this.titre = titre;
        this.state = new LibreState();
    }

    public void setState(DocumentState state) {
        this.state = state;
    }

    @Override
    public String idDoc() {
        return id;
    }

    @Override
    public void reservation(Abonne ab) throws ReservationException {
        state.reservation(this, ab);
    }

    @Override
    public void emprunt(Abonne ab) throws EmpruntException {
        state.emprunt(this, ab);
    }

    @Override
    public void retour() throws RetourException {
        state.retour(this);
    }

    public boolean canTake(Abonne ab) {
        return state.canTake(this, ab);
    }

    public void setAbonneDeLaReservationDuDocument(Abonne ab) {
        this.abonneDeLaReservationDuDocument = ab;
    }

    public Abonne getAbonneDeLaReservationDuDocument() {
        return abonneDeLaReservationDuDocument;
    }

    public static boolean degradation() {
        return Math.random() < 0.1;
    }
}
