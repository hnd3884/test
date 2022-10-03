package com.azul.crs.client.service;

public interface FileTailerListener
{
    default void fileNotFound() {
    }
    
    default void fileRotated(final String details) {
    }
    
    default void eofReached() {
    }
    
    void handle(final byte[] p0, final int p1);
    
    default void handle(final Exception ex) {
    }
    
    default void interrupted() {
    }
}
