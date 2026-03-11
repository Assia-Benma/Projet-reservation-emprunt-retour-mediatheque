package bibliotheque.document.type;

import bibliotheque.document.Document;

public class Livre extends Document {
    private final int NbPage;

    public Livre(String id, int nbPage, String titre) {
        super(id, titre);
        this.NbPage = nbPage;
    }

}
