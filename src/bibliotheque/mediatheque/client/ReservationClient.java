package bibliotheque.mediatheque.client;

public class ReservationClient extends BaseClient {
    @Override
    protected int defaultPort() {
        return 2000;
    }

    @Override
    protected String clientName() {
        return "reservation";
    }

    public static void main(String[] args) {
        new ReservationClient().run(args);
    }
}
