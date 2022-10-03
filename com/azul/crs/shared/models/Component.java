package com.azul.crs.shared.models;

import java.util.Objects;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Component extends Payload
{
    private String componentId;
    private String componentName;
    private Map<String, Object> metadata;
    private Long registerTime;
    
    public String getComponentId() {
        return this.componentId;
    }
    
    public String getComponentName() {
        return this.componentName;
    }
    
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }
    
    public Long getRegisterTime() {
        return this.registerTime;
    }
    
    public void setComponentId(final String componentId) {
        this.componentId = componentId;
    }
    
    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }
    
    public void setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void setRegisterTime(final Long registerTime) {
        this.registerTime = registerTime;
    }
    
    public Component componentId(final String componentId) {
        this.setComponentId(componentId);
        return this;
    }
    
    public Component name(final String name) {
        this.setComponentName(name);
        return this;
    }
    
    public Component metadata(final Map<String, Object> metadata) {
        this.setMetadata(metadata);
        return this;
    }
    
    public Component registerTime(final Long registerTime) {
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
        final Component component = (Component)o;
        return Objects.equals(this.componentId, component.componentId) && Objects.equals(this.componentName, component.componentName) && Objects.equals(this.metadata, component.metadata) && Objects.equals(this.registerTime, component.registerTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.componentId, this.componentName, this.metadata, this.registerTime);
    }
    
    public Component copy() {
        return new Component().componentId(this.componentId).name(this.componentName).metadata(this.metadata).registerTime(this.registerTime);
    }
}
