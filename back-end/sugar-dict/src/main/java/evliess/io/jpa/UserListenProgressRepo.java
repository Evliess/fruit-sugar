package evliess.io.jpa;

import evliess.io.entity.UserListenProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserListenProgressRepo
  extends JpaRepository<UserListenProgress, Long> {
  @Query("SELECT u FROM UserListenProgress u WHERE u.userId = :userId and u.moduleId = :moduleId and u.type = :type")
  UserListenProgress getIndex(@Param("userId") Long userId, @Param("type") String type, @Param("moduleId") Long moduleId);
}
