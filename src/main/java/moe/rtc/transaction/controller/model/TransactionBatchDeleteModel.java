package moe.rtc.transaction.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TransactionBatchDeleteModel {
    @NotNull
    private List<@NotNull Long> ids;
}
