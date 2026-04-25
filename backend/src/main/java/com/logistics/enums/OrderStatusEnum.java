package com.logistics.enums;

public enum OrderStatusEnum {
    PENDING_ASSIGN("pending_assign", "待分配"),
    ASSIGNED("assigned", "已分配"),
    ACCEPTED("accepted", "已接单"),
    PICKING_UP("picking_up", "取件中"),
    DELIVERING("delivering", "配送中"),
    SIGNED("signed", "已签收"),
    FAILED("failed", "配送失败");

    private final String code;
    private final String name;

    OrderStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
