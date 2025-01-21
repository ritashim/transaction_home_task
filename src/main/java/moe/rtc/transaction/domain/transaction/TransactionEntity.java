package moe.rtc.transaction.domain.transaction;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import moe.rtc.transaction.domain.BaseEntity;
import moe.rtc.transaction.domain.transaction.enumation.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder=true)
@ToString
@Entity(name = "transaction")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(indexes = {
        @Index(name = "transaction_idx_created_at", columnList = "created_at"),
        @Index(name = "transaction_idx_updated_at", columnList = "updated_at"),
        @Index(name = "transaction_idx_transaction_type", columnList = "transaction_type")
})
public class TransactionEntity extends BaseEntity implements Serializable {
    @Id
    @Column(name = "transaction_id", nullable = false)
    private Long id;

    @Column(name = "transaction_type", nullable = false, length = 3)
    protected TransactionType transactionType;

    @Column(name = "account", nullable = false)
    private String account;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    public TransactionEntity validate() {
        return this;
    }
}
