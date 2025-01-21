package moe.rtc.transaction.domain.transaction.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class TransactionQueryDto {
    private Set<Long> ids = new HashSet<>();

    private String account;

    private LocalDateTime createdAtStart;

    private LocalDateTime createdAtEnd;

    private LocalDateTime updatedAtStart;

    private LocalDateTime updatedAtEnd;

    private long pageSize = 50;

    private long curPage = 1;

    public TransactionQueryDto addId(Long id){
        ids.add(id);
        return this;
    }
}
