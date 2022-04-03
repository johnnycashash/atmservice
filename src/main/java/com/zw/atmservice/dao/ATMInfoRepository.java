package com.zw.atmservice.dao;

import com.zw.atmservice.entity.ATMInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ATMInfoRepository extends MongoRepository<ATMInfo, Long> {
}
