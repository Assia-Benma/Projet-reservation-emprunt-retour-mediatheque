package bibliotheque.document.type;

import bibliotheque.document.Document;

public class Livre extends Document {
    private final int nbPage;

    public Livre(String id, int nbPage, String titre) {
        super(id, titre);
        this.nbPage = nbPage;
    }

    public int getNbPage() {
        return nbPage;
    }
}

