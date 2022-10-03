package com.sun.org.apache.xerces.internal.util;

import javax.xml.stream.Location;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;

public final class StAXLocationWrapper implements XMLLocator
{
    private Location fLocation;
    
    public StAXLocationWrapper() {
        this.fLocation = null;
    }
    
    public void setLocation(final Location location) {
        this.fLocation = location;
    }
    
    public Location getLocation() {
        return this.fLocation;
    }
    
    @Override
    public String getPublicId() {
        if (this.fLocation != null) {
            return this.fLocation.getPublicId();
        }
        return null;
    }
    
    @Override
    public String getLiteralSystemId() {
        if (this.fLocation != null) {
            return this.fLocation.getSystemId();
        }
        return null;
    }
    
    @Override
    public String getBaseSystemId() {
        return null;
    }
    
    @Override
    public String getExpandedSystemId() {
        return this.getLiteralSystemId();
    }
    
    @Override
    public int getLineNumber() {
        if (this.fLocation != null) {
            return this.fLocation.getLineNumber();
        }
        return -1;
    }
    
    @Override
    public int getColumnNumber() {
        if (this.fLocation != null) {
            return this.fLocation.getColumnNumber();
        }
        return -1;
    }
    
    @Override
    public int getCharacterOffset() {
        if (this.fLocation != null) {
            return this.fLocation.getCharacterOffset();
        }
        return -1;
    }
    
    @Override
    public String getEncoding() {
        return null;
    }
    
    @Override
    public String getXMLVersion() {
        return null;
    }
}
