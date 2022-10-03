package com.google.zxing.oned.rss.expanded.decoders;

final class DecodedInformation extends DecodedObject
{
    private final String newString;
    private final int remainingValue;
    private final boolean remaining;
    
    DecodedInformation(final int newPosition, final String newString) {
        super(newPosition);
        this.newString = newString;
        this.remaining = false;
        this.remainingValue = 0;
    }
    
    DecodedInformation(final int newPosition, final String newString, final int remainingValue) {
        super(newPosition);
        this.remaining = true;
        this.remainingValue = remainingValue;
        this.newString = newString;
    }
    
    String getNewString() {
        return this.newString;
    }
    
    boolean isRemaining() {
        return this.remaining;
    }
    
    int getRemainingValue() {
        return this.remainingValue;
    }
}
