package com.me.devicemanagement.onpremise.start.metrack.util;

import org.json.JSONObject;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import java.util.logging.Logger;

public abstract class DeviceMgmtEvaluatorUtil
{
    protected static Logger logger;
    
    public Properties getModuleTrackerProperties(final HashMap<String, String> meTrackKeyVsModule) {
        final Properties moduleTrackerProperties = new Properties();
        if (meTrackKeyVsModule != null) {
            for (final Map.Entry<String, String> entry : meTrackKeyVsModule.entrySet()) {
                moduleTrackerProperties.setProperty(entry.getKey(), String.valueOf(this.getJSONFromFileForModule(entry.getValue())));
            }
        }
        return moduleTrackerProperties;
    }
    
    public JSONObject getJSONFromFileForModule(final String moduleName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public HashMap<String, String> getMETrackKeyAndModule() {
        return new HashMap<String, String>();
    }
    
    static {
        DeviceMgmtEvaluatorUtil.logger = Logger.getLogger(DeviceMgmtEvaluatorUtil.class.getName());
    }
}
