package com.google.zxing.oned.rss.expanded.decoders;

final class CurrentParsingState
{
    private int position;
    private State encoding;
    
    CurrentParsingState() {
        this.position = 0;
        this.encoding = State.NUMERIC;
    }
    
    int getPosition() {
        return this.position;
    }
    
    void setPosition(final int position) {
        this.position = position;
    }
    
    void incrementPosition(final int delta) {
        this.position += delta;
    }
    
    boolean isAlpha() {
        return this.encoding == State.ALPHA;
    }
    
    boolean isNumeric() {
        return this.encoding == State.NUMERIC;
    }
    
    boolean isIsoIec646() {
        return this.encoding == State.ISO_IEC_646;
    }
    
    void setNumeric() {
        this.encoding = State.NUMERIC;
    }
    
    void setAlpha() {
        this.encoding = State.ALPHA;
    }
    
    void setIsoIec646() {
        this.encoding = State.ISO_IEC_646;
    }
    
    private enum State
    {
        NUMERIC, 
        ALPHA, 
        ISO_IEC_646;
    }
}
