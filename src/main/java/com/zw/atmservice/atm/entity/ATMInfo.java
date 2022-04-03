package com.zw.atmservice.atm.entity;

import com.zw.atmservice.atm.dto.DenominationDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("atminfo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ATMInfo {
    @Id
    private Long atmId;
    private List<DenominationDetail> denominationDetails;
}
