package com.zoho.mickey.ha;

public interface TakeOverHandler extends Initializable
{
    default void initialize(final HAConfig config) {
    }
    
    void onTakeover() throws HAException;
}
