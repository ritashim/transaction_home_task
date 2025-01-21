package moe.rtc.transaction.domain.transaction.enumation;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<TransactionType, String> {
    @Override
    public String convertToDatabaseColumn(TransactionType transactionType) {
        return transactionType.transactionCode;
    }

    @Override
    public TransactionType convertToEntityAttribute(String code) {
        return TransactionType.convert(code);
    }
}
