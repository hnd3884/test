package com.me.mdm.uem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.List;
import java.util.Map;
import com.me.mdm.server.role.RBDAUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class RBDAActionListenerImpl
{
    private Logger logger;
    
    public RBDAActionListenerImpl() {
        this.logger = Logger.getLogger("MDMModernMgmtLogger");
    }
    
    public JSONObject addOrUpdateModernMgmtScope(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final Map techMap = this.convertRBDAJSONObjectToMap((JSONObject)params.get("technicianMap"));
            final Boolean deleteAndAdd = (Boolean)params.get("deleteAndAdd");
            RBDAUtil.getInstance().addOrUpdateModernMgmtScope(techMap, deleteAndAdd);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject removeModernMgmtDevice(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final Map techMap = this.convertRBDAJSONObjectToMap((JSONObject)params.get("technicianMap"));
            RBDAUtil.getInstance().removeModernMgmtDevice(techMap);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject addAllManagedMobileDeviceGrouptoUsers(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final List userList = this.convertRBDAJSONArrayToList((JSONArray)params.get("technicianList"));
            final List techsWithAllDeviceRole = RBDAUtil.getInstance().getAllDeviceAccessTechs(userList);
            RBDAUtil.getInstance().addAllManagedMobileDeviceGrouptoUsers(techsWithAllDeviceRole);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    private Map convertRBDAJSONObjectToMap(JSONObject jsonObject) throws Exception {
        final Map techMap = new HashMap();
        jsonObject = new JSONObject(jsonObject.toString());
        final Iterator itr = jsonObject.keySet().iterator();
        while (itr.hasNext()) {
            final String key = String.valueOf(itr.next());
            if (jsonObject.get(key) instanceof JSONObject) {
                final JSONObject internalJSON = jsonObject.getJSONObject(key);
                final Map internalMap = this.convertRBDAJSONObjectToMap(internalJSON);
                techMap.put(Long.parseLong(key), internalMap);
            }
            else {
                if (!(jsonObject.get(key) instanceof JSONArray)) {
                    continue;
                }
                final JSONArray internalArray = jsonObject.getJSONArray(key);
                final List techList = this.convertRBDAJSONArrayToList(internalArray);
                techMap.put(Long.parseLong(key), techList);
            }
        }
        return techMap;
    }
    
    private List convertRBDAJSONArrayToList(final JSONArray internalArray) throws Exception {
        final List techList = new ArrayList();
        for (int index = 0; index < internalArray.length(); ++index) {
            techList.add(Long.parseLong(String.valueOf(internalArray.get(index))));
        }
        return techList;
    }
}
