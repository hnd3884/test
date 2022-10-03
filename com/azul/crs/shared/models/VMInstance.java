package com.azul.crs.shared.models;

import java.util.Collection;
import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = { "effectiveState" }, allowGetters = true)
public class VMInstance extends Payload
{
    private String vmId;
    private String agentVersion;
    private Map<String, Object> inventory;
    private Long startTime;
    private Long lastHeardTime;
    private String owner;
    private State state;
    private State effectiveState;
    private Map<String, Object> annotations;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> tags;
    private String imageId;
    
    public String getVmId() {
        return this.vmId;
    }
    
    public String getAgentVersion() {
        return this.agentVersion;
    }
    
    public Map<String, Object> getInventory() {
        if (this.inventory == null) {
            this.inventory = new HashMap<String, Object>();
        }
        return this.inventory;
    }
    
    public Long getStartTime() {
        return this.startTime;
    }
    
    public Long getLastHeardTime() {
        return this.lastHeardTime;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public State getState() {
        return this.state;
    }
    
    public State getEffectiveState() {
        return this.effectiveState;
    }
    
    public Map<String, Object> getAnnotations() {
        if (this.annotations == null) {
            this.annotations = new HashMap<String, Object>();
        }
        return this.annotations;
    }
    
    public Set<String> getTags() {
        if (this.tags == null) {
            this.tags = new HashSet<String>();
        }
        return this.tags;
    }
    
    public String getImageId() {
        return this.imageId;
    }
    
    public void setVmId(final String vmId) {
        this.vmId = vmId;
    }
    
    public void setAgentVersion(final String agentVersion) {
        this.agentVersion = agentVersion;
    }
    
    public void setInventory(final Map<String, Object> inventory) {
        this.inventory = inventory;
    }
    
    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }
    
    public void setLastHeardTime(final Long lastHeardTime) {
        this.lastHeardTime = lastHeardTime;
    }
    
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    public void setState(final State state) {
        this.state = state;
    }
    
    public void setEffectiveState(final State effectiveState) {
        this.effectiveState = effectiveState;
    }
    
    public void setAnnotations(final Map<String, Object> annotations) {
        this.annotations = annotations;
    }
    
    public void setTags(final Set<String> tags) {
        this.tags = tags;
    }
    
    public void setImageId(final String imageId) {
        this.imageId = imageId;
    }
    
    public VMInstance vmId(final String vmId) {
        this.setVmId(vmId);
        return this;
    }
    
    public VMInstance agentVersion(final String agentVersion) {
        this.setAgentVersion(agentVersion);
        return this;
    }
    
    public VMInstance inventory(final Map<String, Object> inventory) {
        this.setInventory(inventory);
        return this;
    }
    
    public VMInstance startTime(final Long startTime) {
        this.setStartTime(startTime);
        return this;
    }
    
    public VMInstance lastHeardTime(final Long lastHeardTime) {
        this.setLastHeardTime(lastHeardTime);
        return this;
    }
    
    public VMInstance owner(final String owner) {
        this.setOwner(owner);
        return this;
    }
    
    public VMInstance state(final State state) {
        this.setState(state);
        return this;
    }
    
    public VMInstance state(final String state) {
        this.setState(State.valueOf(state));
        return this;
    }
    
    public VMInstance effectiveState(final State effectiveState) {
        this.setEffectiveState(effectiveState);
        return this;
    }
    
    public VMInstance annotations(final Map<String, Object> annotations) {
        this.setAnnotations(annotations);
        return this;
    }
    
    public VMInstance tags(final Set<String> tags) {
        this.setTags(tags);
        return this;
    }
    
    public VMInstance imageId(final String imageId) {
        this.setImageId(imageId);
        return this;
    }
    
    @JsonIgnore
    public VMInstance inventory(final String key, final Object value) {
        if (this.inventory == null) {
            this.inventory = new HashMap<String, Object>();
        }
        this.inventory.put(key, value);
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
        final VMInstance instance = (VMInstance)o;
        return Objects.equals(this.vmId, instance.vmId) && Objects.equals(this.agentVersion, instance.agentVersion) && Objects.equals(this.getInventory(), instance.getInventory()) && Objects.equals(this.startTime, instance.startTime) && Objects.equals(this.lastHeardTime, instance.lastHeardTime) && Objects.equals(this.owner, instance.owner) && this.state == instance.state && Objects.equals(this.getAnnotations(), instance.getAnnotations()) && Objects.equals(this.getTags(), instance.getTags()) && Objects.equals(this.imageId, instance.imageId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.vmId, this.agentVersion, this.getInventory(), this.startTime, this.lastHeardTime, this.owner, this.state, this.getAnnotations(), this.getTags(), this.imageId);
    }
    
    public VMInstance copy() {
        return new VMInstance().vmId(this.vmId).agentVersion(this.agentVersion).inventory(new HashMap<String, Object>(this.getInventory())).startTime(this.startTime).lastHeardTime(this.lastHeardTime).owner(this.owner).state(this.state).annotations(new HashMap<String, Object>(this.getAnnotations())).tags(new HashSet<String>(this.getTags())).imageId(this.imageId);
    }
    
    public enum State
    {
        STARTED, 
        RUNNING, 
        TERMINATED, 
        REGISTERED, 
        OFFLINE;
    }
    
    public enum Annotation
    {
        APPLICATION("com.azul.crs.application"), 
        APPLICATION_COMPONENTS("com.azul.crs.application.components"), 
        CVE_IMPACT("com.azul.crs.cve.impact"), 
        EVENT_ANALYSIS_STATS("com.azul.crs.analysis.statistics");
        
        private final String name;
        
        private Annotation(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
