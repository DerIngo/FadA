package deringo.fada;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import deringo.fada.entity.Item;
import deringo.fada.service.ItemService;

@Service
public class ImportOldData {
    private static final Logger logger = LoggerFactory.getLogger(ImportOldData.class);
    
    @Autowired
    private ItemService itemService;
    
    public void doIt() {
        Document doc = getDataDocument();
        Elements items = doc.select("item");
        System.out.println("items.size: " + items.size());
        for (Element item : items) {
            String guid = StringUtils.trim(item.select("guid").text());
            if (itemService.itemExists(guid)) {
                // Item bereits vorhanden
                logger.debug(String.format("Skip Item '%s', it already exists.", guid));
                // PubDate aktualisieren
                Optional<Item> optionalItem = itemService.findPodcastById(guid);
                if (optionalItem.isPresent() && optionalItem.get().getPubDate() == null) {
                    Date pubDate = new Date( toString(item, "pubDate") );
                    optionalItem.get().setPubDate(pubDate);
                    itemService.savePodcast(optionalItem.get());
                }
                continue;
            }
            logger.info(String.format("Found new Item '%s'", guid));
            
            // Build and save new Item
            String title = toString(item, "title");
            String description = toString(item, "description");
            String content = toString(item, "content\\:encoded");
            Date pubDate = new Date( toString(item, "pubDate") );
            
            String url = item.select("enclosure").attr("url");
            String filename = StringUtils.substringAfterLast(item.select("enclosure").attr("url"), "/");
            Long length = NumberUtils.toLong(item.select("enclosure").attr("length"));
            String type = item.select("enclosure").attr("type");
            
            String subtitle = toString(item, "itunes\\:subtitle");
            String summary = toString(item, "itunes\\:summary");
            String image = toString(item, "itunes\\:image");
            String author = toString(item, "itunes\\:author");
            String keywords = toString(item, "itunes\\:keywords");
            Item item2 = new Item(guid, title, description, content, pubDate, url, filename, length, type, subtitle, summary, image, author, keywords);
            itemService.savePodcast(item2);

            System.out.println("Item saved: " + item2);
        }
        
    }
    
    private static String toString(Element item, String key) {
        String value = StringUtils.trim(item.select(key).html());
        return value;
    }
    
    private static Document getDataDocument() {
        String dataFile = "data.xml";
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
}