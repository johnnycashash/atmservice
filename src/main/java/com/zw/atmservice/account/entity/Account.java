package com.zw.atmservice.account.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("account")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    Long acccountNumber;
    Boolean active;
    String firstName;
    String lastName;
    String address;
    Long balance;
    Long od;
}
