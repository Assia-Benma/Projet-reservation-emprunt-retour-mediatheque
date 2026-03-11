package bibliotheque;
import java.time.*;

public class Abonne {
    private int numero;
    private String nom;
    private LocalDate dateNaissance;
    private LocalDate dateFinBan;


    public Abonne(int numero, String nom, LocalDate dateNaissance) {
        this.numero = numero;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.dateFinBan = null;
    }

    public int getNumero() { return numero; }
    public String getNom() { return nom; }

    public boolean isAdult() {
        return Period.between(dateNaissance, LocalDate.now()).getYears() >= 16;
    }

    public void bannir(int dureeJours) {
        dateFinBan = LocalDate.now().plusDays(dureeJours);
    }

    public boolean estBanni() {
        if (dateFinBan == null) {
            return false;
        }
        return LocalDate.now().isBefore(dateFinBan);
    }

}