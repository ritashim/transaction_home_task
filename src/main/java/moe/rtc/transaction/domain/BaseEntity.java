package moe.rtc.transaction.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    @Getter
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    @Getter
    @Setter
    private String createdBy = "SYSTEM";

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    @Getter
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    @Getter
    @Setter
    private String updatedBy = "SYSTEM";

    @Column(name = "deleted")
    @Getter
    @Setter
    @JsonIgnore
    private long deleted = 0;

    public void delete() {
        this.deleted = 1L;
    }
}
