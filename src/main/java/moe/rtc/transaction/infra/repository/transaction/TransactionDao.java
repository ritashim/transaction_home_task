package moe.rtc.transaction.infra.repository.transaction;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import moe.rtc.transaction.domain.transaction.QTransactionEntity;
import moe.rtc.transaction.domain.transaction.TransactionEntity;
import moe.rtc.transaction.infra.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TransactionDao {

    private JPAQueryFactory queryFactory;

    @Autowired
    TransactionDao(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Page<List<Long>> pagedQueryId(TransactionQueryQo qo){
        QTransactionEntity entity = QTransactionEntity.transactionEntity;

        JPAQuery<Long> query = queryFactory.select(entity.id).from(entity);
        JPAQuery<Long> queryCount = queryFactory.select(entity.count()).from(entity);
        BooleanExpression wheres = entity.deleted.eq(0L);
        if(!qo.getIds().isEmpty()) {
            wheres = wheres.and(entity.id.in(qo.getIds()));
        }
        if(StringUtils.isNotBlank(qo.getAccount())) {
            wheres = wheres.and(entity.account.eq(qo.getAccount()));
        }
        if(Objects.nonNull(qo.getCreatedAtStart())) {
            wheres = wheres.and(entity.createdAt.goe(qo.getCreatedAtStart()));
        }
        if(Objects.nonNull(qo.getCreatedAtEnd())) {
            wheres = wheres.and(entity.createdAt.loe(qo.getCreatedAtEnd()));
        }
        if(Objects.nonNull(qo.getUpdatedAtStart())) {
            wheres = wheres.and(entity.createdAt.goe(qo.getUpdatedAtStart()));
        }
        if(Objects.nonNull(qo.getUpdatedAtEnd())) {
            wheres = wheres.and(entity.createdAt.loe(qo.getUpdatedAtEnd()));
        }

        long size = queryCount.where(wheres).fetchOne();

        Page<List<Long>> res = new Page<>();
        res.setCurrentPage(qo.getCurPage())
                .setPageSize(qo.getPageSize())
                .setTotalPage(Math.max(1, 1 + (size - 1) / qo.getPageSize()))
                .setTotalData(size)
                .setPagedData(
                query.where(wheres)
                .orderBy(entity.id.asc())
                .offset((long) qo.getPageSize() * (qo.getCurPage() - 1))
                .limit(qo.getPageSize())
                .fetch());
        return res;
    }
}
