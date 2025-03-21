package deringo.fada.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deringo.fada.entity.Item;
import deringo.fada.service.ItemService;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    
    @Autowired
    private ItemService itemService;
    
    @GetMapping("/hello")
    public String index() {
        logger.info("Greetings from Spring Boot!");
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/feed")
    public List<Item> podcasts() {
        return itemService.getAllPodcasts();
    }
    
}