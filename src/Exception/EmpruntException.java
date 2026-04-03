package Exception;

public class EmpruntException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EmpruntException() {
        super("Impossible d'emprunter le document.");
    }

    public EmpruntException(String message) {
        super(message);
    }
}
