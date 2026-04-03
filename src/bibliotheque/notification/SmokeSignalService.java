package bibliotheque.notification;

import bibliotheque.document.Document;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

public class SmokeSignalService {
    private final String smtpHost = System.getenv().getOrDefault("MEDIATHEQUE_SMTP_HOST", "localhost");
    private final String smtpPort = System.getenv().getOrDefault("MEDIATHEQUE_SMTP_PORT", "25");
    private final String sender = System.getenv().getOrDefault("MEDIATHEQUE_SMOKE_FROM", "noreply@mediatheque.local");

    public void sendTestCloud(String recipient) {
        sendSmokeSignal(recipient, "[Mediatheque] Nuage de test", "Signal de fumee de test envoye avec succes.");
    }

    public void sendAvailabilityAlerts(Document document, List<String> recipients) {
        if (recipients == null || recipients.isEmpty()) {
            return;
        }

        String subject = "[Mediatheque] Document disponible: " + document.idDoc();
        String body = "Le document " + document.idDoc() + " (" + document.getTitre() + ") est de nouveau disponible.";

        for (String recipient : recipients) {
            sendSmokeSignal(recipient, subject, body);
        }
    }

    private void sendSmokeSignal(String recipient, String subject, String body) {
        if (recipient == null || recipient.isBlank()) {
            return;
        }

        boolean sentWithMailApi = trySendWithJavaxMail(recipient.trim(), subject, body);
        if (!sentWithMailApi) {
            System.out.println("[SmokeSignal][fallback] Envoi simule vers " + recipient + " | " + subject);
        }
    }

    private boolean trySendWithJavaxMail(String recipient, String subject, String body) {
        try {
            Class<?> sessionClass = Class.forName("javax.mail.Session");
            Class<?> transportClass = Class.forName("javax.mail.Transport");
            Class<?> messageClass = Class.forName("javax.mail.Message");
            Class<?> addressClass = Class.forName("javax.mail.Address");
            Class<?> internetAddressClass = Class.forName("javax.mail.internet.InternetAddress");
            Class<?> mimeMessageClass = Class.forName("javax.mail.internet.MimeMessage");

            Properties properties = new Properties();
            properties.setProperty("mail.smtp.host", smtpHost);
            properties.setProperty("mail.smtp.port", smtpPort);

            Method getInstance = sessionClass.getMethod("getInstance", Properties.class);
            Object session = getInstance.invoke(null, properties);

            Object message = mimeMessageClass.getConstructor(sessionClass).newInstance(session);
            Object fromAddress = internetAddressClass.getConstructor(String.class).newInstance(sender);
            Method setFrom = mimeMessageClass.getMethod("setFrom", addressClass);
            setFrom.invoke(message, fromAddress);

            Class<?> recipientTypeClass = Class.forName("javax.mail.Message$RecipientType");
            Field to = recipientTypeClass.getField("TO");
            Object toValue = to.get(null);

            Object toAddress = internetAddressClass.getConstructor(String.class).newInstance(recipient);
            Object addressArray = Array.newInstance(addressClass, 1);
            Array.set(addressArray, 0, toAddress);

            Method setRecipients = mimeMessageClass.getMethod("setRecipients", recipientTypeClass, addressArray.getClass());
            setRecipients.invoke(message, toValue, addressArray);

            Method setSubject = mimeMessageClass.getMethod("setSubject", String.class);
            setSubject.invoke(message, subject);

            Method setText = mimeMessageClass.getMethod("setText", String.class);
            setText.invoke(message, body);

            Method send = transportClass.getMethod("send", messageClass);
            send.invoke(null, message);
            return true;
        } catch (ReflectiveOperationException reflectiveFailure) {
            return false;
        }
    }
}

