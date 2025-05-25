package pk.edu.pl.PASiR_Sosin_Jakub.controllers;

import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.BalanceDto;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.TransactionDTO;
import pk.edu.pl.PASiR_Sosin_Jakub.model.Transaction;
import pk.edu.pl.PASiR_Sosin_Jakub.model.User;
import pk.edu.pl.PASiR_Sosin_Jakub.service.TransactionService;

import java.util.List;

@Controller
public class TransactionGraphQLController {

    private final TransactionService transactionService;

    public TransactionGraphQLController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @QueryMapping
    public List<Transaction> transactions(){
        return transactionService.getAllTransactions();
    }

    @QueryMapping
    public BalanceDto userBalance(@Argument Float days){
        User user = transactionService.getCurrentUser();
        return transactionService.getUserBalance(user,days);
    }

    @MutationMapping
    public Transaction addTransaction(@Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.saveTransactions(transactionDTO);
    }

    @MutationMapping
    public Transaction updateTransaction(@Argument Long id, @Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.updateTransaction(id,transactionDTO);
    }

    @MutationMapping
    public Boolean deleteTransaction(@Argument Long id) {
        transactionService.removeTransaction(id);
        return true;
    }
}

