package com.me.mdm.api.internaltool;

import org.json.JSONObject;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;

public class InternalToolFacade
{
    InternalToolInterface baseInternalToolHandler;
    
    public InternalToolFacade() {
        this.baseInternalToolHandler = MDMRestAPIFactoryProvider.getBaseInternalToolHandler();
    }
    
    public void createDemoUser(final JSONObject jsonObject) throws Exception {
        this.baseInternalToolHandler.createDemoUsers(jsonObject);
    }
    
    public JSONObject refreshQueue(final JSONObject jsonObject) throws Exception {
        return this.baseInternalToolHandler.refreshQueue(jsonObject);
    }
    
    public JSONObject resumeQueue(final JSONObject jsonObject) throws Exception {
        return this.baseInternalToolHandler.resumeQueue(jsonObject);
    }
    
    public JSONObject suspendQueue(final JSONObject jsonObject) throws Exception {
        return this.baseInternalToolHandler.suspendQueue(jsonObject);
    }
    
    public JSONObject simulateDevices(final JSONObject jsonObject) throws Exception {
        return this.baseInternalToolHandler.simulateDevices(jsonObject);
    }
    
    public JSONObject simulateGroups(final JSONObject jsonObject) throws Exception {
        return this.baseInternalToolHandler.simulateGroups(jsonObject);
    }
    
    public JSONObject simulateScanDevices(final JSONObject jsonObject) throws Exception {
        return this.baseInternalToolHandler.simulateScanDevices(jsonObject);
    }
}
