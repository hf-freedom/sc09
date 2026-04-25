package com.logistics.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DispatchLog {
    private Long id;
    private Long orderId;
    private String orderNo;
    private Long courierId;
    private String courierName;
    private String action;
    private String reason;
    private LocalDateTime createdAt;
}
