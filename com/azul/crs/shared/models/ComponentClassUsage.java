package com.azul.crs.shared.models;

import java.util.Objects;
import java.io.Serializable;

public class ComponentClassUsage extends Payload implements Serializable
{
    private String vmId;
    private String componentClassUsageId;
    private String classHash;
    private String className;
    private Long usageTime;
    private String componentId;
    private String componentName;
    private Integer componentVersion;
    
    public String getVmId() {
        return this.vmId;
    }
    
    public String getComponentClassUsageId() {
        return this.componentClassUsageId;
    }
    
    public String getClassHash() {
        return this.classHash;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public Long getUsageTime() {
        return this.usageTime;
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
    
    public void setVmId(final String vmId) {
        this.vmId = vmId;
    }
    
    public void setComponentClassUsageId(final String componentClassUsageId) {
        this.componentClassUsageId = componentClassUsageId;
    }
    
    public void setClassHash(final String classHash) {
        this.classHash = classHash;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public void setUsageTime(final Long usageTime) {
        this.usageTime = usageTime;
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
    
    public ComponentClassUsage vmId(final String vmId) {
        this.setVmId(vmId);
        return this;
    }
    
    public ComponentClassUsage componentClassUsageId(final String componentClassUsageId) {
        this.setComponentClassUsageId(componentClassUsageId);
        return this;
    }
    
    public ComponentClassUsage classHash(final String classHash) {
        this.setClassHash(classHash);
        return this;
    }
    
    public ComponentClassUsage className(final String className) {
        this.setClassName(className);
        return this;
    }
    
    public ComponentClassUsage usageTime(final Long usageTime) {
        this.setUsageTime(usageTime);
        return this;
    }
    
    public ComponentClassUsage componentId(final String componentId) {
        this.setComponentId(componentId);
        return this;
    }
    
    public ComponentClassUsage componentName(final String componentName) {
        this.setComponentName(componentName);
        return this;
    }
    
    public ComponentClassUsage componentVersion(final Integer componentVersion) {
        this.setComponentVersion(componentVersion);
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
        final ComponentClassUsage that = (ComponentClassUsage)o;
        return Objects.equals(this.vmId, that.vmId) && Objects.equals(this.componentClassUsageId, that.componentClassUsageId) && Objects.equals(this.classHash, that.classHash) && Objects.equals(this.className, that.className) && Objects.equals(this.usageTime, that.usageTime) && Objects.equals(this.componentId, that.componentId) && Objects.equals(this.componentName, that.componentName) && Objects.equals(this.componentVersion, that.componentVersion);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.vmId, this.componentClassUsageId, this.classHash, this.className, this.usageTime, this.componentId, this.componentName, this.componentVersion);
    }
}
