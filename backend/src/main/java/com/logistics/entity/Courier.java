package com.logistics.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Courier {
    private Long id;
    private String name;
    private String region;
    private Integer currentTaskCount;
    private Integer maxTaskCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
