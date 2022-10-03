package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonCreator;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonProperty;

public class ComponentUsage extends Payload
{
    private final String name;
    private final Integer version;
    private final Integer likehood;
    
    @JsonCreator
    public ComponentUsage(@JsonProperty("name") final String name, @JsonProperty("version") final Integer version, @JsonProperty("likehood") final Integer likehood) {
        this.name = name;
        this.version = version;
        this.likehood = likehood;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Integer getVersion() {
        return this.version;
    }
    
    public Integer getLikehood() {
        return this.likehood;
    }
    
    public Builder copier() {
        return new Builder().name(this.name).version(this.version).likehood(this.likehood);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder builder(final ComponentUsage lib) {
        return new Builder().name(lib.name).version(lib.version).likehood(lib.likehood);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ComponentUsage that = (ComponentUsage)o;
        return Objects.equals(this.name, that.name) && Objects.equals(this.version, that.version) && Objects.equals(this.likehood, that.likehood);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.version, this.likehood);
    }
    
    public static class Builder
    {
        private String name;
        private Integer version;
        private Integer likehood;
        
        public Builder name(final String name) {
            this.name = name;
            return this;
        }
        
        public Builder version(final Integer version) {
            this.version = version;
            return this;
        }
        
        public Builder likehood(final Integer likehood) {
            this.likehood = likehood;
            return this;
        }
        
        public ComponentUsage build() {
            return new ComponentUsage(this.name, this.version, this.likehood);
        }
    }
}
