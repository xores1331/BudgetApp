package pk.edu.pl.PASiR_Sosin_Jakub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.model.Debt;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByGroupId(Long groupId);
}
