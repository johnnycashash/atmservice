package com.zw.atmservice.atm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtmInfoDetail {
    private Long atmId;
    private List<DenominationDetail> denominationDetails;
}
