package moe.rtc.transaction.domain.transaction.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import moe.rtc.transaction.infra.exception.ApplicationException;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class TransactionException extends ApplicationException {
    private static String prefix = "D_TRANSACTION_";
    public enum Category {
        DUPLICATE_TRANSACTION("DUP"),
        NO_TRANSACTION_FOUND("NOT"),
        INVALID_TRANSACTION_TYPE("ITT"),
        SOFT_DELETED_REVIVEL("SDR");

        private final String code;
        Category(String code) {
            this.code = code;
        }

        public void occur(String... formatString) throws TransactionException {
            occur(HttpStatus.INTERNAL_SERVER_ERROR, formatString);
        }

        public void occur(HttpStatus httpStatus, String... formatString) throws TransactionException {
            TransactionException exp = new TransactionException();
            exp.setErrorCode(prefix + code)
               .setHttpStatus(httpStatus)
               .setErrorMsgFormatString(formatString);
            throw exp;
        }
    }
}