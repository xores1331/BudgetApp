package pk.edu.pl.PASiR_Sosin_Jakub.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.DebtDTO;
import pk.edu.pl.PASiR_Sosin_Jakub.model.*;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.DebtRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.GroupRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.TransactionRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.UserRepository;

import java.util.List;

@Service
public class DebtService {
    private final DebtRepository debtRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DebtService(DebtRepository debtRepository, GroupRepository groupRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.debtRepository = debtRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Debt> getGroupDebts(Long groupId) {
        return debtRepository.findByGroupId(groupId);
    }

    public Debt createDebt(DebtDTO debtDTO) {
        Group group = groupRepository.findById(debtDTO.getGroupId()).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono grupy o id: " + debtDTO.getGroupId())
        );

        User debtor = userRepository.findById(debtDTO.getDebtorId()).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono dluznika o id: " + debtDTO.getDebtorId())
        );

        User creditor = userRepository.findById(debtDTO.getCreditorId()).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono wierzycie o id: " + debtDTO.getCreditorId())
        );

        Debt debt = new Debt();
        debt.setGroup(group);
        debt.setDebtor(debtor);
        debt.setCreditor(creditor);
        debt.setAmount(debtDTO.getAmount());
        debt.setTitle(debtDTO.getTitle());

        return debtRepository.save(debt);
    }

    public void deleteDebt(Long debtId, User currentUser) {
        Debt debt = debtRepository.findById(debtId).orElseThrow(
                () -> new EntityNotFoundException("Dlug o id: " + debtId + "nie zostal odnaleziony")
        );

        if(!debt.getCreditor().getId().equals(currentUser.getId())) {
            throw new SecurityException("Tylko wierzyciel moze usunac dlug!");
        }

        debtRepository.delete(debt);
    }

    public boolean markAsPaid(Long debtId, User user) {
        Debt debt = debtRepository.findById(debtId).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono dlugu")
        );

        if(!debt.getDebtor().getId().equals(user.getId())) {
            throw new SecurityException("Nie jestes dluznikiem");
        }

        debt.setMarkedAsPaid(true);
        debtRepository.save(debt);
        return true;
    }

    public boolean confirmPayment(Long debtId, User user) {
        Debt debt = debtRepository.findById(debtId).orElseThrow(
                () -> new EntityNotFoundException("Nie znaleziono dlugu")
        );

        if(!debt.getCreditor().getId().equals(user.getId())) {
            throw new SecurityException("Nie jestes wierzycielem");
        }

        if(!debt.isMarkedAsPaid()){
            throw new IllegalStateException("Dluznik nie oznaczyl jeszcze jako oplacone");
        }

        debt.setConfirmedByCreditor(true);
        debtRepository.save(debt);

        Transaction incomeTx = new Transaction(
                debt.getAmount(),
                TransactionType.INCOME,
                "Splata dlugu",
                "Splata dlugo od: " + debt.getDebtor().getEmail(),
                debt.getCreditor()

        );
        transactionRepository.save(incomeTx);

        Transaction expenseTx = new Transaction(
                debt.getAmount(),
                TransactionType.EXPENSE,
                "Splata dlugu",
                "Splata dlugu dla: " + debt.getCreditor().getEmail(),
                debt.getDebtor()
        );

        transactionRepository.save(expenseTx);

        return true;
    }
}
