package com.adventnet.tools.update;

public class FeaturePrdVersionInfo
{
    private String productVersion;
    private FeatureVersionComp fvc;
    
    FeaturePrdVersionInfo() {
        this.productVersion = null;
        this.fvc = null;
    }
    
    public void setFeatureVersionComp(final FeatureVersionComp fvc) {
        this.fvc = fvc;
    }
    
    public FeatureVersionComp getFeatureVersionComp() {
        return this.fvc;
    }
    
    public void setProductVersion(final String name) {
        this.productVersion = name;
    }
    
    public String getProductVersion() {
        return this.productVersion;
    }
}
