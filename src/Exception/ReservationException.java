package Exception;

public class ReservationException extends RuntimeException {
    public ReservationException() {
        super("Impossible de resrver");
    }
}
