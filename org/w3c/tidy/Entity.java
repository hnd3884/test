package org.w3c.tidy;

public class Entity
{
    private String name;
    private short code;
    
    public Entity(final String name, final int n) {
        this.name = name;
        this.code = (short)n;
    }
    
    public short getCode() {
        return this.code;
    }
    
    public String getName() {
        return this.name;
    }
}
