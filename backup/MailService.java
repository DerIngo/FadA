package deringo.fada.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.rometools.rome.feed.synd.SyndEntry;

import deringo.fada.config.MailConfig;
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
    
    public void sendEMail(Element item) {
        String subject = "Eine neue Folge von 'Fragen an den Autor' wurde hinzugefügt.";
        String body = getEMailBody(item);
        sendMailToRecipients(subject, body);
    }

    public void sendEMail(SyndEntry entry) {
        String subject = "Eine neue Folge von 'Fragen an den Autor' wurde hinzugefügt.";
        String body = getEMailBody(entry);
        sendMailToRecipients(subject, body);
    }

    private String getEMailBody(SyndEntry entry) {
        SimpleDateFormat germanFormat = new SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.GERMAN);
        String body = "<html><body>";
        try {
          String title        = entry.getTitle();
          String pubDate      = germanFormat.format(entry.getPublishedDate());
          String description  = entry.getDescription().getValue();
          String link         = "<a href=\"https://kabango.eu/fada/\">Link</a>";
          body = title + "<br/><br/>" + pubDate + "<br/><br/>" + description + "<br/><br/>" + link;
        } catch (Exception e) {
          body = body + e.toString();
        }
        body = body + /*item.toString() + */"</body></html>";
        return body;
    }
    
    private String getEMailBody(Element item) {
        String body = "<html><body>";
        try {
          String title        = item.select("title").text();
          String pubDate      = item.select("pubdate").text();
          String description  = item.select("description").text();
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