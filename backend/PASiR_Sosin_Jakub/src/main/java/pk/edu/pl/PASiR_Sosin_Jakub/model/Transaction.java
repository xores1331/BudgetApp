    package pk.edu.pl.PASiR_Sosin_Jakub.model;

    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Setter(value = AccessLevel.NONE)
        private Long id;

        private Double amount;

        @Enumerated(EnumType.STRING)
        private TransactionType type;

        private String tags;

        private String notes;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        private LocalDateTime timestamp;

        public Transaction(Double amount, TransactionType type, String tags, String notes, LocalDateTime timestamp) {
            this.amount = amount;
            this.type = type;
            this.tags = tags;
            this.notes = notes;
            this.timestamp = timestamp;
        }

    public Transaction(Double amount, TransactionType type, String tags, String notes, User user, LocalDateTime timestamp) {
        this.amount = amount;
        this.type = type;
        this.tags = tags;
        this.notes = notes;
        this.user = user;
        this.timestamp = timestamp;
    }

    public Transaction(Double amount, TransactionType type, String tags, String notes, User user) {
        this.amount = amount;
        this.type = type;
        this.tags = tags;
        this.notes = notes;
        this.user = user;
    }
}
