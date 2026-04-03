package bibliotheque.mediatheque.client;

public class RetourClient extends BaseClient {
    @Override
    protected int defaultPort() {
        return 2002;
    }

    @Override
    protected String clientName() {
        return "retour";
    }

    public static void main(String[] args) {
        new RetourClient().run(args);
    }
}
