package org.jfree.base.modules;

public class DefaultModuleInfo implements ModuleInfo
{
    private String moduleClass;
    private String majorVersion;
    private String minorVersion;
    private String patchLevel;
    
    public DefaultModuleInfo() {
    }
    
    public DefaultModuleInfo(final String moduleClass, final String majorVersion, final String minorVersion, final String patchLevel) {
        if (moduleClass == null) {
            throw new NullPointerException("Module class must not be null.");
        }
        this.moduleClass = moduleClass;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchLevel = patchLevel;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultModuleInfo)) {
            return false;
        }
        final ModuleInfo defaultModuleInfo = (ModuleInfo)o;
        return this.moduleClass.equals(defaultModuleInfo.getModuleClass());
    }
    
    public String getMajorVersion() {
        return this.majorVersion;
    }
    
    public String getMinorVersion() {
        return this.minorVersion;
    }
    
    public String getModuleClass() {
        return this.moduleClass;
    }
    
    public String getPatchLevel() {
        return this.patchLevel;
    }
    
    public int hashCode() {
        final int result = this.moduleClass.hashCode();
        return result;
    }
    
    public void setMajorVersion(final String majorVersion) {
        this.majorVersion = majorVersion;
    }
    
    public void setMinorVersion(final String minorVersion) {
        this.minorVersion = minorVersion;
    }
    
    public void setModuleClass(final String moduleClass) {
        if (moduleClass == null) {
            throw new NullPointerException();
        }
        this.moduleClass = moduleClass;
    }
    
    public void setPatchLevel(final String patchLevel) {
        this.patchLevel = patchLevel;
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.getClass().getName());
        buffer.append("={ModuleClass=");
        buffer.append(this.getModuleClass());
        if (this.getMajorVersion() != null) {
            buffer.append("; Version=");
            buffer.append(this.getMajorVersion());
            if (this.getMinorVersion() != null) {
                buffer.append("-");
                buffer.append(this.getMinorVersion());
                if (this.getPatchLevel() != null) {
                    buffer.append("_");
                    buffer.append(this.getPatchLevel());
                }
            }
        }
        buffer.append("}");
        return buffer.toString();
    }
}
