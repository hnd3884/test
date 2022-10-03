package org.apache.commons.csv;

final class Token
{
    private static final int INITIAL_TOKEN_LENGTH = 50;
    Type type;
    final StringBuilder content;
    boolean isReady;
    
    Token() {
        this.type = Type.INVALID;
        this.content = new StringBuilder(50);
    }
    
    void reset() {
        this.content.setLength(0);
        this.type = Type.INVALID;
        this.isReady = false;
    }
    
    @Override
    public String toString() {
        return this.type.name() + " [" + this.content.toString() + "]";
    }
    
    enum Type
    {
        INVALID, 
        TOKEN, 
        EOF, 
        EORECORD, 
        COMMENT;
    }
}
