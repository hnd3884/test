package com.adventnet.tools.prevalent;

import java.io.Serializable;

public class Version implements Serializable
{
    private static final long serialVersionUID = 3487495895819333L;
    private int[] pVersion;
    private int[] pType;
    private int[] pID;
    
    Version() {
        this.pVersion = null;
        this.pType = null;
        this.pID = null;
    }
    
    void setVersion(final int[] version) {
        this.pVersion = version;
    }
    
    int[] getVersion() {
        return this.pVersion;
    }
    
    void setType(final int[] type) {
        this.pType = type;
    }
    
    int[] getType() {
        return this.pType;
    }
    
    void setID(final int[] id) {
        this.pID = id;
    }
    
    int[] getID() {
        return this.pID;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("\nVersion :" + new String(Encode.revShiftBytes(this.pVersion)));
        buf.append("\nType :" + new String(Encode.revShiftBytes(this.pType)));
        buf.append("\nID :" + new String(Encode.revShiftBytes(this.pID)));
        return buf.toString();
    }
}
