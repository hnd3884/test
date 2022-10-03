package com.adventnet.tools.update;

public class FeatureVersionComp
{
    private String[] versions;
    private String version;
    private String option;
    
    public FeatureVersionComp() {
        this.versions = new String[0];
        this.version = null;
        this.option = null;
    }
    
    public void addVersion(final String fName, final String option, final String versionName) {
        final int len = this.versions.length;
        final String[] tmp = new String[len + 3];
        System.arraycopy(this.versions, 0, tmp, 0, len);
        tmp[len] = fName;
        tmp[len + 1] = option;
        tmp[len + 2] = versionName;
        this.versions = tmp;
    }
    
    public String[] getVersions() {
        return this.versions;
    }
    
    public void setCompPatchOption(final String opt) {
        this.option = opt;
    }
    
    public String getCompPatchOption() {
        return this.option;
    }
    
    public void setCompPatchVersion(final String ver) {
        this.version = ver;
    }
    
    public String getCompPatchVersion() {
        return this.version;
    }
}
