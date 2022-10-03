package com.google.zxing.oned.rss;

public class DataCharacter
{
    private final int value;
    private final int checksumPortion;
    
    public DataCharacter(final int value, final int checksumPortion) {
        this.value = value;
        this.checksumPortion = checksumPortion;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int getChecksumPortion() {
        return this.checksumPortion;
    }
}
