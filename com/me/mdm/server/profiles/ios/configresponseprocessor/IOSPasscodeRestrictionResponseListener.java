package com.me.mdm.server.profiles.ios.configresponseprocessor;

import com.adventnet.persistence.Row;
import java.util.Iterator;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.config.MDMConfigUtil;
import java.util.List;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import java.util.ArrayList;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class IOSPasscodeRestrictionResponseListener implements MDMProfileResponseListener
{
    private boolean isNotify;
    
    public IOSPasscodeRestrictionResponseListener() {
        this.isNotify = false;
    }
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        return this.handlePasscodeRestriction(params);
    }
    
    @Override
    public JSONObject failureHandler(final JSONObject params) {
        return this.handlePasscodeRestriction(params);
    }
    
    @Override
    public boolean isNotify(final JSONObject params) {
        return this.isNotify;
    }
    
    private JSONObject handlePasscodeRestriction(final JSONObject params) {
        final JSONObject listenerResponse = new JSONObject();
        try {
            final Long collectionId = JSONUtil.optLongForUVH(params, "collectionId", Long.valueOf(0L));
            final Long resourceId = JSONUtil.optLongForUVH(params, "resourceId", Long.valueOf(0L));
            final JSONArray commandUUIDs = new JSONArray();
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final List<Long> collectionList = new ArrayList<Long>();
            collectionList.add(collectionId);
            final JSONObject previousProfile = new ProfileAssociateDataHandler().getPreVerOfProfileAssociatedForResource(resourceList, collectionList, null);
            final JSONObject resourceObject = previousProfile.optJSONObject(resourceId.toString());
            if (resourceObject != null && resourceObject.length() > 0) {
                final Iterator iterator = resourceObject.keys();
                while (iterator.hasNext()) {
                    final String profileId = iterator.next();
                    final Long prevCollectionId = resourceObject.optLong(profileId);
                    final List configDo = MDMConfigUtil.getConfigurations(prevCollectionId);
                    for (int i = 0; i < configDo.size(); ++i) {
                        final DataObject dataObject = configDo.get(i);
                        final Integer configID = (Integer)dataObject.getFirstValue("ConfigData", "CONFIG_ID");
                        if (configID.equals(172)) {
                            final Row passcodeRow = dataObject.getFirstRow("PasscodePolicy");
                            if (passcodeRow != null) {
                                final boolean restrictPasscode = (boolean)passcodeRow.get("RESTRICT_PASSCODE");
                                if (restrictPasscode) {
                                    commandUUIDs.put((Object)"Restrictions");
                                    commandUUIDs.put((Object)"ProfileList");
                                    this.isNotify = true;
                                }
                            }
                        }
                    }
                }
            }
            final JSONObject commandObject = new JSONObject();
            commandObject.put(String.valueOf(1), (Object)commandUUIDs);
            listenerResponse.put("commandUUIDs", (Object)commandObject);
        }
        catch (final SyMException e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in handling general password restriction", (Throwable)e);
        }
        catch (final DataAccessException e2) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in handling general password restriction", (Throwable)e2);
        }
        catch (final JSONException e3) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in handling general password restriction", (Throwable)e3);
        }
        return listenerResponse;
    }
}
