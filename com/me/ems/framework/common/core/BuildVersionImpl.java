package com.me.ems.framework.common.core;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import java.util.logging.Logger;

public class BuildVersionImpl implements BuildVersionAPI
{
    protected static Logger logger;
    protected Properties productProperties;
    
    public BuildVersionImpl() {
        this.productProperties = SyMUtil.getProductProperties();
    }
    
    @Override
    public Map<String, Object> getBuildVersionDetails() throws Exception {
        final Map<String, Object> buildVersionMap = new HashMap<String, Object>(4);
        final Map<String, Object> componentVersionsMap = new HashMap<String, Object>(5);
        componentVersionsMap.put("serverVersion", ((Hashtable<K, Object>)this.productProperties).get("productversion"));
        buildVersionMap.put("versionsMap", componentVersionsMap);
        buildVersionMap.put("buildNumber", ((Hashtable<K, Object>)this.productProperties).get("buildnumber"));
        return buildVersionMap;
    }
    
    static {
        BuildVersionImpl.logger = Logger.getLogger(BuildVersionAPI.class.getName());
    }
}
