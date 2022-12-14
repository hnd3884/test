package com.google.api.client.util;

public interface Clock
{
    public static final Clock SYSTEM = new Clock() {
        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    };
    
    long currentTimeMillis();
}
