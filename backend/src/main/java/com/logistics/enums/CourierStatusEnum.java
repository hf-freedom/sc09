package com.logistics.enums;

public enum CourierStatusEnum {
    IDLE("idle", "空闲"),
    BUSY("busy", "忙碌"),
    OFFLINE("offline", "离线");

    private final String code;
    private final String name;

    CourierStatusEnum(String code, String name) {
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
