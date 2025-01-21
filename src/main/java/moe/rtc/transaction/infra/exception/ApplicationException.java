package moe.rtc.transaction.infra.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ApplicationException extends RuntimeException{
    private String errorCode;
    private String errorMsg;
    private String[] errorMsgFormatString;
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
}
