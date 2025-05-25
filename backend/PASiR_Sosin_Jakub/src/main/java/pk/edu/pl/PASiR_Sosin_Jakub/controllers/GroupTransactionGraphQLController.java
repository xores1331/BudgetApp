package pk.edu.pl.PASiR_Sosin_Jakub.controllers;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.GroupTransactionDTO;
import pk.edu.pl.PASiR_Sosin_Jakub.model.User;
import pk.edu.pl.PASiR_Sosin_Jakub.service.GroupTransactionService;
import pk.edu.pl.PASiR_Sosin_Jakub.service.TransactionService;

@Controller
public class GroupTransactionGraphQLController {
    private final GroupTransactionService groupTransactionService;
    private final TransactionService transactionService;

    public GroupTransactionGraphQLController(GroupTransactionService groupTransactionService, TransactionService transactionService) {
        this.groupTransactionService = groupTransactionService;
        this.transactionService = transactionService;
    }

    @MutationMapping
    public Boolean addGroupTransaction(@Argument GroupTransactionDTO groupTransactionDTO) {
        User user = transactionService.getCurrentUser();
        groupTransactionService.addGroupTransaction(groupTransactionDTO, user);
        return true;
    }
}
