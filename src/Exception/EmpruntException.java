package Exception;

public class EmpruntException extends RuntimeException {
    public EmpruntException() {
        super("Impossible d'emprunter");
    }
}
