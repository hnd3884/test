package com.azul.crs.shared.models;

import java.util.Objects;
import java.util.HashMap;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VMArtifactChunk extends Payload
{
    private String chunkId;
    private Set<String> artifactIds;
    private Map<String, Object> metadata;
    private Long createTime;
    private transient String location;
    private transient Long size;
    
    public String getChunkId() {
        return this.chunkId;
    }
    
    public Set<String> getArtifactIds() {
        return this.artifactIds;
    }
    
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }
    
    public Long getCreateTime() {
        return this.createTime;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public Long getSize() {
        return this.size;
    }
    
    public void setChunkId(final String chunkId) {
        this.chunkId = chunkId;
    }
    
    public void setArtifactIds(final Set<String> artifactIds) {
        this.artifactIds = artifactIds;
    }
    
    public void setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void setCreateTime(final Long createTime) {
        this.createTime = createTime;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public void setSize(final Long size) {
        this.size = size;
    }
    
    public VMArtifactChunk chunkId(final String artifactId) {
        this.setChunkId(artifactId);
        return this;
    }
    
    public VMArtifactChunk artifactIds(final Set<String> artifactIds) {
        this.setArtifactIds(artifactIds);
        return this;
    }
    
    public VMArtifactChunk metadata(final Map<String, Object> metadata) {
        this.setMetadata(metadata);
        return this;
    }
    
    public VMArtifactChunk createTime(final Long createTime) {
        this.setCreateTime(createTime);
        return this;
    }
    
    public VMArtifactChunk location(final String location) {
        this.setLocation(location);
        return this;
    }
    
    public VMArtifactChunk size(final Long size) {
        this.setSize(size);
        return this;
    }
    
    @JsonIgnore
    public VMArtifactChunk artifactId(final String artifactId) {
        if (this.artifactIds == null) {
            this.artifactIds = new HashSet<String>();
        }
        this.artifactIds.add(artifactId);
        return this;
    }
    
    @JsonIgnore
    public VMArtifactChunk metadata(final String key, final Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<String, Object>();
        }
        this.metadata.put(key, value);
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
        final VMArtifactChunk that = (VMArtifactChunk)o;
        return Objects.equals(this.chunkId, that.chunkId) && Objects.equals(this.artifactIds, that.artifactIds) && Objects.equals(this.metadata, that.metadata) && Objects.equals(this.createTime, that.createTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.chunkId, this.artifactIds, this.metadata, this.createTime);
    }
    
    public VMArtifactChunk copy() {
        return new VMArtifactChunk().chunkId(this.chunkId).metadata(this.metadata).createTime(this.createTime).artifactIds(this.artifactIds);
    }
}
