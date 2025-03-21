package deringo.fada.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import deringo.fada.entity.Item;

@Service
public class FadAFetcherService {
    private static final Logger logger = LoggerFactory.getLogger(FadAFetcherService.class);

    @Value("${dataFile}")
    private String dataFile;
    
    @Value("${audioFilePath}")
    private String audioFilePath;
    
    @Value("${saveMP3}")
    private boolean saveMP3;
    
    @Value("${sendEMail}")
    private boolean sendEMail;
    
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private MailService mailService;
    
    public void doIt(String feedUrl) {
        SyndFeed urlFeed = getFeed(feedUrl);
        for (SyndEntry entry : urlFeed.getEntries()) {
            System.out.println("URI: " + entry.getUri());
            if (itemService.itemExists(entry.getUri())) {
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

            // Build and save new Item
            String guid = entry.getUri();
            String title = entry.getTitle();
            String description = entry.getDescription().getValue(); 
            String content = syndContentListToString(entry.getContents());
            Date pubDate = entry.getPublishedDate();
            
            String url = entry.getEnclosures().get(0).getUrl();
            String filename = StringUtils.substringAfterLast(url, "/");
            Long length = entry.getEnclosures().get(0).getLength();
            String type = entry.getEnclosures().get(0).getType();
            
            String subtitle = toString("subtitle", entry.getForeignMarkup());
            String summary = toString("summary", entry.getForeignMarkup());
            String image = toString("image", entry.getForeignMarkup());
            String author = toString("author", entry.getForeignMarkup());
            String keywords = toString("keywords", entry.getForeignMarkup());
            Item item = new Item(guid, title, description, content, pubDate, url, filename, length, type, subtitle, summary, image, author, keywords);
            itemService.savePodcast(item);

            if (sendEMail) {
                mailService.sendEMail(item);
            }
        }
    }
    
    private static String toString(String key, List<Element> foreignMarkup) {
        StringBuilder contentBuilder = new StringBuilder();
        for (Element e : foreignMarkup) {
            if (StringUtils.equals(key, e.getName())) {
                List<Content> contents = e.getContent();
                for (int i = 0; i < contents.size(); i++) {
                    contentBuilder.append(contents.get(i).getValue());
                    // Füge Zeilenumbruch nur hinzu, wenn es nicht das letzte Element ist
                    if (i < contents.size() - 1) {
                        contentBuilder.append("\n");
                    }
                }
                if (StringUtils.equals("image", key)) {
                    contentBuilder.append(e.getAttribute("href").getValue());
                }
            }
        }
        
        String contentString = contentBuilder.toString();
        return contentString;
    }
    
    private static String syndContentListToString(List<SyndContent> contents) {
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < contents.size(); i++) {
            contentBuilder.append(contents.get(i).getValue());
            // Füge Zeilenumbruch nur hinzu, wenn es nicht das letzte Element ist
            if (i < contents.size() - 1) {
                contentBuilder.append("\n");
            }
        }
        String contentString = contentBuilder.toString();
        return contentString;
    }
    
    private static SyndFeed getFeed(String url) {
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