package moe.rtc.transaction.controllers;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import moe.rtc.transaction.controller.model.TransactionBatchCreateModel;
import moe.rtc.transaction.controller.model.TransactionCreateModel;
import moe.rtc.transaction.domain.transaction.TransactionEntity;
import moe.rtc.transaction.infra.utils.Snowflake;
import moe.rtc.transaction.infra.web.BaseApiDataResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.IntStream;

import static moe.rtc.transaction.domain.transaction.enumation.TransactionType.DEPOSIT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:test/TransactionControllerSingleTest.sql")
@Log
public class TransactionBatchBenchTest {
    @Autowired
    ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createTransaction() throws Exception {
        long begin = System.nanoTime();
        for(int i = 0; i < 9; i++) {
            List<TransactionBatchCreateModel.TransactionBatchCreateData> datas = IntStream.range(0, 10000)
                    .boxed()
                    .map(t -> {
                                TransactionBatchCreateModel.TransactionBatchCreateData data = new TransactionBatchCreateModel.TransactionBatchCreateData()
                                        .setId(Snowflake.nextId())
                                        .setAccount(randomName())
                                        .setTransactionType("DEP")
                                        .setAmount(randomNumber());
                                return data;
                            }
                    ).toList();
            TransactionBatchCreateModel model = new TransactionBatchCreateModel().setTransactions(datas);

            BaseApiDataResponse<List<TransactionEntity>> actual = om.readValue(mockMvc.perform(post("/transaction/batchCreate")
                            .contentType("application/json")
                            .content(om.writeValueAsString(model)))
                    .andDo(print())
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
            });
        }
        double cost = (System.nanoTime() - begin) / 1e9;
        log.info("createTransaction - time = " + cost);
    }

    public static BigDecimal randomNumber() {
        return BigDecimal.valueOf(Math.random() * 100).setScale(2, RoundingMode.DOWN);
    }

    public static String randomName() {
        StringBuilder nameSb = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            nameSb.append('a' + (Math.random() * 26));
        }
        return nameSb.toString();
    }
}
