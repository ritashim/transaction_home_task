package moe.rtc.transaction.controller.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class TransactionPagedQueryModel {
    private List<Long> ids;

    private String account;

    private LocalDateTime createdAtStart;

    private LocalDateTime createdAtEnd;

    private LocalDateTime updatedAtStart;

    private LocalDateTime updatedAtEnd;
}
