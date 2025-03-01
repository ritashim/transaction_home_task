package moe.rtc.transaction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.java.Log;
import moe.rtc.transaction.controller.model.*;
import moe.rtc.transaction.domain.transaction.TransactionMapper;
import moe.rtc.transaction.domain.transaction.TransactionService;
import moe.rtc.transaction.domain.transaction.enumation.TransactionType;
import moe.rtc.transaction.domain.transaction.exception.TransactionException;
import moe.rtc.transaction.infra.pagination.Page;
import moe.rtc.transaction.infra.repository.transaction.TransactionDao;
import moe.rtc.transaction.infra.repository.transaction.TransactionRepository;
import moe.rtc.transaction.domain.transaction.TransactionEntity;
import moe.rtc.transaction.infra.utils.Snowflake;
import moe.rtc.transaction.infra.web.BaseApiDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("transaction")
@Log
@Tag(name = "Transaction", description = "The transaction APIs.")
public class TransactionController {
    private final TransactionService service;

    private final TransactionRepository repository;


    @Autowired
    public TransactionController(TransactionRepository repository, TransactionService service, TransactionDao dao) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping("/test")
    public ResponseEntity<List<TransactionEntity>> test() throws TransactionException {
        List<TransactionEntity> randomDatas = IntStream.range(0, 100000)
                .boxed()
                .map(t -> new TransactionEntity().setId(Snowflake.nextId()).setTransactionType(TransactionType.DEPOSIT).setAmount(new BigDecimal("346.99")).setAccount("anzu"))
                .toList();
        service.create(randomDatas);
        return new ResponseEntity<>(randomDatas, HttpStatus.OK);
    }

    @Operation(summary = "Create new transaction", description = "Create new transaction of specific transaction ID to the service")
    @PostMapping("/{id}")
    public ResponseEntity<BaseApiDataResponse<TransactionEntity>> create(@Valid @RequestBody TransactionCreateModel model, @PathVariable(value = "id") Long id) {
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                service.create(
                        TransactionMapper.INSTANCE.createModelToEntity(model, id)
                )),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Batch create new transaction", description = "Batch create new transaction to the service.")
    @PostMapping("/batchCreate")
    public ResponseEntity<BaseApiDataResponse<List<TransactionEntity>>> batchCreate(@Valid @RequestBody TransactionBatchCreateModel model) {
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                service.create(
                        TransactionMapper.INSTANCE.batchCreateModelToEntity(model.getTransactions())
                )),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Query transaction", description = "Query one specific transaction by transaction ID.")
    @GetMapping("/{id}")
    public ResponseEntity<BaseApiDataResponse<TransactionEntity>> queryOne(@PathVariable(value = "id") Long id) {
        TransactionEntity data = service.query(id);
        return new ResponseEntity<>(new BaseApiDataResponse<>(
               data),
               HttpStatus.OK);
    }

    @Operation(summary = "Paged query transaction", description = "Query transactions with condition and pagination.")
    @PostMapping("/pagedQuery")
    public ResponseEntity<BaseApiDataResponse<Page<List<TransactionEntity>>>> pagedQuery(
            @Valid @RequestBody TransactionPagedQueryModel model,
            @RequestParam(value = "size", defaultValue = "50") @Max(500) @Min(1) Long pageSize,
            @RequestParam(value = "page", defaultValue = "1") Long curPage) {
        Page<List<TransactionEntity>> data = service.query(TransactionMapper.INSTANCE.queryModelToDto(model, pageSize, curPage));
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                data),
                HttpStatus.OK);
    }

    @Operation(summary = "Update transaction", description = "Update one specific transaction by transaction ID.")
    @PutMapping("/{id}")
    public ResponseEntity<BaseApiDataResponse<TransactionEntity>> update(@PathVariable(value = "id") Long id, @Valid @RequestBody TransactionUpdateModel model) {
        TransactionEntity data = service.update(TransactionMapper.INSTANCE.updateModelToEntity(model, id));
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                data),
                HttpStatus.OK);
    }

    @Operation(summary = "Batch update transaction", description = "Update transactions with list of transaction datas.")
    @PostMapping("/batchUpdate")
    public ResponseEntity<BaseApiDataResponse<List<TransactionEntity>>> batchUpdate(@Valid @RequestBody TransactionBatchUpdateModel model) {
        List<TransactionEntity> data = service.update(TransactionMapper.INSTANCE.batchUpdateDataToEntity(model.getTransactions()));
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                data),
                HttpStatus.OK);
    }

    @Operation(summary = "Delete transaction", description = "Soft delete one specific transaction by transaction ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseApiDataResponse<TransactionEntity>> delete(@PathVariable(value = "id") Long id) {
        TransactionEntity data = service.delete(id);
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                data),
                HttpStatus.OK);
    }

    @Operation(summary = "Batch delete transaction", description = "Soft delete transactions by transaction IDs.")
    @PostMapping("/batchDelete")
    public ResponseEntity<BaseApiDataResponse<List<TransactionEntity>>> batchDelete(@Valid @RequestBody TransactionBatchDeleteModel model) {
        List<TransactionEntity> data = service.delete(model.getIds());
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                data),
                HttpStatus.OK);
    }

}
