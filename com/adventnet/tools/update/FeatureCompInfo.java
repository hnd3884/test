package com.adventnet.tools.update;

public class FeatureCompInfo
{
    private String productName;
    private Object[] versions;
    
    FeatureCompInfo() {
        this.productName = null;
        this.versions = new Object[0];
    }
    
    void addPrdVersionInfo(final FeaturePrdVersionInfo name) {
        final int len = this.versions.length;
        final Object[] tmp = new Object[len + 1];
        System.arraycopy(this.versions, 0, tmp, 0, len);
        tmp[len] = name;
        this.versions = tmp;
    }
    
    public Object[] getPrdVersionInfo() {
        return this.versions;
    }
    
    public void setProductName(final String name) {
        this.productName = name;
    }
    
    public String getProductName() {
        return this.productName;
    }
}
