package com.me.devicemanagement.framework.server.api;

import java.util.List;
import org.json.JSONObject;

public interface MDMSupportAPI
{
    boolean uploadAgentLogs(final JSONObject p0, final int p1) throws Exception;
    
    boolean isMDMDevicesSelectedValid(final List p0, final Long p1, final Long p2) throws Exception;
}
