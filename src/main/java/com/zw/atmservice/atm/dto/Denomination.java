package com.zw.atmservice.atm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Denomination {
    private Long value;
    private String type;
}
