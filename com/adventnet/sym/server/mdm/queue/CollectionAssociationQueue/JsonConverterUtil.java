package com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class JsonConverterUtil
{
    private static JsonConverterUtil myUtil;
    private Logger configLogger;
    
    public JsonConverterUtil() {
        this.configLogger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static JsonConverterUtil getInstance() {
        if (JsonConverterUtil.myUtil == null) {
            JsonConverterUtil.myUtil = new JsonConverterUtil();
        }
        return JsonConverterUtil.myUtil;
    }
    
    public void convertObjectToJSON(final CommandQueueObject qObject) {
        try {
            final JSONObject qJSONObject = new JSONObject();
            qJSONObject.put("customerId", (Object)qObject.getCustomerId());
            qJSONObject.put("commandName", (Object)qObject.getCommandName());
            qJSONObject.put("commandType", (Object)qObject.getCommandType());
            final Properties qProps = qObject.getPropsFile();
            final JSONObject internalQData = new JSONObject();
            final Enumeration<?> enums = qProps.propertyNames();
            while (enums.hasMoreElements()) {
                final String propKey = (String)enums.nextElement();
                final Object propObject = ((Hashtable<K, Object>)qProps).get(propKey);
                internalQData.put(propKey, (Object)propObject.toString());
            }
            qJSONObject.put("PropsFile", (Object)internalQData);
            this.configLogger.log(Level.INFO, "Serilaized Queue Data JSON is {0}", qJSONObject.toString());
        }
        catch (final Exception e) {
            this.configLogger.log(Level.SEVERE, "Error while parsing command object to JSON ", e);
        }
    }
    
    public void convertJsonToObject(final String object) {
        try {
            final JSONObject qObject = new JSONObject(object);
            this.configLogger.log(Level.INFO, "De-Serilaized Queue Data JSON is {0}", qObject.toString());
        }
        catch (final Exception e) {
            this.configLogger.log(Level.SEVERE, "Error while parsing JSON to Command Object ", e);
        }
    }
    
    static {
        JsonConverterUtil.myUtil = null;
    }
}
