package framework;

import customobjects.User;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Simple helper class for interacting with an IMAP email server
 * It's recommended that you use an email provider which supports filters in email addresses
 * (i.e email+user1@example.com, email+user2@gmail.com) as this allows you to create as many users in the application
 * under test as you need, while only needing a single email account.
 * If you are using this with GMail then you will need to enable the Insecure Apps option in your Google account settings
 * TODO Add an OAuth2 connection option
 */
public class EmailHelper {

    private Folder inbox;

    private final String PASSWORD_RESET_TITLE = "Forgot Password from the-internet";
    private final long SEARCH_FREQUENCY = 5000;
    private final long SEARCH_TIMEOUT = 30000;

    public EmailHelper() {

        try {
            PropertyManager props = new PropertyManager();
            Properties mailServerProps = new Properties();

            mailServerProps.put("mail.store.protocol", "imaps");
            mailServerProps.put("mail.imap.ssl.enable", "true");
            //mailServerProps.put("mail.debug", "true");

            Session session = Session.getInstance(mailServerProps);
            Store store = session.getStore("imaps");

            store.connect(props.getImapHost(), props.getImapPort(), props.getImapUsername(), props.getImapPassword());

            inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_WRITE);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Make sure the inbox is closed properly
    protected void finalize() throws Throwable {

        if (inbox != null) {
            closeConnection();
        }

        super.finalize();
    }

    public void closeConnection() {

        try {
            inbox.close();
            inbox = null;
        }
        catch (MessagingException e) { e.printStackTrace(); }
    }

    public String waitForPasswordResetEmail(User user) {

        Message email = waitForEmail(user.getEmailAddress(), PASSWORD_RESET_TITLE);
        String messageBody = "";

        if (email == null) {
            throw new IllegalStateException("Email did not arrive within " + SEARCH_TIMEOUT / 1000 + " seconds");
        }

        try {
            if (email.isMimeType("text/plain")) {
                messageBody = email.getContent().toString();
            }
            else {
                // TODO Handle multipart messages
            }

        }
        catch(MessagingException | IOException e) { e.printStackTrace(); }

        return messageBody;
    }


    private Message waitForEmail(String recipient, String subject){

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Object notifier = new Object();
        final Message[] email = new Message[1];

        // We'll use a runnable to check for the presence of the email so that we can execute it in a separate thread
        Runnable searchForEmail = new Runnable() {
            public void run() {
                synchronized (notifier) {
                    try {
                        // Fetch unseen messages from inbox
                        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

                        for (Message message : messages) {

                            if (message.getSubject().equalsIgnoreCase(subject) && message.getRecipients(Message.RecipientType.TO)[0].toString().equalsIgnoreCase(recipient)) {
                                email[0] = message;
                                message.setFlag(Flags.Flag.SEEN, true); // Once we've found the email mark it as read
                                notifier.notifyAll();
                            }
                        }
                    }
                    catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        // Create a thread which will execute the runnable every five seconds
        final ScheduledFuture<?> scheduledThread = scheduler.scheduleAtFixedRate(searchForEmail, 5, SEARCH_FREQUENCY, SECONDS);

        // Start our scheduled thread.  We'll resume the main thread once the email has been found or we hit our timeout
        synchronized (notifier) {
            try {
                notifier.wait(SEARCH_TIMEOUT);
                scheduledThread.cancel(false);
            }
            catch (InterruptedException e) { e.printStackTrace(); }
        }

        return email[0];
    }
}