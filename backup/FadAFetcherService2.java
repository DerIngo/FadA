package deringo.fada;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import deringo.fada.service.MailService;


@Service
public class FadAFetcherService2 {
    @Value("${currentUrl}")
    private String currentUrl;
    
    @Value("${historyUrl}")
    private String historyUrl;
    
    @Value("${dataFile}")
    private String dataFile;
    
    @Value("${audioFilePath}")
    private String audioFilePath;
    
    @Value("${saveMP3}")
    private boolean saveMP3;
    
    @Value("${sendEMail}")
    private boolean sendEMail;
    
    private static final Logger logger = LoggerFactory.getLogger(FadAFetcherService2.class);
    
    @Autowired
    private MailService mailService;
    
    //@Scheduled(cron = "${scheduling.task.cron}", zone = "${scheduling.task.zone}")
    public void fetchNewEpisodes() {
        logger.info("Suche nach neuen Podcast-Folgen: " + LocalDateTime.now());
        doIt(currentUrl);
        doIt(historyUrl);
    }

    private void doIt(String url) {
//System.out.println("HURZ");
        Document doc1 = getDocument1(url);
        Document doc2 = getDataDocument();
        Elements items = doc1.select("item");
        for (Element item : items) {
            String guid = item.select("guid").text();
            if (doc2.getElementsMatchingOwnText(guid).size() > 0) {
                // Item bereits vorhanden
                logger.debug(String.format("Skip Item '%s', it already exists.", guid));
                continue;
            }
//System.out.println("guid: " + guid);
//System.exit(0);
            logger.info(String.format("Found new Item '%s'", guid));
            
            // remove link (link-tag wird nicht geschlossen, JSOUP-Bug?)
            item.select("link").remove();
            
            // save MP3
            if (saveMP3) {
                String fileurl = item.select("enclosure").attr("url");
                String filename = StringUtils.substringAfterLast(item.select("enclosure").attr("url"), "/");
                boolean saved = saveMP3(fileurl, filename);
                if (!saved) {
                    logger.warn(String.format("Could not save file '%s', skip Item '%s'. ", filename, guid));
                    continue;
                }
            }
            
            //
//logger.warning(String.valueOf(doc2));
//logger.warning(String.valueOf(doc2.getElementById("items")));
//logger.warning(String.valueOf(item));
            doc2.getElementById("items").appendChild(item);

            // send EMail
            if (sendEMail) {
                sendEMail(item);
            }
            
//            System.out.println(item.select("guid").text());
//            System.out.println(item.select("pubDate").text());
//            System.out.println(item.select("title").text());
//            System.out.println(item.select("description").text());
//            System.out.println(item.select("enclosure").attr("url"));
//            System.out.println(StringUtils.substringAfterLast(item.select("enclosure").attr("url"), "/"));
        }
        
        saveDataDocument(doc2.getElementsByTag("items").outerHtml());
    }

    private Document getDocument1(String url) {
        try {
            InputStream input = new URI(url).toURL().openStream();
            Document doc = Jsoup.parse(input, "UTF-8", url); // previous used encoding: iso-8859-15
    //        Document doc = Jsoup.connect(url).get();
            return doc;
        } catch (IOException | URISyntaxException e) {
            String message = String.format("Could not load from URL: %s.\n%s", url, e.toString());
            logger.error(message);
            throw new RuntimeException(message);
        }
    }

    private Document getDataDocument() {
        try {
            File in = new File(dataFile);
            Document doc = Jsoup.parse(in, "UTF-8");
            return doc;
        } catch (IOException e) {
            String message = "Could not load DataDocument.\n"+e.toString();
            logger.error(message);
            throw new RuntimeException(message);
        }
    }


    private void saveDataDocument(String items) {
        logger.debug("Start saving DataDocument");
        try {
            File in = new File(dataFile);
            Writer writer = new BufferedWriter(new FileWriter(in));
            writer.write(items);
            writer.close();
            logger.debug("DataDocument saved.");
        } catch (IOException e) {
            String message = "Could not save DataDocument.\n"+e.toString();
            logger.error(message);
            throw new RuntimeException(message);
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
    
    private void sendEMail(Element item) {
        mailService.sendEMail(item);
    }
}