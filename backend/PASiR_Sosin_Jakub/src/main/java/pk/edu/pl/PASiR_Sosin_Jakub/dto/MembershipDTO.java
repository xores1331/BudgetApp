package pk.edu.pl.PASiR_Sosin_Jakub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipDTO {
    @NotNull(message = "Email użytkownika nie może być puste")
    private String userEmail;

    @NotNull(message = "ID grupy nie może być puste")
    private Long groupId;
}
