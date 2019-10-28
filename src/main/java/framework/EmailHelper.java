package framework;

import javax.mail.*;
import java.util.Properties;


public class EmailHelper {

    private Store store;



    public EmailHelper() {

        try {
            PropertyManager props = new PropertyManager();
            Properties mailServerProps = new Properties();

            mailServerProps.put("mail.store.protocol", "imaps");
            mailServerProps.put("mail.imap.ssl.enable", "true");
            //mailServerProps.put("mail.debug", "true");

            Session session = Session.getInstance(mailServerProps);
            store = session.getStore("imaps");

            store.connect(props.getImapHost(), props.getImapPort(), props.getImapUsername(), props.getImapPassword());
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}