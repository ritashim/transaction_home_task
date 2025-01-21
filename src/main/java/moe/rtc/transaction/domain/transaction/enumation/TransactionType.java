package moe.rtc.transaction.domain.transaction.enumation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import io.micrometer.common.util.StringUtils;


public enum TransactionType {
    DEPOSIT("DEP"),
    ATM_TRANSFER("ATT"),
    CORRUPTED("COP");

    TransactionType(String code) {
        this.transactionCode = code;
    }
    final String transactionCode;

    public static TransactionType convert(String code) {
        if(StringUtils.isBlank(code)) {
            return CORRUPTED;
        }
        for(TransactionType t : TransactionType.values()) {
            if(t.transactionCode.equals(code)) {
                return t;
            }
        }
        return CORRUPTED;
    }

    @JsonValue
    public String toString(){
        return this.transactionCode;
    }
}

