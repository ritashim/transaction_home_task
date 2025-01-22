package moe.rtc.transaction.controller.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import moe.rtc.transaction.controller.TransactionControllerErrorCodes;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class TransactionBatchCreateModel {
    private List<TransactionBatchCreateData> transactions;

    @Data
    public static class TransactionBatchCreateData {
        @NotNull(message = TransactionControllerErrorCodes.ID_IS_NULL)
        private Long id;

        @NotEmpty(message = TransactionControllerErrorCodes.TRANSACTION_TYPE_IS_EMPTY)
        private String transactionType;

        @NotEmpty(message = TransactionControllerErrorCodes.ACCOUNT_IS_EMPTY)
        private String account;

        @NotNull(message = TransactionControllerErrorCodes.AMOUNT_IS_NULL)
        private BigDecimal amount;

    }
}
