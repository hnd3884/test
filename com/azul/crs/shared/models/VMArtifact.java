package com.azul.crs.shared.models;

import java.util.Objects;
import java.util.LinkedList;
import java.util.HashMap;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VMArtifact extends Payload
{
    private String artifactId;
    private Type artifactType;
    private Map<String, Object> metadata;
    private String vmId;
    private List<VMArtifactChunk> chunks;
    private Long createTime;
    private transient String snapshot;
    private transient Long size;
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public Type getArtifactType() {
        return this.artifactType;
    }
    
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }
    
    public String getVmId() {
        return this.vmId;
    }
    
    public List<VMArtifactChunk> getChunks() {
        return this.chunks;
    }
    
    public String getSnapshot() {
        return this.snapshot;
    }
    
    public Long getCreateTime() {
        return this.createTime;
    }
    
    public Long getSize() {
        return this.size;
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public void setArtifactType(final Type artifactType) {
        this.artifactType = artifactType;
    }
    
    public void setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void setVmId(final String vmId) {
        this.vmId = vmId;
    }
    
    public void setChunks(final List<VMArtifactChunk> chunks) {
        this.chunks = chunks;
    }
    
    public void setSnapshot(final String snapshot) {
        this.snapshot = snapshot;
    }
    
    public void setCreateTime(final Long createTime) {
        this.createTime = createTime;
    }
    
    public void setSize(final Long size) {
        this.size = size;
    }
    
    public VMArtifact artifactId(final String artifactId) {
        this.setArtifactId(artifactId);
        return this;
    }
    
    public VMArtifact artifactType(final Type artifactType) {
        this.setArtifactType(artifactType);
        return this;
    }
    
    public VMArtifact artifactType(final String artifactType) {
        if (artifactType != null) {
            this.setArtifactType(Type.valueOf(artifactType));
        }
        return this;
    }
    
    public VMArtifact metadata(final Map<String, Object> metadata) {
        this.setMetadata(metadata);
        return this;
    }
    
    public VMArtifact vmId(final String vmId) {
        this.setVmId(vmId);
        return this;
    }
    
    public VMArtifact snapshot(final String snapshot) {
        this.setSnapshot(snapshot);
        return this;
    }
    
    public VMArtifact createTime(final Long createTime) {
        this.setCreateTime(createTime);
        return this;
    }
    
    public VMArtifact chunks(final List<VMArtifactChunk> chunks) {
        this.setChunks(chunks);
        return this;
    }
    
    public VMArtifact size(final Long size) {
        this.setSize(size);
        return this;
    }
    
    @JsonIgnore
    public VMArtifactChunk getLastChunk() {
        return (this.chunks != null && !this.chunks.isEmpty()) ? this.chunks.get(this.chunks.size() - 1) : null;
    }
    
    @JsonIgnore
    public VMArtifactChunk getChunk(final String chunkId) {
        return this.chunks.stream().filter(c -> c.getChunkId().equals(chunkId)).findFirst().orElse(null);
    }
    
    @JsonIgnore
    public VMArtifact metadata(final String key, final Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<String, Object>();
        }
        this.metadata.put(key, value);
        return this;
    }
    
    @JsonIgnore
    public VMArtifact chunk(final VMArtifactChunk chunk) {
        if (this.chunks == null) {
            this.chunks = new LinkedList<VMArtifactChunk>();
        }
        this.chunks.add(chunk);
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
        final VMArtifact that = (VMArtifact)o;
        return Objects.equals(this.artifactId, that.artifactId) && this.artifactType == that.artifactType && Objects.equals(this.metadata, that.metadata) && Objects.equals(this.vmId, that.vmId) && Objects.equals(this.chunks, that.chunks) && Objects.equals(this.createTime, that.createTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.artifactId, this.artifactType, this.metadata, this.vmId, this.chunks, this.createTime);
    }
    
    public VMArtifact copy() {
        return new VMArtifact().artifactId(this.artifactId).artifactType(this.artifactType).metadata(this.metadata).vmId(this.vmId).chunks(this.chunks).createTime(this.createTime);
    }
    
    public enum Type
    {
        GC_LOG, 
        JFR;
    }
}
