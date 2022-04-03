package com.zw.atmservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DenominationDetail {
    private Denomination denomination;
    private Long count;
}
