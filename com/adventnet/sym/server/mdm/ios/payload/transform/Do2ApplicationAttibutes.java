package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.ManagedSettingItem;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.dd.plist.NSDictionary;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;

public class Do2ApplicationAttibutes implements DO2Settings
{
    @Override
    public List<NSDictionary> createSettingCommand(final DataObject dataObject, final JSONObject params) {
        final ArrayList<String> appListForVPN = new ArrayList<String>();
        final List<NSDictionary> applicationPolicyList = new ArrayList<NSDictionary>();
        try {
            final Iterator iterator = dataObject.getRows("VpnPolicy");
            while (iterator.hasNext()) {
                final Row policyRow = iterator.next();
                final Long configDataItemID = (Long)policyRow.get("CONFIG_DATA_ITEM_ID");
                final String uuid = (String)policyRow.get("VPNUUID");
                final Iterator<Row> appRows = DO2AppLockPayload.getPolicyAppsRows(dataObject, configDataItemID);
                while (appRows.hasNext()) {
                    final Row appGrpRow = appRows.next();
                    String identifier = (String)appGrpRow.get("IDENTIFIER");
                    if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
                        identifier = IOSModifiedEnterpriseAppsUtil.getOriginalBundleIDOfEnterpriseApp(identifier);
                    }
                    if (!appListForVPN.contains(identifier)) {
                        appListForVPN.add(identifier);
                    }
                }
                for (final String appIdentifer : appListForVPN) {
                    final ManagedSettingItem settingsPayload = new ManagedSettingItem("ApplicationAttributes");
                    settingsPayload.setIdentifier(appIdentifer);
                    settingsPayload.setAttibutes(uuid);
                    applicationPolicyList.add(settingsPayload.getPayloadDict());
                }
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(Do2ApplicationAttibutes.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return applicationPolicyList;
    }
}
