package Exception;

public class ReservationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReservationException() {
        super("Impossible de reserver le document.");
    }

    public ReservationException(String message) {
        super(message);
    }
}
