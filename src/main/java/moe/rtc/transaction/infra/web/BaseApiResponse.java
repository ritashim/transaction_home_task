package moe.rtc.transaction.infra.web;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseApiResponse {
    private String bizCode;
    private String bizMessage;
}
