package pk.edu.pl.PASiR_Sosin_Jakub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.model.Membership;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByGroupId(Long groupId);
}
