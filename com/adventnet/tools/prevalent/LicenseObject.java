package com.adventnet.tools.prevalent;

import java.io.Serializable;

public class LicenseObject implements Serializable
{
    private static final long serialVersionUID = 9051681332338468915L;
    private int[] licenseObject;
    
    public LicenseObject(final int[] licenseObject) {
        this.licenseObject = null;
        this.licenseObject = licenseObject;
    }
    
    public int[] getLicenseObject() {
        return this.licenseObject;
    }
}
