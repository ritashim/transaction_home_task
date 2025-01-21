package moe.rtc.transaction.controller;

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

    @PostMapping("/{id}")
    public ResponseEntity<BaseApiDataResponse<TransactionEntity>> create(@Valid @RequestBody TransactionCreateModel model, @PathVariable(value = "id") Long id) {
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                service.create(
                        TransactionMapper.INSTANCE.createModelToEntity(model, id)
                )),
                HttpStatus.CREATED);
    }

    @PostMapping("/batchCreate")
    public ResponseEntity<BaseApiDataResponse<List<TransactionEntity>>> batchCreate(@Valid @RequestBody TransactionBatchCreateModel model) {
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                service.create(
                        TransactionMapper.INSTANCE.batchCreateModelToEntity(model.getTransactions())
                )),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiDataResponse<TransactionEntity>> queryOne(@PathVariable(value = "id") Long id) {
        TransactionEntity data = service.query(id);
        return new ResponseEntity<>(new BaseApiDataResponse<>(
               data),
               HttpStatus.OK);
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiDataResponse<TransactionEntity>> update(@PathVariable(value = "id") Long id, @Valid @RequestBody TransactionUpdateModel model) {
        TransactionEntity data = service.update(TransactionMapper.INSTANCE.updateModelToEntity(model, id));
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                data),
                HttpStatus.OK);
    }

    @PostMapping("/batchUpdate")
    public ResponseEntity<BaseApiDataResponse<List<TransactionEntity>>> batchUpdate(@Valid @RequestBody TransactionBatchUpdateModel model) {
        List<TransactionEntity> data = service.update(TransactionMapper.INSTANCE.batchUpdateDataToEntity(model.getTransactions()));
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                data),
                HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<BaseApiDataResponse<TransactionEntity>> delete(@PathVariable(value = "id") Long id) {
        TransactionEntity data = service.delete(id);
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                data),
                HttpStatus.OK);
    }

    @PostMapping("/batchDelete")
    public ResponseEntity<BaseApiDataResponse<List<TransactionEntity>>> batchDelete(@Valid @RequestBody TransactionBatchDeleteModel model) {
        List<TransactionEntity> data = service.delete(model.getIds());
        return new ResponseEntity<>(new BaseApiDataResponse<>(
                data),
                HttpStatus.OK);
    }

}
