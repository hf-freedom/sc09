package com.logistics.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Branch {
    private Long id;
    private String name;
    private String coverageRegions;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
