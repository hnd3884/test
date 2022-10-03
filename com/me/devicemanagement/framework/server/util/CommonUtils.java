package com.me.devicemanagement.framework.server.util;

import org.json.JSONArray;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class CommonUtils
{
    public static Logger log;
    
    public static JSONObject createJsonObject(final String jsonAsString) {
        JSONObject jsonObject = null;
        try {
            if (jsonAsString != null) {
                jsonObject = new JSONObject(jsonAsString);
            }
            else {
                jsonObject = new JSONObject("{}");
            }
        }
        catch (final Exception use) {
            CommonUtils.log.log(Level.INFO, "Exception while creating jsonObject");
            use.printStackTrace();
        }
        return jsonObject;
    }
    
    public static Long getUserId() {
        Long user_id = null;
        try {
            user_id = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        }
        catch (final Exception e) {
            CommonUtils.log.log(Level.INFO, "User name not found" + e);
            e.printStackTrace();
        }
        return user_id;
    }
    
    public static JSONArray createJsonArray(final String jsonAsString) {
        JSONArray jsonArray = null;
        try {
            if (jsonAsString != null) {
                jsonArray = new JSONArray(jsonAsString);
            }
        }
        catch (final Exception use) {
            CommonUtils.log.log(Level.INFO, "Exception while creating jsonarray");
            use.printStackTrace();
        }
        return jsonArray;
    }
    
    public static long convertReadableSizeToBytes(final String maxmemory) {
        final String[] array = maxmemory.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        long memoryInBytes = 0L;
        final int size = Integer.parseInt(array[0]);
        final String sizeFormat = array[1];
        if (sizeFormat.equalsIgnoreCase("GB")) {
            memoryInBytes = size * 1073741824L;
        }
        else if (sizeFormat.equalsIgnoreCase("MB")) {
            memoryInBytes = size * 1048576L;
        }
        return memoryInBytes;
    }
    
    static {
        CommonUtils.log = Logger.getLogger(CommonUtils.class.getName());
    }
}
