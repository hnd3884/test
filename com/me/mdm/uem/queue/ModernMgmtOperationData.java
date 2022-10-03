package com.me.mdm.uem.queue;

import org.json.JSONObject;

public interface ModernMgmtOperationData
{
    void processData();
    
    JSONObject toJSON();
}
