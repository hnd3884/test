package com.adventnet.sym.server.mdm.queue.commonqueue;

import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import java.util.Map;
import org.json.JSONObject;

public class CommonQueueData
{
    private Long customerId;
    private String taskName;
    private String className;
    private JSONObject jsonQueueData;
    
    public CommonQueueData() {
    }
    
    public CommonQueueData(final Map<?, ?> mapData) {
        if (mapData.containsKey("CLASS_NAME") && mapData.containsKey("COMMAND_NAME")) {
            this.taskName = (String)mapData.get("COMMAND_NAME");
            this.className = (String)mapData.get("CLASS_NAME");
            if (mapData.containsKey("CUSTOMER_ID")) {
                this.customerId = (Long)mapData.get("CUSTOMER_ID");
            }
        }
    }
    
    public CommonQueueData(final Map<?, ?> mapData, final String jsonData) throws JSONException {
        if (mapData.containsKey("CLASS_NAME") && mapData.containsKey("COMMAND_NAME")) {
            this.taskName = (String)mapData.get("COMMAND_NAME");
            this.className = (String)mapData.get("CLASS_NAME");
            if (mapData.containsKey("CUSTOMER_ID")) {
                this.customerId = (Long)mapData.get("CUSTOMER_ID");
            }
        }
        this.jsonQueueData = new JSONObject(jsonData);
    }
    
    public Long getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }
    
    public String getTaskName() {
        return this.taskName;
    }
    
    public void setTaskName(final String taskName) {
        this.taskName = taskName;
    }
    
    public JSONObject getJsonQueueData() {
        return this.jsonQueueData;
    }
    
    public void setJsonQueueData(final JSONObject jsonQueueData) {
        this.jsonQueueData = jsonQueueData;
    }
    
    public void setEmptyJsonQueueData() {
        this.jsonQueueData = new JSONObject();
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public HashMap<String, String> getHashMap() {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("CLASS_NAME", this.className);
        hashMap.put("COMMAND_NAME", this.taskName);
        final ArrayList<Long> customerId = new ArrayList<Long>();
        customerId.add(this.customerId);
        hashMap.put("CUSTOMER_ID", JSONUtil.getInstance().convertLongToString(customerId).get(0));
        return hashMap;
    }
}
