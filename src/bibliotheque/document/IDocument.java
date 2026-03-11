package bibliotheque.document;
import Exception.*;
import bibliotheque.Abonne;


public interface IDocument {
    String idDoc();
    // exception si déjà réservé ou emprunté
    void reservation (Abonne ab) throws ReservationException;
    // exception si réservé pour une autre abonné ou déjà emprunté
    void emprunt(Abonne ab) throws EmpruntException;
    // sert au retour d’un document ou à l’annulation d‘une réservation
    void retour() throws RetourException;

}
