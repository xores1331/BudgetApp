package pk.edu.pl.PASiR_Sosin_Jakub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}