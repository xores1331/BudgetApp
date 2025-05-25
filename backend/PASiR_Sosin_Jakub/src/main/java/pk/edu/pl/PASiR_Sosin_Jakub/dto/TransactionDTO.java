package pk.edu.pl.PASiR_Sosin_Jakub.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import pk.edu.pl.PASiR_Sosin_Jakub.model.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    @Setter(value = AccessLevel.NONE)
    private Long id;

    @NotNull(message = "Kwota nie może być pusta")
    @Min(value = 1, message = "Kwota musi być większa niż 0")
    private Double amount;

    @NotNull(message = "Typ transakcji nie może być pusty")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Size(max = 50, message = "Tagi nie mogą przekraczać 100 znaków")
    private String tags;

    @Size(max = 255, message = "Notatka nie może przekraczać 255 znaków")
    private String notes;

    private LocalDateTime timestamp;

    public TransactionDTO(Double amount, TransactionType type, String tags, String notes, LocalDateTime timestamp) {
        this.amount = amount;
        this.type = type;
        this.tags = tags;
        this.notes = notes;
        this.timestamp = timestamp;
    }
}
