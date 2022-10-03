package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class Printer extends GenericJson
{
    @Key
    private List<AuxiliaryMessage> auxiliaryMessages;
    @Key
    private String createTime;
    @Key
    private String description;
    @Key
    private String displayName;
    @Key
    private String id;
    @Key
    private String makeAndModel;
    @Key
    private String name;
    @Key
    private String orgUnitId;
    @Key
    private String uri;
    @Key
    private Boolean useDriverlessConfig;
    
    public List<AuxiliaryMessage> getAuxiliaryMessages() {
        return this.auxiliaryMessages;
    }
    
    public Printer setAuxiliaryMessages(final List<AuxiliaryMessage> auxiliaryMessages) {
        this.auxiliaryMessages = auxiliaryMessages;
        return this;
    }
    
    public String getCreateTime() {
        return this.createTime;
    }
    
    public Printer setCreateTime(final String createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Printer setDescription(final String description) {
        this.description = description;
        return this;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public Printer setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public Printer setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getMakeAndModel() {
        return this.makeAndModel;
    }
    
    public Printer setMakeAndModel(final String makeAndModel) {
        this.makeAndModel = makeAndModel;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Printer setName(final String name) {
        this.name = name;
        return this;
    }
    
    public String getOrgUnitId() {
        return this.orgUnitId;
    }
    
    public Printer setOrgUnitId(final String orgUnitId) {
        this.orgUnitId = orgUnitId;
        return this;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public Printer setUri(final String uri) {
        this.uri = uri;
        return this;
    }
    
    public Boolean getUseDriverlessConfig() {
        return this.useDriverlessConfig;
    }
    
    public Printer setUseDriverlessConfig(final Boolean useDriverlessConfig) {
        this.useDriverlessConfig = useDriverlessConfig;
        return this;
    }
    
    public Printer set(final String fieldName, final Object value) {
        return (Printer)super.set(fieldName, value);
    }
    
    public Printer clone() {
        return (Printer)super.clone();
    }
    
    static {
        Data.nullOf((Class)AuxiliaryMessage.class);
    }
}
