package moe.rtc.transaction.domain.transaction;

import moe.rtc.transaction.domain.BaseEntity;
import moe.rtc.transaction.domain.transaction.dto.TransactionQueryDto;
import moe.rtc.transaction.domain.transaction.exception.TransactionException;
import moe.rtc.transaction.infra.pagination.Page;
import moe.rtc.transaction.infra.repository.transaction.TransactionDao;
import moe.rtc.transaction.infra.repository.transaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@CacheConfig(cacheNames = {"transactionCache"})
public class TransactionService {

    private Cache cache;
    private TransactionRepository repo;
    private TransactionDao dao;

    @Autowired
    TransactionService(CacheManager cacheManager, TransactionRepository repository, TransactionDao transactionDao) {
        this.cache = cacheManager.getCache("transactionCache");
        this.repo = repository;
        this.dao = transactionDao;
    }

    public TransactionEntity create(TransactionEntity entity){
        List<TransactionEntity> arg = new ArrayList<>();
        arg.add(entity);
        List<TransactionEntity> result = create(arg);
        return result.getFirst();
    }

    public List<TransactionEntity> create(Collection<TransactionEntity> entitys){
        // Validation + Get from cache
        List<TransactionEntity> cacheHits = new ArrayList<>();
        List<Long> cacheMissIds = new ArrayList<>();
        for(TransactionEntity entity : entitys){
            entity.validate();
            if(Objects.nonNull(cache.get(entity.getId(), TransactionEntity.class))) {
                cacheHits.add(entity);
            } else {
                cacheMissIds.add(entity.getId());
            }
        }
        softDeletedAccessedValidation(cacheHits);
        if(!cacheHits.isEmpty()) {
            TransactionException.Category.DUPLICATE_TRANSACTION.occur(
                    String.join(",", cacheHits.stream().map(t -> String.valueOf(t.getId())).toList())
            );
        }

        // Get from repo
        List<TransactionEntity> duplicatedTransactionFromRepo = repo.findAllById(cacheMissIds);
        softDeletedAccessedValidation(duplicatedTransactionFromRepo);
        if(!duplicatedTransactionFromRepo.isEmpty()) {
            TransactionException.Category.DUPLICATE_TRANSACTION.occur(
                    String.join(",", duplicatedTransactionFromRepo.stream().map(t -> String.valueOf(t.getId())).toList())
            );
        }

        // No duplicates, insert
        List<TransactionEntity> save = repo.saveAll(entitys);
        return save;
    }

    public TransactionEntity update(TransactionEntity entity){
        List<TransactionEntity> arg = new ArrayList<>();
        arg.add(entity);
        List<TransactionEntity> result = update(arg);
        return result.getFirst();
    }

    public List<TransactionEntity> update(Collection<TransactionEntity> entitys){
        // Duplication Check
        Set<Long> allIds = new HashSet<>();

        // Validation + Get from cache
        Set<Long> cacheMissIds = new HashSet<>();
        List<TransactionEntity> existedTransaction = new ArrayList<>();
        for(TransactionEntity entity : entitys){
            entity.validate();

            if(!allIds.add(entity.getId())){
                TransactionException.Category.DUPLICATE_TRANSACTION.occur(String.valueOf(entity.getId()));
            }

            TransactionEntity cached = cache.get(entity.getId(), TransactionEntity.class);
            if(Objects.isNull(cached)) {
                cacheMissIds.add(entity.getId());
            } else {
                existedTransaction.add(cached);
            }
        }

        // Get from repo check if actually not existed
        existedTransaction.addAll(repo.findAllById(cacheMissIds));

        if(existedTransaction.size() != allIds.size()) {
            for(TransactionEntity t : existedTransaction)  {
                allIds.remove(t.getId());
            }
            TransactionException.Category.NO_TRANSACTION_FOUND.occur(HttpStatus.NOT_FOUND,
                    String.join(",", cacheMissIds.stream().map(String::valueOf).toList())
            );
        }

        // Soft deleted transaction could not be accessed.
        softDeletedAccessedValidation(existedTransaction);

        // All exist, update
        List<TransactionEntity> save = repo.saveAll(entitys);

        // Evict cache
        for(TransactionEntity entity : entitys){
            cache.evict(entity.getId());
        }

        return save;
    }

    public TransactionEntity query(Long id) {
        Page<List<TransactionEntity>> paged = query(new TransactionQueryDto().addId(id));
        if(paged.getPagedData().isEmpty()) {
            TransactionException.Category.NO_TRANSACTION_FOUND.occur(HttpStatus.NOT_FOUND, String.valueOf(id));
        }
        TransactionEntity data = paged.getPagedData().getFirst();
        return data;
    }

    public Page<List<TransactionEntity>> query(TransactionQueryDto dto){
        // Query all ids
        Page<List<Long>> allIds = dao.pagedQueryId(TransactionMapper.INSTANCE.queryDtoToQo(dto));

        // Check cached data
        List<TransactionEntity> data = new ArrayList<>();
        List<Long> cacheMissId = new ArrayList<>();
        for(Long l : allIds.getPagedData()) {
            TransactionEntity t = cache.get(l, TransactionEntity.class);
            if(Objects.nonNull(t)) {
                data.add(t);
            } else {
                cacheMissId.add(l);
            }
        }
        if(!cacheMissId.isEmpty()) {
            List<TransactionEntity> repoData = repo.findAllById(cacheMissId);
            repoData.forEach(t -> cache.put(t.getId(), t)); // insert to cache
            data.addAll(repoData);
        }
        data.sort(Comparator.comparingLong(TransactionEntity::getId));
        return new Page<>(allIds, data);
    }

    public TransactionEntity delete(Long id){
        List<Long> arg = new ArrayList<>();
        arg.add(id);
        List<TransactionEntity> result = delete(arg);
        return result.getFirst();
    }

    public List<TransactionEntity> delete(Collection<Long> ids){
        // Duplication Check
        Set<Long> allIds = new HashSet<>();

        // Validation + Get from cache
        Set<Long> cacheMissIds = new HashSet<>();
        List<TransactionEntity> existedTransaction = new ArrayList<>();
        for(long id : ids){
            if(!allIds.add(id)){
                TransactionException.Category.DUPLICATE_TRANSACTION.occur(String.valueOf(id));
            }

            TransactionEntity cached = cache.get(id, TransactionEntity.class);
            if(Objects.isNull(cached)) {
                cacheMissIds.add(id);
            } else {
                existedTransaction.add(cached);
            }
        }

        // Get from repo check if actually not existed
        existedTransaction.addAll(repo.findAllById(cacheMissIds));

        if(existedTransaction.size() != allIds.size()) {
            for(TransactionEntity t : existedTransaction)  {
                allIds.remove(t.getId());
            }
            TransactionException.Category.NO_TRANSACTION_FOUND.occur(HttpStatus.NOT_FOUND,
                    String.join(",", cacheMissIds.stream().map(String::valueOf).toList())
            );
        }

        // Soft deleted transaction could not be accessed.
        softDeletedAccessedValidation(existedTransaction);

        // Delete
        existedTransaction.forEach(BaseEntity::delete);

        // Update
        List<TransactionEntity> save = repo.saveAll(existedTransaction);

        // Evict cache
        for(TransactionEntity entity : existedTransaction){
            cache.evict(entity.getId());
        }

        return save;
    }

    private static void softDeletedAccessedValidation(List<TransactionEntity> existedTransaction) {
        if(existedTransaction.isEmpty()) return;
        List<String> softDeleted = existedTransaction.stream()
                .filter(t -> 1L == t.getDeleted())
                .map(t -> String.valueOf(t.getId()))
                .toList();
        if(!softDeleted.isEmpty()) {
            TransactionException.Category.SOFT_DELETED_REVIVEL.occur(String.join(",", softDeleted));
        }
    }
}
