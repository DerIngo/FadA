package deringo.fada.scheduler;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import deringo.fada.service.FadAFetcherService;

@Component
public class FadAScheduler {
    private static final Logger logger = LoggerFactory.getLogger(FadAScheduler.class);

    @Autowired
    private FadAFetcherService fadAFetcherService;
    
    @Value("${currentUrl}")
    private String currentUrl;
    
    @Value("${historyUrl}")
    private String historyUrl;

    
    @Scheduled(cron = "${scheduling.task.cron}", zone = "${scheduling.task.zone}")
    public void fetchNewEpisodes() {
        logger.info("Suche nach neuen Podcast-Folgen: " + LocalDateTime.now());
        fadAFetcherService.doIt(currentUrl);
        fadAFetcherService.doIt(historyUrl);
    }
}