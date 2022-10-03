package com.sun.xml.internal.ws.util.xml;

import javax.xml.stream.Location;

public final class DummyLocation implements Location
{
    public static final Location INSTANCE;
    
    private DummyLocation() {
    }
    
    @Override
    public int getCharacterOffset() {
        return -1;
    }
    
    @Override
    public int getColumnNumber() {
        return -1;
    }
    
    @Override
    public int getLineNumber() {
        return -1;
    }
    
    @Override
    public String getPublicId() {
        return null;
    }
    
    @Override
    public String getSystemId() {
        return null;
    }
    
    static {
        INSTANCE = new DummyLocation();
    }
}
