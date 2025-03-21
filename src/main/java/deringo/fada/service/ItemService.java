package deringo.fada.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import deringo.fada.entity.Item;
import deringo.fada.repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    
    public List<Item> getAllPodcasts() {
        return itemRepository.findAll();
    }

    public Optional<Item> findPodcastById(String guid) {
        return itemRepository.findById(guid);
    }
    
    public boolean itemExists(String guid) {
        return itemRepository.existsById(guid);
    }
    
    public Item savePodcast(Item item) {
        return itemRepository.save(item);
    }
}