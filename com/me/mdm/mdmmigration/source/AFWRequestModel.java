package com.me.mdm.mdmmigration.source;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.paging.model.Pagination;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AFWRequestModel extends Pagination
{
    @JsonProperty("topic")
    private String topic;
    @JsonProperty("key")
    private String key;
    @JsonProperty("tag")
    private String tag;
    @JsonProperty("data")
    private String data;
    @JsonProperty("code")
    private String code;
    
    public String getTag() {
        return this.tag;
    }
    
    public void setTag(final String tag) {
        this.tag = tag;
    }
    
    public String getData() {
        return this.data;
    }
    
    public void setData(final String data) {
        this.data = data;
    }
    
    public String getTopic() {
        return this.topic;
    }
    
    public void setTopic(final String topic) {
        this.topic = topic;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(final String code) {
        this.code = code;
    }
}
