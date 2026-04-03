package bibliotheque.mediatheque.client;

public class EmpruntClient extends BaseClient {
    @Override
    protected int defaultPort() {
        return 2001;
    }

    @Override
    protected String clientName() {
        return "emprunt";
    }

    public static void main(String[] args) {
        new EmpruntClient().run(args);
    }
}
