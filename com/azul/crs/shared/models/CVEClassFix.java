package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CVEClassFix
{
    private String className;
    private String cveClassFixId;
    private String classHash;
    private String componentId;
    private String componentName;
    private Integer componentFixVersion;
    private String cveId;
    
    public String getClassName() {
        return this.className;
    }
    
    public String getCveClassFixId() {
        return this.cveClassFixId;
    }
    
    public String getClassHash() {
        return this.classHash;
    }
    
    public String getComponentId() {
        return this.componentId;
    }
    
    public String getComponentName() {
        return this.componentName;
    }
    
    public Integer getComponentFixVersion() {
        return this.componentFixVersion;
    }
    
    public String getCveId() {
        return this.cveId;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public void setCveClassFixId(final String cveClassFixId) {
        this.cveClassFixId = cveClassFixId;
    }
    
    public void setClassHash(final String classHash) {
        this.classHash = classHash;
    }
    
    public void setComponentId(final String componentId) {
        this.componentId = componentId;
    }
    
    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }
    
    public void setComponentFixVersion(final Integer componentFixVersion) {
        this.componentFixVersion = componentFixVersion;
    }
    
    public void setCveId(final String cveId) {
        this.cveId = cveId;
    }
    
    public CVEClassFix className(final String className) {
        this.setClassName(className);
        return this;
    }
    
    public CVEClassFix cveClassFixId(final String cveClassFixId) {
        this.setCveClassFixId(cveClassFixId);
        return this;
    }
    
    public CVEClassFix classHash(final String classHash) {
        this.setClassHash(classHash);
        return this;
    }
    
    public CVEClassFix componentId(final String componentId) {
        this.setComponentId(componentId);
        return this;
    }
    
    public CVEClassFix componentName(final String componentName) {
        this.setComponentName(componentName);
        return this;
    }
    
    public CVEClassFix componentFixVersion(final Integer componentFixVersion) {
        this.setComponentFixVersion(componentFixVersion);
        return this;
    }
    
    public CVEClassFix cveId(final String cveId) {
        this.setCveId(cveId);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final CVEClassFix that = (CVEClassFix)o;
        return Objects.equals(this.className, that.className) && Objects.equals(this.classHash, that.classHash) && Objects.equals(this.componentId, that.componentId) && Objects.equals(this.componentName, that.componentName) && Objects.equals(this.componentFixVersion, that.componentFixVersion) && Objects.equals(this.cveId, that.cveId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.className, this.classHash, this.componentId, this.componentName, this.componentFixVersion, this.cveId);
    }
}
