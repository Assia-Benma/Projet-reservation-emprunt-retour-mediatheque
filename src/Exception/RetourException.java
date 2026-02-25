package Exception;

public class RetourException extends RuntimeException {
    public RetourException() {
        super("Impossible de retourner");
    }
}
