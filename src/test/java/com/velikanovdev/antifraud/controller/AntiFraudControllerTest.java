package com.velikanovdev.antifraud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.velikanovdev.antifraud.dto.TransactionDTO;
import com.velikanovdev.antifraud.entity.StolenCard;
import com.velikanovdev.antifraud.entity.SuspiciousIP;
import com.velikanovdev.antifraud.entity.Transaction;
import com.velikanovdev.antifraud.entity.TransactionInfo;
import com.velikanovdev.antifraud.enums.Region;
import com.velikanovdev.antifraud.enums.TransactionStatus;
import com.velikanovdev.antifraud.repository.TransactionRepository;
import com.velikanovdev.antifraud.service.StolenCardService;
import com.velikanovdev.antifraud.service.SuspiciousIPService;
import com.velikanovdev.antifraud.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AntiFraudControllerTest {
    private static final String BASE_API_URL = "/api/antifraud";

    @MockBean
    private SuspiciousIPService suspiciousIPService;

    @Mock
    private TransactionService transactionService;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private StolenCardService stolenCardService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testPrepareTransaction_ValidTransaction_ReturnsOkResponse() throws Exception {
        // Test for "prepareTransaction" endpoint with a valid transaction
        TransactionDTO validTransaction = new TransactionDTO();
        TransactionInfo transactionInfo = new TransactionInfo(TransactionStatus.ALLOWED, "none");

        validTransaction.setAmount(150L);
        validTransaction.setIp("192.168.1.1");
        validTransaction.setNumber("4000008449433403");
        validTransaction.setRegion(Region.ECA);
        validTransaction.setDate(LocalDateTime.parse("2022-01-22T16:04:00"));

        when(transactionService.checkTransactionValidity(validTransaction))
                .thenReturn(ResponseEntity.ok(transactionInfo));

        mockMvc.perform(post(BASE_API_URL + "/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validTransaction)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(transactionInfo)));
    }

    @Test
    public void testAddSuspiciousIp_ValidSuspiciousIP_ReturnsOkResponse() throws Exception {
        // Test for "addSuspiciousIp" endpoint with a valid suspicious IP
        SuspiciousIP validSuspiciousIP = new SuspiciousIP(1L, "192.168.0.1");

        when(suspiciousIPService.addSuspiciousIP(validSuspiciousIP))
                .thenReturn(ResponseEntity.ok(validSuspiciousIP));

        // Perform the request and check the response
        mockMvc.perform(post(BASE_API_URL + "/suspicious-ip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validSuspiciousIP)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteSuspiciousIP_ExistingIp_ReturnsOkResponse() throws Exception {
        // Test for "deleteSuspiciousIP" endpoint with an existing IP
        String existingIp = "192.168.0.1";
        Map<String, String> response = Map.of("status", "IP " + existingIp + " successfully removed!");

        when(suspiciousIPService.deleteSuspiciousIP(existingIp))
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(delete(BASE_API_URL + "/suspicious-ip/{ip}", existingIp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(response)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(response)));
    }

    @Test
    public void testGetAllSuspiciousIp_SuspiciousIpsExist_ReturnsOkResponse() throws Exception {
        // Test for "getAllSuspiciousIp" endpoint when there are suspicious IPs
        List<SuspiciousIP> suspiciousIp = List.of(new SuspiciousIP(1L, "192.168.0.1"));
        when(suspiciousIPService.getAllSuspiciousIp()).thenReturn(ResponseEntity.ok(suspiciousIp));

        mockMvc.perform(get(BASE_API_URL + "/suspicious-ip")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(suspiciousIp)));
    }

    @Test
    public void testAddStolenCard_ValidStolenCard_ReturnsOkResponse() throws Exception {
        // Test for "addStolenCard" endpoint with a valid stolen card
        StolenCard validStolenCard = new StolenCard(1L, "4000008449433403");

        when(stolenCardService.addStolenCard(validStolenCard))
                .thenReturn(ResponseEntity.ok(validStolenCard));

        mockMvc.perform(post(BASE_API_URL + "/stolencard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validStolenCard)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteStolenCard_ExistingCardNumber_ReturnsOkResponse() throws Exception {
        // Test for "deleteStolenCard" endpoint with an existing stolen card number
        String existingCardNumber = "1234567812345678";
        Map<String, String> response = Map.of("status", "Card " + existingCardNumber + " successfully removed!");

        when(stolenCardService.deleteStolenCard(existingCardNumber))
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(delete(BASE_API_URL + "/stolencard/{number}", existingCardNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(response)));
    }

    @Test
    public void testGetAllStolenCards_StolenCardsExist_ReturnsOkResponse() throws Exception {
        // Test for "getAllStolenCards" endpoint when there are stolen cards
        List<StolenCard> stolenCards = List.of(new StolenCard(1L, "4000008449433403"));
        when(stolenCardService.getAllStolenCards()).thenReturn(ResponseEntity.ok(stolenCards));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_API_URL + "/stolencard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(stolenCards)));
    }

    @Test
    public void testShowAllHistory_TransactionsExist_ReturnsOkResponse() throws Exception {
        // Test for "showAllHistory" endpoint when there are transactions
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(1L, 210L, "192.168.1.1", "4000008449433403",
                Region.ECA, LocalDateTime.parse("2022-01-22T16:04:00"), TransactionStatus.MANUAL_PROCESSING));

        transactions.add(new Transaction(2L, 100L, "192.168.1.1", "4000008449433403",
                Region.ECA, LocalDateTime.parse("2022-01-22T16:05:00"), TransactionStatus.ALLOWED));

        when(transactionService.showAllHistory()).thenReturn(ResponseEntity.ok(transactions));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_API_URL + "/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(transactions)))
                .andExpect(status().isOk());
    }

    @Test
    public void testShowCardHistory_ExistingCardNumber_ReturnsOkResponse() throws Exception {
        // Test for "showCardHistory" endpoint with an existing card number
        String existingCardNumber = "4000008449433403";
        List<Transaction> cardHistory = new ArrayList<>();

        cardHistory.add(new Transaction(1L, 210L, "192.168.1.1", "4000008449433403",
                Region.ECA, LocalDateTime.parse("2022-01-22T16:04:00"), TransactionStatus.MANUAL_PROCESSING));

         cardHistory.add(new Transaction(2L, 100L, "192.168.1.1", "4000008449433403",
                 Region.ECA, LocalDateTime.parse("2022-01-22T16:05:00"), TransactionStatus.ALLOWED));

        when(transactionRepository.findAllByNumber(existingCardNumber)).thenReturn(cardHistory);
        when(transactionService.showCardHistory(existingCardNumber)).thenReturn(ResponseEntity.ok(cardHistory));


        mockMvc.perform(MockMvcRequestBuilders.get(BASE_API_URL + "/history/{number}", existingCardNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(cardHistory)));
    }

    // Helper method to convert objects to JSON string
    private static String asJsonString(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(object);
    }

}

