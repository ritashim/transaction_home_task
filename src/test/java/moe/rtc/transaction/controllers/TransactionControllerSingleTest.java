package moe.rtc.transaction.controllers;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.rtc.transaction.controller.model.TransactionCreateModel;
import moe.rtc.transaction.controller.model.TransactionUpdateModel;
import moe.rtc.transaction.domain.transaction.TransactionEntity;
import moe.rtc.transaction.infra.utils.Snowflake;
import moe.rtc.transaction.infra.web.BaseApiDataResponse;
import moe.rtc.transaction.infra.web.BaseApiResponse;
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

import static moe.rtc.transaction.domain.transaction.enumation.TransactionType.DEPOSIT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:test/TransactionControllerSingleTest.sql")
public class TransactionControllerSingleTest {
    @Autowired
    ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    private static final long id = 1145141919810702656L;

    @Test
    public void createTransaction() throws Exception {
        TransactionCreateModel model = new TransactionCreateModel()
                .setTransactionType("DEP")
                .setAmount(new BigDecimal("114.51"))
                .setAccount("anzu");
        BaseApiDataResponse<TransactionEntity> actual = om.readValue(mockMvc.perform(post("/transaction/"+id)
                        .contentType("application/json")
                        .content(om.writeValueAsString(model)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.equals("anzu", actual.getData().getAccount());
        Assert.equals(new BigDecimal("114.51"), actual.getData().getAmount());
        Assert.equals(DEPOSIT, actual.getData().getTransactionType());
        Assert.equals(id, actual.getData().getId());
    }

    @Test
    public void queryTransaction() throws Exception {
        createTransaction();

        BaseApiDataResponse<TransactionEntity> actual = om.readValue(mockMvc.perform(get("/transaction/"+id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.equals("anzu", actual.getData().getAccount());
        Assert.equals(new BigDecimal("114.51"), actual.getData().getAmount());
        Assert.equals(DEPOSIT, actual.getData().getTransactionType());
        Assert.equals(id, actual.getData().getId());
    }

    @Test
    public void updateTransaction() throws Exception {
        createTransaction();

        TransactionUpdateModel model = new TransactionUpdateModel()
                .setTransactionType("DEP")
                .setAmount(new BigDecimal("1919.81"))
                .setAccount("anzu");
        BaseApiDataResponse<TransactionEntity> actual = om.readValue(mockMvc.perform(put("/transaction/"+id)
                        .contentType("application/json")
                        .content(om.writeValueAsString(model)))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    @Test
    public void deleteTransaction() throws Exception {
        createTransaction();

        BaseApiDataResponse<TransactionEntity> actual = om.readValue(mockMvc.perform(delete("/transaction/"+id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.equals("anzu", actual.getData().getAccount());
        Assert.equals(new BigDecimal("114.51"), actual.getData().getAmount());
        Assert.equals(DEPOSIT, actual.getData().getTransactionType());
        Assert.equals(id, actual.getData().getId());

        BaseApiResponse actual2 = om.readValue(mockMvc.perform(get("/transaction/"+id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    @Test
    public void deleteAndReinsertTransaction() throws Exception {
        deleteTransaction();

        TransactionCreateModel model = new TransactionCreateModel()
                .setTransactionType("DEP")
                .setAmount(new BigDecimal("114.51"))
                .setAccount("anzu");
        BaseApiResponse actual = om.readValue(mockMvc.perform(post("/transaction/"+id)
                        .contentType("application/json")
                        .content(om.writeValueAsString(model)))
                .andDo(print())
                .andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.equals("D_TRANSACTION_SDR", actual.getBizCode());
    }

    @Test
    public void deleteAndReupdateTransaction() throws Exception {
        deleteTransaction();

        TransactionCreateModel model = new TransactionCreateModel()
                .setTransactionType("DEP")
                .setAmount(new BigDecimal("114.51"))
                .setAccount("anzu");
        BaseApiResponse actual = om.readValue(mockMvc.perform(put("/transaction/"+id)
                        .contentType("application/json")
                        .content(om.writeValueAsString(model)))
                .andDo(print())
                .andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.equals("D_TRANSACTION_SDR", actual.getBizCode());
    }

    @Test
    public void deleteAndRedeleteTransaction() throws Exception {
        deleteTransaction();

        BaseApiDataResponse<TransactionEntity> actual = om.readValue(mockMvc.perform(delete("/transaction/"+id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.equals("D_TRANSACTION_SDR", actual.getBizCode());
    }
}
