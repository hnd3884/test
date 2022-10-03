package com.adventnet.tools.prevalent;

import java.io.Serializable;

public class BaseObject implements Serializable
{
    private static final long serialVersionUID = 7315249025416722086L;
    private int[] baseObject;
    
    public BaseObject(final int[] licenseObject) {
        this.baseObject = null;
        this.baseObject = licenseObject;
    }
    
    public int[] getBaseObject() {
        return this.baseObject;
    }
}
