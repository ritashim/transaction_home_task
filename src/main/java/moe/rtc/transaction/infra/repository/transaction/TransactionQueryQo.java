package moe.rtc.transaction.infra.repository.transaction;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class TransactionQueryQo {
    private List<Long> ids = new ArrayList<>();

    private String account;

    private LocalDateTime createdAtStart;

    private LocalDateTime createdAtEnd;

    private LocalDateTime updatedAtStart;

    private LocalDateTime updatedAtEnd;

    private long pageSize = 50;

    private long curPage = 1;
}
