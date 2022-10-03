package com.turo.pushy.apns;

public enum DeliveryPriority
{
    IMMEDIATE(10), 
    CONSERVE_POWER(5);
    
    private final int code;
    
    private DeliveryPriority(final int code) {
        this.code = code;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public static DeliveryPriority getFromCode(final int code) {
        for (final DeliveryPriority priority : values()) {
            if (priority.getCode() == code) {
                return priority;
            }
        }
        throw new IllegalArgumentException(String.format("No delivery priority found with code %d", code));
    }
}
