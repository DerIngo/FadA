package deringo.fada.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import deringo.fada.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

}
