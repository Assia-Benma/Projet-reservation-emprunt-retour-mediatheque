package bibliotheque;

import java.time.LocalDate;
import java.time.Period;

public class Abonne {
    private final int numero;
    private final String nom;
    private final LocalDate dateNaissance;
    private LocalDate dateFinBan;

    public Abonne(int numero, String nom, LocalDate dateNaissance) {
        this.numero = numero;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.dateFinBan = null;
    }

    public int getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public LocalDate getDateFinBan() {
        return dateFinBan;
    }

    public boolean isAdult() {
        return Period.between(dateNaissance, LocalDate.now()).getYears() >= 16;
    }

    public void bannir(int dureeJours) {
        dateFinBan = LocalDate.now().plusDays(dureeJours);
    }

    public boolean estBanni() {
        return dateFinBan != null && LocalDate.now().isBefore(dateFinBan);
    }
}

