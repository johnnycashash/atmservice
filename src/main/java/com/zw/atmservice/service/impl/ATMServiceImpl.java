package com.zw.atmservice.service.impl;

import com.zw.atmservice.dao.ATMInfoRepository;
import com.zw.atmservice.dto.*;
import com.zw.atmservice.entity.ATMInfo;
import com.zw.atmservice.exception.*;
import com.zw.atmservice.security.HashGenerator;
import com.zw.atmservice.service.ATMService;
import com.zw.atmservice.service.AccountService;
import com.zw.atmservice.service.CardService;
import com.zw.atmservice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.*;
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

    public BalanceCheckResponse getBalance(BalanceCheckRequest balanceCheckRequest) throws InvalidPinException, InactiveCardException, ATMGeneralException, InactiveAccountException {
        try {
            CardDetail cardDetailByCardNumber = cardService.findCardDetailByCardNumber(balanceCheckRequest.getCardNumber());

            if (Utility.isFalse(cardDetailByCardNumber.isActive())) {
                throw new InactiveCardException("Card is inactive");
            }

            String hashedPin = HashGenerator.getSHA(balanceCheckRequest.getPin());
            String hashedPinActual = cardDetailByCardNumber.getHashedPin();
            if (!isPinMatch(hashedPin, hashedPinActual)) {
                throw new InvalidPinException("Pin is invalid");
            }
            Long accountNumber = cardDetailByCardNumber.getAccountNumber();
            AccountDetail accountDetailByAccountNumber = accountService.findAccountDetailByAccountNumber(accountNumber);
            if (Utility.isFalse(accountDetailByAccountNumber.isActive())) {
                throw new InactiveAccountException("Account is inactive");
            }
            return getBalanceCheckResponse(accountDetailByAccountNumber.getBalance(), accountDetailByAccountNumber.getOd());

        } catch (CardNumberInvalidException | NoSuchAlgorithmException | AccountNumberInvalidException
                | CardGeneralException | AccountGeneralException e) {
            log.error("Exception in GetBalance", e);
            throw new ATMGeneralException("Details are invalid, Contact Bank Branch");
        }
    }

    private boolean isPinMatch(String hashedPin, String hashedPinActual) {
        return hashedPinActual.equalsIgnoreCase(hashedPin);
    }

    @Override
    public WithdrawBalanceResponse withdrawBalance(WithdrawBalanceRequest withdrawBalanceRequest) throws InactiveCardException, InactiveAccountException, InvalidPinException {

        try {
            //validate amount denomination
            Optional<ATMInfo> atmInfoOptional = atmInfoRepository.findById(withdrawBalanceRequest.getAtmId());
            if (!atmInfoOptional.isPresent()) {
                throw new InvalidATMConfigException("Invalid ATM ID");
            }
            CardDetail cardDetailByCardNumber = cardService.findCardDetailByCardNumber(withdrawBalanceRequest.getCardNumber());

            if (Utility.isFalse(cardDetailByCardNumber.isActive())) {
                throw new InactiveCardException("Card is inactive");
            }

            String hashedPin = HashGenerator.getSHA(withdrawBalanceRequest.getPin());
            String hashedPinActual = cardDetailByCardNumber.getHashedPin();
            if (!isPinMatch(hashedPin, hashedPinActual)) {
                throw new InvalidPinException("Pin is invalid");
            }
            Long accountNumber = cardDetailByCardNumber.getAccountNumber();
            AccountDetail accountDetailByAccountNumber = accountService.findAccountDetailByAccountNumber(accountNumber);
            if (Utility.isFalse(accountDetailByAccountNumber.isActive())) {
                throw new InactiveAccountException("Account is inactive");
            }
            Long balance = accountDetailByAccountNumber.getBalance();
            Long od = accountDetailByAccountNumber.getOd();
            if (withdrawBalanceRequest.getAmount() > balance + od) {
                throw new InsufficientAmountException("Account does not contain requested amount");
            }
            ATMInfo atmInfo = atmInfoOptional.get();
            List<DenominationDetail> denominationDetails = atmInfo.getDenominationDetails();
            Long totalAvailableBalanceInATM = denominationDetails.stream().filter(denominationDetail -> denominationDetail.getCount() != 0)
                    .map(denominationDetail -> denominationDetail.getCount() * denominationDetail.getDenomination().getValue()).reduce(0L, Long::sum);
            if (totalAvailableBalanceInATM < withdrawBalanceRequest.getAmount()) {
                throw new InsufficientAmountException("ATM does not contain requested amount");
            }

            //also validate if requested amount is a valid requested amount
            Long requestedWihdrawableAmt = withdrawBalanceRequest.getAmount();
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

            updateATMInfo(atmInfo, toBeUpdatedDtls);
            updateAccountBalance(accountDetailByAccountNumber, availableBalance, availableOD);
            return getWithdrawBalanceResponse(availableBalance, availableOD, denominationDetailList);
        } catch (CardNumberInvalidException | NoSuchAlgorithmException | AccountNumberInvalidException | CardGeneralException | AccountGeneralException | InsufficientAmountException e) {
            log.error("Exception in Withraw Balance", e);
            throw new ATMGeneralException("Details are invalid, Contact Bank Branch");
        } catch (InvalidATMConfigException e) {
            log.error("Exception in Withraw Balance", e);
            throw new ATMGeneralException("Internal Server Error, Contact Bank Branch");
        }
    }

    private boolean updateATMInfo(ATMInfo atmInfo, List<DenominationDetail> toBeUpdatedDtls) {
        Map<Long, Long> denoValByCount = toBeUpdatedDtls.stream().collect(Collectors.toMap(denominationDetail -> denominationDetail.getDenomination().getValue(), DenominationDetail::getCount));
        Set<Long> denoValByCountKeys = denoValByCount.keySet();
        atmInfo.getDenominationDetails().stream().filter(denominationDetail -> denoValByCountKeys.contains(denominationDetail.getDenomination().getValue())).forEach(denominationDetail -> denominationDetail.setCount(denoValByCount.get(denominationDetail.getDenomination().getValue())));
        atmInfoRepository.save(atmInfo);
        return true;
    }

    private boolean updateAccountBalance(AccountDetail accountDetail, long availableBalance, long availableOD) {
        return accountService.updateAccountBalance(accountDetail, availableBalance, availableOD);
    }

    private WithdrawBalanceResponse getWithdrawBalanceResponse(Long availableBalance, Long availableOD, List<DenominationDetail> denominationDetailList) {

        WithdrawBalanceResponse withdrawBalanceResponse = new WithdrawBalanceResponse();
        withdrawBalanceResponse.setRemainingBalance(availableBalance);
        withdrawBalanceResponse.setRamainingMaxBalance(availableBalance + availableOD);
        withdrawBalanceResponse.setDenominationDetails(denominationDetailList);
        return withdrawBalanceResponse;
    }

    private List<DenominationDetail> findDenominationDetails(Long requestedWihdrawableAmt, List<DenominationDetail> denominationDetails, List<DenominationDetail> toBeUpdatedDtls) {
        List<DenominationDetail> availableSortedDenDtl = denominationDetails.stream().filter(denominationDetail -> denominationDetail.getCount() != 0)
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


    private BalanceCheckResponse getBalanceCheckResponse(Long balance, Long od) {
        BalanceCheckResponse balanceCheckResponse = new BalanceCheckResponse();
        balanceCheckResponse.setBalance(balance);
        balanceCheckResponse.setMaxBalance(balance + od);
        return balanceCheckResponse;
    }

}
