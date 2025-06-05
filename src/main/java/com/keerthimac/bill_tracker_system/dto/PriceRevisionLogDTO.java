package com.keerthimac.bill_tracker_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PriceRevisionLogDTO {
    private Long id;
    // private Long supplierMaterialPriceId; // Could include if needed
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate oldEffectiveFromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate newEffectiveFromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate oldEffectiveToDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate newEffectiveToDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime changeTimestamp;
    private String changedByUser;
    private String reasonForChange;
    // You might want to include supplier name and material name for easier display
    private String supplierName;
    private String masterMaterialName;
}