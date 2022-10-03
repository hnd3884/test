package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class About extends Payload
{
    private String name;
    private String version;
    private String description;
    private Map<String, Object> details;
    
    public String getName() {
        return this.name;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Map<String, Object> getDetails() {
        return this.details;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public void setDetails(final Map<String, Object> details) {
        this.details = details;
    }
    
    public About name(final String name) {
        this.setName(name);
        return this;
    }
    
    public About version(final String version) {
        this.setVersion(version);
        return this;
    }
    
    public About description(final String description) {
        this.setDescription(description);
        return this;
    }
    
    public About details(final Map<String, Object> details) {
        this.setDetails(details);
        return this;
    }
    
    @JsonIgnore
    public About details(final String key, final Object value) {
        if (this.details == null) {
            this.details = new HashMap<String, Object>();
        }
        this.details.put(key, value);
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
        final About about = (About)o;
        return Objects.equals(this.name, about.name) && Objects.equals(this.version, about.version) && Objects.equals(this.description, about.description) && Objects.equals(this.details, about.details);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.version, this.description, this.details);
    }
}
