package pk.edu.pl.PASiR_Sosin_Jakub.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.BalanceDto;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.TransactionDTO;
import pk.edu.pl.PASiR_Sosin_Jakub.model.Transaction;
import pk.edu.pl.PASiR_Sosin_Jakub.model.TransactionType;
import pk.edu.pl.PASiR_Sosin_Jakub.model.User;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.TransactionRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getAllTransactions(){
        User user = getCurrentUser();
        return transactionRepository.findAllByUser(user);
    }

    public Transaction getTramsactionById(Long id){
        return transactionRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Nie znaleziono transakcji o ID: "+id));
    }

    public Transaction saveTransactions(TransactionDTO transactionDetails){

        Transaction transaction = new Transaction(
                transactionDetails.getAmount(),
                transactionDetails.getType(),
                transactionDetails.getTags(),
                transactionDetails.getNotes(),
                getCurrentUser(),
                LocalDateTime.now()
        );

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, TransactionDTO transactionDTO){
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Nie znaleziono transakcji o ID: "+id));

        if (!transaction.getUser().getEmail().equals(getCurrentUser().getEmail()))
            throw new SecurityException("Brak dostepu do edycji tej transkacji");

        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(transactionDTO.getType());
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());
        transaction.setTimestamp(transactionDTO.getTimestamp());

        return transactionRepository.save(transaction);
    }

    public Transaction removeTransaction(Long id){
        Transaction deletedTransaction = transactionRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Nie znaleziono transakcji o ID: "+id));
        try {
            transactionRepository.deleteById(id);
            return deletedTransaction;
        } catch (Exception e) {
            throw new RuntimeException("Błąd przy usuwaniu transakcji "+e);
        }
    }

    public User getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("Nie znaleziono zalogowanego uzytkownika"));
    }

    public BalanceDto getUserBalance(User user, Float days) {
        List<Transaction> userTransactions = transactionRepository.findAllByUser(user);

        if(days!=null){
            LocalDateTime fromDate=LocalDateTime.now().minusSeconds(Math.round(days*86400));
            userTransactions=userTransactions.stream().filter(t->t.getTimestamp().isAfter(fromDate)).toList();
        }

        double income = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return new BalanceDto(income, expense, income - expense);
    }
}
