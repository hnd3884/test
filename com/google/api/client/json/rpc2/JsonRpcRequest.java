package com.google.api.client.json.rpc2;

import com.google.api.client.util.Key;
import com.google.api.client.util.Beta;
import com.google.api.client.util.GenericData;

@Beta
public class JsonRpcRequest extends GenericData
{
    @Key
    private final String jsonrpc = "2.0";
    @Key
    private Object id;
    @Key
    private String method;
    @Key
    private Object params;
    
    public String getVersion() {
        return "2.0";
    }
    
    public Object getId() {
        return this.id;
    }
    
    public void setId(final Object id) {
        this.id = id;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public void setMethod(final String method) {
        this.method = method;
    }
    
    public Object getParameters() {
        return this.params;
    }
    
    public void setParameters(final Object parameters) {
        this.params = parameters;
    }
    
    @Override
    public JsonRpcRequest set(final String fieldName, final Object value) {
        return (JsonRpcRequest)super.set(fieldName, value);
    }
    
    @Override
    public JsonRpcRequest clone() {
        return (JsonRpcRequest)super.clone();
    }
}
