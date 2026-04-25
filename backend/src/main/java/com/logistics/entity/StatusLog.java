package com.logistics.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusLog {
    private Long id;
    private Long orderId;
    private String orderNo;
    private String fromStatus;
    private String toStatus;
    private String operator;
    private String remark;
    private LocalDateTime createdAt;
}
