package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComponentClass extends Payload
{
    private String className;
    private String componentClassId;
    private String classHash;
    private String componentId;
    private String componentName;
    private Integer componentVersion;
    private Long registerTime;
    
    public String getClassName() {
        return this.className;
    }
    
    public String getComponentClassId() {
        return this.componentClassId;
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
    
    public Integer getComponentVersion() {
        return this.componentVersion;
    }
    
    public Long getRegisterTime() {
        return this.registerTime;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public void setComponentClassId(final String componentClassId) {
        this.componentClassId = componentClassId;
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
    
    public void setComponentVersion(final Integer componentVersion) {
        this.componentVersion = componentVersion;
    }
    
    public void setRegisterTime(final Long registerTime) {
        this.registerTime = registerTime;
    }
    
    public ComponentClass className(final String className) {
        this.setClassName(className);
        return this;
    }
    
    public ComponentClass componentClassId(final String componentClassId) {
        this.setComponentClassId(componentClassId);
        return this;
    }
    
    public ComponentClass classHash(final String classHash) {
        this.setClassHash(classHash);
        return this;
    }
    
    public ComponentClass componentId(final String componentId) {
        this.setComponentId(componentId);
        return this;
    }
    
    public ComponentClass componentName(final String componentName) {
        this.setComponentName(componentName);
        return this;
    }
    
    public ComponentClass componentVersion(final Integer componentVersion) {
        this.setComponentVersion(componentVersion);
        return this;
    }
    
    public ComponentClass registerTime(final Long registerTime) {
        this.setRegisterTime(registerTime);
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
        final ComponentClass that = (ComponentClass)o;
        return Objects.equals(this.className, that.className) && Objects.equals(this.componentClassId, that.componentClassId) && Objects.equals(this.classHash, that.classHash) && Objects.equals(this.componentId, that.componentId) && Objects.equals(this.componentName, that.componentName) && Objects.equals(this.componentVersion, that.componentVersion) && Objects.equals(this.registerTime, that.registerTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.className, this.componentClassId, this.classHash, this.componentId, this.componentName, this.componentVersion, this.registerTime);
    }
}
