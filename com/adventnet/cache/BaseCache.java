package com.adventnet.cache;

public abstract class BaseCache implements Cache
{
    private String name;
    
    public BaseCache() {
        this.name = null;
        this.name = "";
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
