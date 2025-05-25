package pk.edu.pl.PASiR_Sosin_Jakub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.model.Group;
import pk.edu.pl.PASiR_Sosin_Jakub.model.User;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByMemberships_User(User user);
}
