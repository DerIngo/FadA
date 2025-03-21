package deringo.fada;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

import deringo.fada.service.MailService;

@Service
public class FadAFetcherService3 {
    private static final Logger logger = LoggerFactory.getLogger(FadAFetcherService3.class);

    @Value("${dataFile}")
    private String dataFile;
    
    @Value("${audioFilePath}")
    private String audioFilePath;
    
    @Value("${saveMP3}")
    private boolean saveMP3;
    
    @Value("${sendEMail}")
    private boolean sendEMail;
    
    
    @Autowired
    private MailService mailService;
    
    public void doIt(String url) {
        SyndFeed urlFeed = getFeed(url);
        SyndFeed locFeed = getLocalFeed(dataFile);
        int locFeedSize = locFeed.getEntries().size();
        for (SyndEntry entry : urlFeed.getEntries()) {
            System.out.println("URI: " + entry.getUri());
            if (checkIfEntryExists(locFeed, entry.getUri())) {
                // Item bereits vorhanden
                logger.debug(String.format("Skip Item '%s', it already exists.", entry.getUri()));
                continue;
            }
            logger.info(String.format("Found new Item '%s'", entry.getUri()));
            
            if (saveMP3) {
                String fileurl = entry.getEnclosures().getFirst().getUrl();
                String filename = StringUtils.substringAfterLast(fileurl, "/");
                boolean saved = saveMP3(fileurl, filename);
                if (!saved) {
                    logger.warn(String.format("Could not save file '%s', skip Item '%s'. ", filename, entry.getUri()));
                    continue;
                }
            }
            
            if (sendEMail) {
                mailService.sendEMail(entry);
            }

            locFeed.getEntries().add(entry);
        }
        
        if (locFeed.getEntries().size() > locFeedSize) {
            saveLocalFeed(locFeed, dataFile);
        }
    }
    
    public static SyndFeed getFeed(String url) {
        try {
            @SuppressWarnings("deprecation")
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            @SuppressWarnings("deprecation")
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            
            return feed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Could not get Feed: " + url + e.getLocalizedMessage());
            return new SyndFeedImpl();
        }
    }
    
    private SyndFeed getLocalFeed(String filename) {
        try {
            File feedFile = new File(filename);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedFile));
            return feed;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Could not get Feed: " + filename + e.getLocalizedMessage());
            return null;
        }
    }
    
    private void saveLocalFeed(SyndFeed feed, String filename) {
        try {
            File outputFile = new File(filename);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, new FileWriter(outputFile));
            logger.info("Feed wurde erfolgreich aktualisiert und gespeichert!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Could not save Feed into file: " + filename + e.getLocalizedMessage());
        }
    }
    
    private static boolean checkIfEntryExists(SyndFeed feed, String guid) {
        List<SyndEntry> entries = feed.getEntries();

        for (SyndEntry entry : entries) {
            // Vergleiche die GUID des Eintrags mit der Ziel-GUID
            if (StringUtils.equals(StringUtils.trim(guid), StringUtils.trim(entry.getUri()))) {
                return true;
            }
        }

        return false;
    }
    
    private boolean saveMP3(String url, String filename) {
        try {
            File target = new File(audioFilePath+filename);
            if (target.exists()) {
                String message = String.format("Tried to save file %s, but it already exists. (TotalSpace: %s)", filename, target.getTotalSpace());
                logger.warn(message);
                return true;
            }
            FileUtils.copyURLToFile(new URI(url).toURL(), target);
            return true;
        } catch (IOException | URISyntaxException e) {
            String message = String.format("Could not save %s", filename);
            logger.error(message);
            return false;
        }
    }

}