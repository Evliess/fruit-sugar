package evliess.io.jpa;

import evliess.io.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User, Long> {
  @Query("SELECT a from User a WHERE a.name= :name")
  User getUserByName(String name);

}
