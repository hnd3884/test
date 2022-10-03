package com.google.zxing.oned.rss.expanded.decoders;

abstract class DecodedObject
{
    private final int newPosition;
    
    DecodedObject(final int newPosition) {
        this.newPosition = newPosition;
    }
    
    int getNewPosition() {
        return this.newPosition;
    }
}
