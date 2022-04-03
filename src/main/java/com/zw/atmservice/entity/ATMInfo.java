package com.zw.atmservice.entity;

import com.zw.atmservice.dto.DenominationDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("atminfo")
@Data
@AllArgsConstructor
public class ATMInfo {
    @Id
    private Long atmId;
    private List<DenominationDetail> denominationDetails;
}
