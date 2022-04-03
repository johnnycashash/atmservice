package com.zw.atmservice.atm.service;

import com.zw.atmservice.account.dto.AccountDetail;
import com.zw.atmservice.account.exception.AccountGeneralException;
import com.zw.atmservice.account.exception.AccountNumberInvalidException;
import com.zw.atmservice.account.service.AccountService;
import com.zw.atmservice.atm.dao.ATMInfoRepository;
import com.zw.atmservice.atm.dto.*;
import com.zw.atmservice.atm.entity.ATMInfo;
import com.zw.atmservice.atm.exception.*;
import com.zw.atmservice.atm.service.WithdrawUpdateService;
import com.zw.atmservice.atm.service.impl.ATMServiceImpl;
import com.zw.atmservice.card.dto.CardDetail;
import com.zw.atmservice.card.exception.CardGeneralException;
import com.zw.atmservice.card.exception.CardNumberInvalidException;
import com.zw.atmservice.card.service.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ATMServiceTest {

    @Mock
    private AccountService accountService;
    @Mock
    private CardService cardService;
    @Mock
    private ATMInfoRepository atmInfoRepository;
    @Mock
    private WithdrawUpdateService withdrawUpdateService;

    @InjectMocks
    ATMServiceImpl atmService = new ATMServiceImpl();

    @Test
    void getBalanceInactiveCardException() throws CardGeneralException, CardNumberInvalidException {
        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(false);
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        BalanceCheckRequest balanceCheckRequest = new BalanceCheckRequest();
        balanceCheckRequest.setCardNumber(2333L);
        InactiveCardException thrown = assertThrows(
                InactiveCardException.class,
                () -> atmService.getBalance(balanceCheckRequest),
                "Expected getBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Card is inactive"));
    }

    @Test
    void getBalanceInvalidPinException() throws CardGeneralException, CardNumberInvalidException {
        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setHashedPin("fail");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        BalanceCheckRequest balanceCheckRequest = new BalanceCheckRequest();
        balanceCheckRequest.setCardNumber(2333L);
        balanceCheckRequest.setPin("nomatch");
        InvalidPinException thrown = assertThrows(
                InvalidPinException.class,
                () -> atmService.getBalance(balanceCheckRequest),
                "Expected getBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Pin is invalid"));
    }


    @Test
    void getBalanceInactiveAccountException() throws CardGeneralException, CardNumberInvalidException, AccountNumberInvalidException, AccountGeneralException {
        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setAccountNumber(1L);
        cardDetail.setHashedPin("4945a70fa7f9c13fe1931a3372ac5798140d42eba74d0dd805a4a216ed3a8142");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setActive(false);
        Mockito.when(accountService.findAccountDetailByAccountNumber(Mockito.anyLong())).thenReturn(accountDetail);
        BalanceCheckRequest balanceCheckRequest = new BalanceCheckRequest();
        balanceCheckRequest.setCardNumber(2333L);
        balanceCheckRequest.setPin("match");
        InactiveAccountException thrown = assertThrows(
                InactiveAccountException.class,
                () -> atmService.getBalance(balanceCheckRequest),
                "Expected getBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Account is inactive"));
    }

    @Test
    void getBalance() throws CardGeneralException, CardNumberInvalidException, AccountNumberInvalidException, AccountGeneralException, InactiveCardException, InactiveAccountException, ATMGeneralException, InvalidPinException {
        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setAccountNumber(1L);
        cardDetail.setHashedPin("4945a70fa7f9c13fe1931a3372ac5798140d42eba74d0dd805a4a216ed3a8142");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setActive(true);
        accountDetail.setBalance(100L);
        accountDetail.setOd(500L);
        Mockito.when(accountService.findAccountDetailByAccountNumber(Mockito.anyLong())).thenReturn(accountDetail);
        BalanceCheckRequest balanceCheckRequest = new BalanceCheckRequest();
        balanceCheckRequest.setCardNumber(2333L);
        balanceCheckRequest.setPin("match");
        BalanceCheckResponse balance = atmService.getBalance(balanceCheckRequest);

        assertEquals(balance.getBalance(), accountDetail.getBalance());
        assertEquals(balance.getMaxBalance(), accountDetail.getBalance() + accountDetail.getOd());
    }


    @Test
    void withdrawBalanceInvalidATMConfigException() throws CardGeneralException, CardNumberInvalidException {
        Mockito.when(atmInfoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(false);
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAtmId(2333L);
        withdrawBalanceRequest.setCardNumber(2333L);
        ATMGeneralException thrown = assertThrows(
                ATMGeneralException.class,
                () -> atmService.withdrawBalance(withdrawBalanceRequest),
                "Expected withdrawBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Internal Server Error, Contact Bank Branch"));
    }

    @Test
    void withdrawBalanceInactiveCardException() throws CardGeneralException, CardNumberInvalidException {
        ATMInfo atmInfo = new ATMInfo();
        Mockito.when(atmInfoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(atmInfo));

        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(false);
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAtmId(2333L);
        withdrawBalanceRequest.setCardNumber(2333L);
        withdrawBalanceRequest.setAmount(155L);
        InactiveCardException thrown = assertThrows(
                InactiveCardException.class,
                () -> atmService.withdrawBalance(withdrawBalanceRequest),
                "Expected withdrawBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Card is inactive"));
    }

    @Test
    void withdrawBalanceInvalidPinException() throws CardGeneralException, CardNumberInvalidException {
        ATMInfo atmInfo = new ATMInfo();
        Mockito.when(atmInfoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(atmInfo));

        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setHashedPin("fail");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAtmId(2333L);
        withdrawBalanceRequest.setCardNumber(2333L);
        withdrawBalanceRequest.setPin("nomatch");
        withdrawBalanceRequest.setAmount(155L);
        InvalidPinException thrown = assertThrows(
                InvalidPinException.class,
                () -> atmService.withdrawBalance(withdrawBalanceRequest),
                "Expected withdrawBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Pin is invalid"));
    }


    @Test
    void withdrawBalanceInactiveAccountException() throws CardGeneralException, CardNumberInvalidException, AccountNumberInvalidException, AccountGeneralException {
        ATMInfo atmInfo = new ATMInfo();
        Mockito.when(atmInfoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(atmInfo));

        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setAccountNumber(1L);
        cardDetail.setHashedPin("4945a70fa7f9c13fe1931a3372ac5798140d42eba74d0dd805a4a216ed3a8142");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setActive(false);
        Mockito.when(accountService.findAccountDetailByAccountNumber(Mockito.anyLong())).thenReturn(accountDetail);
        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAtmId(2333L);
        withdrawBalanceRequest.setCardNumber(2333L);
        withdrawBalanceRequest.setPin("match");
        withdrawBalanceRequest.setAmount(155L);
        InactiveAccountException thrown = assertThrows(
                InactiveAccountException.class,
                () -> atmService.withdrawBalance(withdrawBalanceRequest),
                "Expected withdrawBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Account is inactive"));
    }

    @Test
    void withdrawBalanceAmountInDispensableException() throws CardGeneralException, CardNumberInvalidException, AccountNumberInvalidException, AccountGeneralException {
        ATMInfo atmInfo = new ATMInfo();
        Mockito.when(atmInfoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(atmInfo));

        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setAccountNumber(1L);
        cardDetail.setHashedPin("4945a70fa7f9c13fe1931a3372ac5798140d42eba74d0dd805a4a216ed3a8142");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setActive(true);
        Mockito.when(accountService.findAccountDetailByAccountNumber(Mockito.anyLong())).thenReturn(accountDetail);
        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAtmId(2333L);
        withdrawBalanceRequest.setAmount(153L);
        withdrawBalanceRequest.setCardNumber(2333L);
        withdrawBalanceRequest.setPin("match");
        AmountInDispensableException thrown = assertThrows(
                AmountInDispensableException.class,
                () -> atmService.withdrawBalance(withdrawBalanceRequest),
                "Expected withdrawBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("This amount cannot be dispensed"));
    }


    @Test
    void withdrawBalanceAmountInDispensableExceptionNegBal() throws CardGeneralException, CardNumberInvalidException, AccountNumberInvalidException, AccountGeneralException {
        ATMInfo atmInfo = new ATMInfo();
        Mockito.when(atmInfoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(atmInfo));

        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setAccountNumber(1L);
        cardDetail.setHashedPin("4945a70fa7f9c13fe1931a3372ac5798140d42eba74d0dd805a4a216ed3a8142");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setActive(true);
        Mockito.when(accountService.findAccountDetailByAccountNumber(Mockito.anyLong())).thenReturn(accountDetail);
        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAtmId(2333L);
        withdrawBalanceRequest.setAmount(-153L);
        withdrawBalanceRequest.setCardNumber(2333L);
        withdrawBalanceRequest.setPin("match");
        AmountInDispensableException thrown = assertThrows(
                AmountInDispensableException.class,
                () -> atmService.withdrawBalance(withdrawBalanceRequest),
                "Expected withdrawBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("This amount cannot be dispensed"));
    }

    @Test
    void withdrawBalanceInsufficientAmountException() throws CardGeneralException, CardNumberInvalidException, AccountNumberInvalidException, AccountGeneralException {
        ATMInfo atmInfo = new ATMInfo();
        Mockito.when(atmInfoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(atmInfo));

        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setAccountNumber(1L);
        cardDetail.setHashedPin("4945a70fa7f9c13fe1931a3372ac5798140d42eba74d0dd805a4a216ed3a8142");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setActive(true);
        accountDetail.setBalance(500L);
        accountDetail.setOd(2500L);
        Mockito.when(accountService.findAccountDetailByAccountNumber(Mockito.anyLong())).thenReturn(accountDetail);
        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAtmId(2333L);
        withdrawBalanceRequest.setAmount(10055L);
        withdrawBalanceRequest.setCardNumber(2333L);
        withdrawBalanceRequest.setPin("match");
        InsufficientAmountException thrown = assertThrows(
                InsufficientAmountException.class,
                () -> atmService.withdrawBalance(withdrawBalanceRequest),
                "Expected withdrawBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Account does not contain requested amount"));
    }

    @Test
    void withdrawBalanceInsufficientAmountATMException() throws CardGeneralException, CardNumberInvalidException, AccountNumberInvalidException, AccountGeneralException {
        ATMInfo atmInfo = new ATMInfo();
        ArrayList<DenominationDetail> denominationDetails = new ArrayList<>();
        denominationDetails.add(new DenominationDetail(new Denomination(50L, "pounds"), 10L));
        denominationDetails.add(new DenominationDetail(new Denomination(20L, "pounds"), 30L));
        denominationDetails.add(new DenominationDetail(new Denomination(10L, "pounds"), 30L));
        denominationDetails.add(new DenominationDetail(new Denomination(5L, "pounds"), 20L));
        atmInfo.setDenominationDetails(denominationDetails);
        Mockito.when(atmInfoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(atmInfo));

        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setAccountNumber(1L);
        cardDetail.setHashedPin("4945a70fa7f9c13fe1931a3372ac5798140d42eba74d0dd805a4a216ed3a8142");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setActive(true);
        accountDetail.setBalance(50000000L);
        accountDetail.setOd(2500L);
        Mockito.when(accountService.findAccountDetailByAccountNumber(Mockito.anyLong())).thenReturn(accountDetail);
        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAtmId(2333L);
        withdrawBalanceRequest.setAmount(100055L);
        withdrawBalanceRequest.setCardNumber(2333L);
        withdrawBalanceRequest.setPin("match");
        InsufficientAmountException thrown = assertThrows(
                InsufficientAmountException.class,
                () -> atmService.withdrawBalance(withdrawBalanceRequest),
                "Expected withdrawBalance() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("ATM does not contain requested amount"));
    }

    @Test
    void withdrawBalance() throws CardGeneralException, CardNumberInvalidException, AccountNumberInvalidException, AccountGeneralException, InactiveCardException, InactiveAccountException, ATMGeneralException, InvalidPinException, AmountInDispensableException, InsufficientAmountException {
        ATMInfo atmInfo = new ATMInfo();
        ArrayList<DenominationDetail> denominationDetails = new ArrayList<>();
        denominationDetails.add(new DenominationDetail(new Denomination(50L, "pounds"), 10L));
        denominationDetails.add(new DenominationDetail(new Denomination(20L, "pounds"), 30L));
        denominationDetails.add(new DenominationDetail(new Denomination(10L, "pounds"), 30L));
        denominationDetails.add(new DenominationDetail(new Denomination(5L, "pounds"), 20L));
        atmInfo.setDenominationDetails(denominationDetails);
        Mockito.when(atmInfoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(atmInfo));

        CardDetail cardDetail = new CardDetail();
        cardDetail.setActive(true);
        cardDetail.setAccountNumber(1L);
        cardDetail.setHashedPin("4945a70fa7f9c13fe1931a3372ac5798140d42eba74d0dd805a4a216ed3a8142");
        Mockito.when(cardService.findCardDetailByCardNumber(Mockito.anyLong())).thenReturn(cardDetail);
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setActive(true);
        accountDetail.setBalance(1000L);
        accountDetail.setOd(5000L);
        Mockito.when(accountService.findAccountDetailByAccountNumber(Mockito.anyLong())).thenReturn(accountDetail);
        Mockito.doNothing().when(withdrawUpdateService)
                .executeAccountAndAtmUpdate(Mockito.any(AccountDetail.class), Mockito.any(ATMInfo.class), Mockito.anyList(), Mockito.anyLong(), Mockito.anyLong());

        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAtmId(2333L);
        withdrawBalanceRequest.setAmount(155L);
        withdrawBalanceRequest.setCardNumber(2333L);
        withdrawBalanceRequest.setPin("match");
        WithdrawBalanceResponse balance = atmService.withdrawBalance(withdrawBalanceRequest);

        assertEquals(balance.getRemainingBalance(), accountDetail.getBalance() - withdrawBalanceRequest.getAmount());
        assertEquals(balance.getRamainingMaxBalance(), accountDetail.getBalance() - withdrawBalanceRequest.getAmount() + accountDetail.getOd());
        List<DenominationDetail> denominationDetails1 = balance.getDenominationDetails();
        Long count50s = denominationDetails1.stream().filter(denominationDetail -> denominationDetail.getDenomination().getValue() == 50L).collect(Collectors.toList()).get(0).getCount();
        assertEquals(count50s, 3);

        Long count20s = denominationDetails1.stream().filter(denominationDetail -> denominationDetail.getDenomination().getValue() == 20L).collect(Collectors.toList()).get(0).getCount();
        assertEquals(count20s, 0);

        Long count10s = denominationDetails1.stream().filter(denominationDetail -> denominationDetail.getDenomination().getValue() == 10L).collect(Collectors.toList()).get(0).getCount();
        assertEquals(count10s, 0);

        Long count5s = denominationDetails1.stream().filter(denominationDetail -> denominationDetail.getDenomination().getValue() == 5L).collect(Collectors.toList()).get(0).getCount();
        assertEquals(count5s, 1);

    }
}