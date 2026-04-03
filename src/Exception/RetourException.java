package Exception;

public class RetourException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RetourException() {
        super("Impossible de retourner le document.");
    }

    public RetourException(String message) {
        super(message);
    }
}
