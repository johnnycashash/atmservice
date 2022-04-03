package com.zw.atmservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetail {
    Long acccountNumber;
    boolean active;
    String firstName;
    String LastName;
    String address;
    Long balance;
    Long od;
}
