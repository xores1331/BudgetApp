package pk.edu.pl.PASiR_Sosin_Jakub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.model.Transaction;
import pk.edu.pl.PASiR_Sosin_Jakub.model.User;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findAllByUser(User user);
}
