package com.zw.atmservice.account.dao;

import com.zw.atmservice.account.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends MongoRepository<Account, Long> {
}
