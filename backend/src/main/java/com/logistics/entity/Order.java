package com.logistics.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private String receiverAddress;
    private String region;
    private BigDecimal weight;
    private Boolean urgent;
    private String status;
    private Long courierId;
    private String courierName;
    private String failReason;
    private Long lastFailedCourierId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
