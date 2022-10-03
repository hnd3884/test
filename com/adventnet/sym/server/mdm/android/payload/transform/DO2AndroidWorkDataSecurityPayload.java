package com.adventnet.sym.server.mdm.android.payload.transform;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidWorkDataSecurityPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidWorkDataSecurityPayload implements DO2AndroidPayload
{
    public Logger logger;
    static final int ENABLE = 1;
    static final int NOT_CONFIGURED = -1;
    static final int USER_CONTROLLED = 4;
    
    public DO2AndroidWorkDataSecurityPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        Iterator iterator = null;
        AndroidWorkDataSecurityPayload payload = null;
        try {
            if (dataObject != null) {
                iterator = dataObject.getRows("WorkDataSecurityPolicy");
                while (iterator.hasNext()) {
                    payload = new AndroidWorkDataSecurityPayload("1.0", "com.mdm.mobiledevice.workdatasecuritypolicy", "Work Data Security Policy");
                    final Row payloadRow = iterator.next();
                    final boolean allowShareDocToPersonalApps = (boolean)payloadRow.get("ALLOW_SHARE_DOC_TO_PERSONAL_APPS");
                    final boolean allowShareDocToWorkProfile = (boolean)payloadRow.get("ALLOW_SHARE_DOC_TO_WORK_PROFILE");
                    final boolean allowProfileContentToOtherApps = (boolean)payloadRow.get("ALLOW_PROFILE_CONTENT_TO_OTHER_APPS");
                    final boolean allowShareWorkProfileContactOverBluetooth = (boolean)payloadRow.get("ALLOW_PROFILE_CONTACT_OVER_BLUETOOTH");
                    final boolean allowWorkProfileAppWidgetsToHomeScreen = (boolean)payloadRow.get("ALLOW_PROFILE_APP_WIDGETS_TO_HOME_SCREEN");
                    final Boolean allowWorkContactDetailsInPersonalProfile = (Boolean)payloadRow.get("ALLOW_CONTACT_IN_PERSONAL_PROFILE");
                    final boolean allowWorkContactAccessToPersonalApps = (boolean)payloadRow.get("ALLOW_CONTACT_ACCESS_TO_PERSONAL_APPS");
                    final Integer allowConnectedApps = (Integer)payloadRow.get("ALLOW_CONNECTED_APPS");
                    final List connectedApps = this.getAppIdentifierForPayload(dataObject);
                    payload.setAllowShareDocToPersonalApps(allowShareDocToPersonalApps);
                    payload.setAllowShareDocToWorkProfile(allowShareDocToWorkProfile);
                    payload.setAllowWorkProfileContentToOtherApps(allowProfileContentToOtherApps);
                    payload.setAllowShareWorkProfileContactOverBluetooth(allowShareWorkProfileContactOverBluetooth);
                    payload.setAllowWorkProfileAppWidgetToHomeScreen(allowWorkProfileAppWidgetsToHomeScreen);
                    payload.setAllowWorkContactDetailsInPersonalProfile(allowWorkContactDetailsInPersonalProfile);
                    payload.setAllowWorkContactAccessToPersonalApps(allowWorkContactAccessToPersonalApps);
                    payload.setAllowConnectedApps(allowConnectedApps);
                    payload.setConnectedApps(connectedApps);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "failed to save android work data security policy payload", exp);
        }
        return payload;
    }
    
    private List<String> getAppIdentifierForPayload(final DataObject dataObject) throws DataAccessException {
        final Iterator itr = dataObject.getRows("MdAppGroupDetails");
        final List appGroupIds = MDMDBUtil.getColumnValuesAsList(itr, "IDENTIFIER");
        return appGroupIds;
    }
}
