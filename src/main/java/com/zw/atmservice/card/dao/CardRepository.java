package com.zw.atmservice.card.dao;

import com.zw.atmservice.card.entity.Card;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends MongoRepository<Card, Long> {
}
