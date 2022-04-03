package com.zw.atmservice.config;

import com.zw.atmservice.account.dao.AccountRepository;
import com.zw.atmservice.account.entity.Account;
import com.zw.atmservice.atm.dao.ATMInfoRepository;
import com.zw.atmservice.atm.dto.Denomination;
import com.zw.atmservice.atm.dto.DenominationDetail;
import com.zw.atmservice.atm.entity.ATMInfo;
import com.zw.atmservice.atm.security.HashGenerator;
import com.zw.atmservice.card.dao.CardRepository;
import com.zw.atmservice.card.entity.Card;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;

@Configuration
@Profile({"dev", "local"})
@Slf4j
public class MongoConfig {
    @Bean
    CommandLineRunner commandLineRunner(AccountRepository accountRepository, CardRepository cardRepository, ATMInfoRepository atmInfoRepository) {
        log.info("Creating static data");
        return strings -> {
            accountRepository.save(new Account(1L, true, "Jagan", "Singh", "add1", 10000L, 50000L));
            accountRepository.save(new Account(2L, false, "Delhi", "Singh", "add2", 1000L, 5000L));

            cardRepository.save(new Card(1111L, 1L, HashGenerator.getSHA("1234"), true));
            cardRepository.save(new Card(1112L, 1L, HashGenerator.getSHA("1235"), false));
            cardRepository.save(new Card(2221L, 2L, HashGenerator.getSHA("2345"), false));

            ArrayList<DenominationDetail> denominationDetails = new ArrayList<>();
            denominationDetails.add(new DenominationDetail(new Denomination(50L, "pounds"), 10L));
            denominationDetails.add(new DenominationDetail(new Denomination(20L, "pounds"), 30L));
            denominationDetails.add(new DenominationDetail(new Denomination(10L, "pounds"), 30L));
            denominationDetails.add(new DenominationDetail(new Denomination(5L, "pounds"), 20L));
            atmInfoRepository.save(new ATMInfo(1L, denominationDetails));
        };
    }
}