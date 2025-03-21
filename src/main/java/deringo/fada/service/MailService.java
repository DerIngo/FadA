package deringo.fada.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import deringo.fada.config.MailConfig;
import deringo.fada.entity.Item;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
@ComponentScan
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private MailConfig mailConfig;
    
    public void sendEMail(Item entry) {
        String subject = "Eine neue Folge von 'Fragen an den Autor' wurde hinzugefügt.";
        String body = getEMailBody(entry);
        sendMailToRecipients(subject, body);
    }

    private String getEMailBody(Item entry) {
        SimpleDateFormat germanFormat = new SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.GERMAN);
        String body = "<html><body>";
        try {
          String title        = entry.getTitle();
          String pubDate      = germanFormat.format(entry.getPubDate());
          String description  = entry.getDescription();
          String link         = "<a href=\"https://kabango.eu/fada/\">Link</a>";
          body = title + "<br/><br/>" + pubDate + "<br/><br/>" + description + "<br/><br/>" + link;
        } catch (Exception e) {
          body = body + e.toString();
        }
        body = body + /*item.toString() + */"</body></html>";
        return body;
    }
    
    private void sendMailToRecipients(String subject, String text) {
        for (String recipient : mailConfig.getRecipients()) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                // Setze den Absender mit Namen aus der Konfiguration
                helper.setFrom(new InternetAddress(mailConfig.getSender().getEmail(), mailConfig.getSender().getName()));

                helper.setTo(recipient);
                helper.setSubject(subject);
                helper.setText(text, true);

                mailSender.send(message);
            } catch (MessagingException | UnsupportedEncodingException e) {
                logger.error(e.toString());
            }
        }
    }
}