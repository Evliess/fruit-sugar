package evliess.io.jpa;

import evliess.io.entity.ContentModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContentModuleRepo extends JpaRepository<ContentModule, Long> {

  @Query("SELECT a from ContentModule a WHERE a.name= :name")
  ContentModule getByName(String name);

  @Query("SELECT a from ContentModule a WHERE a.description= :description")
  ContentModule getByDescription(String description);
}
