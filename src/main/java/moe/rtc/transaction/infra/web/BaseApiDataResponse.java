package moe.rtc.transaction.infra.web;

import lombok.*;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseApiDataResponse<T> {
    private String bizCode = "I_SUCCESS";
    private String bizMessage = "Success.";
    private T data;

    public BaseApiDataResponse(T data) {
        this.data = data;
    }

}
