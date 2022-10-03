package com.me.mdm.core.auth;

import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;

public class APIKey
{
    protected final String keyName;
    protected final String keyValue;
    protected final int version;
    public static int VERSION_1_0;
    public static int VERSION_2_0;
    
    public APIKey(final String keyName, final String keyValue, final int version) {
        this.keyName = keyName;
        this.keyValue = keyValue;
        this.version = version;
    }
    
    public String getKeyName() {
        return this.keyName;
    }
    
    public String getKeyValue() {
        return this.keyValue;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public String getAsURLParams() {
        if (MDMStringUtils.isEmpty(this.keyName)) {
            return "";
        }
        return this.keyName + "=" + this.keyValue;
    }
    
    public String appendAsURLParams(String url) {
        final String appendString = this.getAsURLParams();
        if (appendString != null && !appendString.isEmpty()) {
            url = url + (url.contains("?") ? "&" : "?") + appendString;
        }
        return url;
    }
    
    public Map toMap() {
        final Map map = new HashMap();
        if (!MDMStringUtils.isEmpty(this.keyName)) {
            map.put(this.keyName, this.keyValue);
        }
        return map;
    }
    
    public JSONObject toClientJSON() {
        final JSONObject map = new JSONObject();
        if (!MDMStringUtils.isEmpty(this.keyName)) {
            try {
                map.put("TokenName", (Object)this.keyName);
                map.put("TokenValue", (Object)this.keyValue);
            }
            catch (final JSONException ex) {
                Logger.getLogger(APIKey.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return map;
    }
    
    static {
        APIKey.VERSION_1_0 = 1;
        APIKey.VERSION_2_0 = 2;
    }
}
