package com.me.mdm.core.lockdown.data;

import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.persistence.Row;

public class LockdownApplication implements Comparable
{
    public static final Integer MODERN_APP_TYPE;
    public static final Integer LEGACY_APP_TYPE;
    public Long appID;
    public String identifier;
    public Integer appType;
    
    public LockdownApplication() {
    }
    
    public LockdownApplication(final Row row) {
        this.appID = (Long)row.get("APP_ID");
        this.appType = (Integer)row.get("APP_TYPE");
        this.identifier = (String)row.get("APP_IDENTIFIER");
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("identifier", (Object)this.identifier);
        jsonObject.put("app_type", (Object)this.appType);
        return jsonObject;
    }
    
    @Override
    public int compareTo(final Object o) {
        return this.identifier.compareTo(((LockdownApplication)o).identifier);
    }
    
    static {
        MODERN_APP_TYPE = 1;
        LEGACY_APP_TYPE = 2;
    }
}
