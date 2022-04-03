package com.zw.atmservice.atm.service.impl;

import com.zw.atmservice.account.dto.AccountDetail;
import com.zw.atmservice.account.exception.AccountGeneralException;
import com.zw.atmservice.account.service.AccountService;
import com.zw.atmservice.atm.dao.ATMInfoRepository;
import com.zw.atmservice.atm.dto.*;
import com.zw.atmservice.atm.entity.ATMInfo;
import com.zw.atmservice.atm.exception.*;
import com.zw.atmservice.atm.security.HashGenerator;
import com.zw.atmservice.atm.service.ATMService;
import com.zw.atmservice.atm.service.WithdrawUpdateService;
import com.zw.atmservice.atm.util.Utility;
import com.zw.atmservice.card.dto.CardDetail;
import com.zw.atmservice.card.exception.CardGeneralException;
import com.zw.atmservice.card.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ATMServiceImpl implements ATMService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private CardService cardService;
    @Autowired
    private ATMInfoRepository atmInfoRepository;
    @Autowired
    private WithdrawUpdateService withdrawUpdateService;

    public BalanceCheckResponse getBalance(BalanceCheckRequest balanceCheckRequest)
            throws InvalidPinException, InactiveCardException, ATMGeneralException, InactiveAccountException {
        try {
            CardDetail cardDetailByCardNumber = cardService.findCardDetailByCardNumber(balanceCheckRequest.getCardNumber());

            checkCardInactivity(cardDetailByCardNumber);

            checkPinValidity(balanceCheckRequest.getPin(), cardDetailByCardNumber.getHashedPin());
            Long accountNumber = cardDetailByCardNumber.getAccountNumber();
            AccountDetail accountDetailByAccountNumber = accountService.findAccountDetailByAccountNumber(accountNumber);
            checkAccountValidity(accountDetailByAccountNumber.isActive());
            return getBalanceCheckResponse(accountDetailByAccountNumber.getBalance(), accountDetailByAccountNumber.getOd());

        } catch (NoSuchAlgorithmException | CardGeneralException | AccountGeneralException e) {
            log.error("Exception in GetBalance", e);
            throw new ATMGeneralException("Details are invalid, Contact Bank Branch");
        }
    }

    private void checkAccountValidity(boolean isActive) throws InactiveAccountException {
        if (Utility.isFalse(isActive)) {
            throw new InactiveAccountException("Account is inactive");
        }
    }

    private void checkPinValidity(String requestPin, String actualPin) throws NoSuchAlgorithmException, InvalidPinException {
        String hashedPin = HashGenerator.getSHA(requestPin);
        if (!isPinMatch(hashedPin, actualPin)) {
            throw new InvalidPinException("Pin is invalid");
        }
    }

    private void checkCardInactivity(CardDetail cardDetailByCardNumber) throws InactiveCardException {
        if (Utility.isFalse(cardDetailByCardNumber.isActive())) {
            throw new InactiveCardException("Card is inactive");
        }
    }

    private boolean isPinMatch(String hashedPin, String hashedPinActual) {
        return hashedPinActual.equalsIgnoreCase(hashedPin);
    }

    @Override
    public WithdrawBalanceResponse withdrawBalance(WithdrawBalanceRequest withdrawBalanceRequest)
            throws InactiveCardException, InactiveAccountException, InvalidPinException, InsufficientAmountException, AmountInDispensableException, ATMGeneralException {

        try {
            Optional<ATMInfo> atmInfoOptional = atmInfoRepository.findById(withdrawBalanceRequest.getAtmId());
            if (!atmInfoOptional.isPresent()) {
                throw new InvalidATMConfigException("Invalid ATM ID");
            }
            checkAmountDispensable(withdrawBalanceRequest.getAmount());
            CardDetail cardDetailByCardNumber = cardService.findCardDetailByCardNumber(withdrawBalanceRequest.getCardNumber());

            checkCardInactivity(cardDetailByCardNumber);

            checkPinValidity(withdrawBalanceRequest.getPin(), cardDetailByCardNumber.getHashedPin());
            Long accountNumber = cardDetailByCardNumber.getAccountNumber();
            AccountDetail accountDetailByAccountNumber = accountService.findAccountDetailByAccountNumber(accountNumber);
            checkAccountValidity(accountDetailByAccountNumber.isActive());
            Long balance = accountDetailByAccountNumber.getBalance();
            Long od = accountDetailByAccountNumber.getOd();
            checkAccountContainsAmount(withdrawBalanceRequest.getAmount(), balance, od);
            ATMInfo atmInfo = atmInfoOptional.get();
            List<DenominationDetail> denominationDetails = atmInfo.getDenominationDetails();
            Long requestedWihdrawableAmt = withdrawBalanceRequest.getAmount();

            checkATMContainsAmount(requestedWihdrawableAmt, denominationDetails);

            List<DenominationDetail> toBeUpdatedDtls = new ArrayList<>();
            List<DenominationDetail> denominationDetailList = findDenominationDetails(requestedWihdrawableAmt, denominationDetails, toBeUpdatedDtls);

            long availableBalance;
            long availableOD;
            if (requestedWihdrawableAmt <= accountDetailByAccountNumber.getBalance()) {
                availableBalance = accountDetailByAccountNumber.getBalance() - requestedWihdrawableAmt;
                availableOD = accountDetailByAccountNumber.getOd();
            } else {
                availableBalance = 0L;
                availableOD = accountDetailByAccountNumber.getOd() - (requestedWihdrawableAmt - accountDetailByAccountNumber.getBalance());
            }

            withdrawUpdateService.executeAccountAndAtmUpdate(accountDetailByAccountNumber, atmInfo, toBeUpdatedDtls, availableBalance, availableOD);
            return getWithdrawBalanceResponse(availableBalance, availableOD, denominationDetailList);
        } catch (NoSuchAlgorithmException | CardGeneralException | AccountGeneralException e) {
            log.error("Exception in Withraw Balance", e);
            throw new ATMGeneralException("Details are invalid, Contact Bank Branch");
        } catch (InvalidATMConfigException e) {
            log.error("Exception in Withraw Balance", e);
            throw new ATMGeneralException("Internal Server Error, Contact Bank Branch");
        }
    }

    private void checkATMContainsAmount(Long requestAmount, List<DenominationDetail> denominationDetails) throws InsufficientAmountException {
        Long totalAvailableBalanceInATM = denominationDetails.stream().filter(denominationDetail -> denominationDetail.getCount() != 0)
                .map(denominationDetail -> denominationDetail.getCount() * denominationDetail.getDenomination().getValue()).reduce(0L, Long::sum);
        if (totalAvailableBalanceInATM < requestAmount) {
            throw new InsufficientAmountException("ATM does not contain requested amount");
        }
    }

    private void checkAccountContainsAmount(Long requestAmount, Long balance, Long od) throws InsufficientAmountException {
        if (requestAmount > balance + od) {
            throw new InsufficientAmountException("Account does not contain requested amount");
        }
    }

    private void checkAmountDispensable(Long amount) throws AmountInDispensableException {
        if (amount <= 0 || amount % 5 != 0) {
            throw new AmountInDispensableException("This amount cannot be dispensed");
        }
    }


    private BalanceCheckResponse getBalanceCheckResponse(Long balance, Long od) {
        BalanceCheckResponse balanceCheckResponse = new BalanceCheckResponse();
        balanceCheckResponse.setBalance(balance);
        balanceCheckResponse.setMaxBalance(balance + od);
        return balanceCheckResponse;
    }

    private WithdrawBalanceResponse getWithdrawBalanceResponse(Long availableBalance, Long availableOD, List<DenominationDetail> denominationDetailList) {
        WithdrawBalanceResponse withdrawBalanceResponse = new WithdrawBalanceResponse();
        withdrawBalanceResponse.setRemainingBalance(availableBalance);
        withdrawBalanceResponse.setRamainingMaxBalance(availableBalance + availableOD);
        withdrawBalanceResponse.setDenominationDetails(denominationDetailList);
        return withdrawBalanceResponse;
    }

    private List<DenominationDetail> findDenominationDetails(Long requestedWihdrawableAmt, List<DenominationDetail> denominationDetails, List<DenominationDetail> toBeUpdatedDtls) {
        List<DenominationDetail> availableSortedDenDtl = denominationDetails.stream()
                .sorted(Comparator.comparingLong(o -> ((DenominationDetail) o).getDenomination().getValue()).reversed()).collect(Collectors.toList());
        List<DenominationDetail> out = new ArrayList<>();
        for (DenominationDetail denominationDetail : availableSortedDenDtl) {
            DenominationDetail toBeUpdatedDtl = new DenominationDetail();
            BeanUtils.copyProperties(denominationDetail, toBeUpdatedDtl);
            DenominationDetail resultDenominationDtl = new DenominationDetail();
            requestedWihdrawableAmt = findDenominationDetail(requestedWihdrawableAmt, denominationDetail, resultDenominationDtl, toBeUpdatedDtl);
            out.add(resultDenominationDtl);
            toBeUpdatedDtls.add(toBeUpdatedDtl);
        }
        return out;
    }

    private long findDenominationDetail(Long requestedWihdrawableAmt, DenominationDetail denominationDetail,
                                        DenominationDetail denominationRequired, DenominationDetail toBeUpdatedDtl) {
        long expectedRequiredCount = requestedWihdrawableAmt / denominationDetail.getDenomination().getValue();
        long actualRequiredCount;
        long remainingAmt;
        if (expectedRequiredCount > denominationDetail.getCount()) {
            actualRequiredCount = denominationDetail.getCount();
            remainingAmt = requestedWihdrawableAmt - (denominationDetail.getCount() * denominationDetail.getDenomination().getValue());
        } else {
            actualRequiredCount = expectedRequiredCount;
            remainingAmt = requestedWihdrawableAmt % denominationDetail.getDenomination().getValue();
        }
        toBeUpdatedDtl.setCount(denominationDetail.getCount() - actualRequiredCount);
        denominationRequired.setDenomination(new Denomination(denominationDetail.getDenomination().getValue(), denominationDetail.getDenomination().getType()));
        denominationRequired.setCount(actualRequiredCount);
        return remainingAmt;
    }

}
