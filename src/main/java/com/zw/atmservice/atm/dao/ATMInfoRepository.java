package com.zw.atmservice.atm.dao;

import com.zw.atmservice.atm.entity.ATMInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ATMInfoRepository extends MongoRepository<ATMInfo, Long> {
}
