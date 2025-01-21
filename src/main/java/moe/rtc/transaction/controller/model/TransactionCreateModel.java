package moe.rtc.transaction.controller.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import moe.rtc.transaction.controller.TransactionControllerErrorCodes;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class TransactionCreateModel {
    @NotEmpty(message = TransactionControllerErrorCodes.TRANSACTION_TYPE_IS_EMPTY)
    private String transactionType;

    @NotEmpty(message = TransactionControllerErrorCodes.ACCOUNT_IS_EMPTY)
    private String account;

    @NotNull(message = TransactionControllerErrorCodes.AMOUNT_IS_NULL)
    private BigDecimal amount;
}
