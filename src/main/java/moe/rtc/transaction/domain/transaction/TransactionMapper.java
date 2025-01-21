package moe.rtc.transaction.domain.transaction;

import moe.rtc.transaction.controller.model.*;
import moe.rtc.transaction.domain.transaction.dto.TransactionQueryDto;
import moe.rtc.transaction.domain.transaction.enumation.TransactionType;
import moe.rtc.transaction.domain.transaction.exception.TransactionException;
import moe.rtc.transaction.infra.repository.transaction.TransactionQueryQo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "model.transactionType", target = "transactionType", qualifiedByName = "transactionTypeStringConvert")
    @Mapping(target = "createdBy", expression = "java(\"SYSTEM\")")
    @Mapping(target = "updatedBy", expression = "java(\"SYSTEM\")")
    TransactionEntity createModelToEntity(TransactionCreateModel model, long id);

    @Mapping(source = "transactionType", target = "transactionType", qualifiedByName = "transactionTypeStringConvert")
    @Mapping(target = "createdBy", expression = "java(\"SYSTEM\")")
    @Mapping(target = "updatedBy", expression = "java(\"SYSTEM\")")
    TransactionEntity batchCreateModelToEntity(TransactionBatchCreateModel.TransactionBatchCreateData model);
    List<TransactionEntity> batchCreateModelToEntity(List<TransactionBatchCreateModel.TransactionBatchCreateData> model);

    @Mapping(source = "model.transactionType", target = "transactionType", qualifiedByName = "transactionTypeStringConvert")
    @Mapping(target = "updatedBy", expression = "java(\"SYSTEM\")")
    TransactionEntity updateModelToEntity(TransactionUpdateModel model, long id);

    @Mapping(source = "transactionType", target = "transactionType", qualifiedByName = "transactionTypeStringConvert")
    @Mapping(target = "updatedBy", expression = "java(\"SYSTEM\")")
    TransactionEntity batchUpdateDataToEntity(TransactionBatchUpdateModel.TransactionBatchUpdateData model);
    List<TransactionEntity> batchUpdateDataToEntity(List<TransactionBatchUpdateModel.TransactionBatchUpdateData> model);

    TransactionQueryDto queryModelToDto(TransactionPagedQueryModel model, long pageSize, long curPage);

    @Named("transactionTypeStringConvert")
    public static TransactionType transactionTypeStringConvert (String code) throws TransactionException {
        TransactionType t = TransactionType.convert(code);
        if(TransactionType.CORRUPTED == t) {
            TransactionException.Category.INVALID_TRANSACTION_TYPE.occur(code);
        }
        return t;
    }

    TransactionQueryQo queryDtoToQo(TransactionQueryDto dto);
}
