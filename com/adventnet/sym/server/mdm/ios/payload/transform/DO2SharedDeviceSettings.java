package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.ManagedSettingItem;
import java.util.ArrayList;
import com.dd.plist.NSDictionary;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2SharedDeviceSettings implements DO2Settings
{
    public static final int GUEST_USER = 3;
    public static final int ABM_USER = 2;
    public static final int ALL_USER = 1;
    private static final Logger LOGGER;
    
    @Override
    public List<NSDictionary> createSettingCommand(final DataObject dataObject, final JSONObject params) {
        final List<NSDictionary> settingList = new ArrayList<NSDictionary>();
        try {
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("SharedDeviceConfiguration");
                final int idleTimeout = (int)row.get("IDLE_TIME_LOGOUT");
                final int guestIdleTimeout = (int)row.get("GUEST_IDLE_TIME_LOGOUT");
                final int userAllowed = (int)row.get("USER_ALLOWED");
                final int appAnalytics = (int)row.get("APP_ANALYTICS");
                final int diagnosticsSubmission = (int)row.get("DIAGNOSTICS_SUBMISSION");
                final Integer gracePeriod = (Integer)row.get("MAX_GRACE_PERIOD");
                final ManagedSettingItem sharedSettingItem = new ManagedSettingItem("SharedDeviceConfiguration");
                if (userAllowed == 3 || idleTimeout != 0 || guestIdleTimeout != 0) {
                    if (userAllowed == 3) {
                        sharedSettingItem.setTemporarySession(true);
                        sharedSettingItem.setGuestUserSessionTimeout(idleTimeout);
                    }
                    else if (userAllowed == 2) {
                        sharedSettingItem.setTemporarySession(false);
                        sharedSettingItem.setUserSessionTimeout(idleTimeout);
                    }
                    else {
                        sharedSettingItem.setTemporarySession(false);
                        sharedSettingItem.setGuestUserSessionTimeout(guestIdleTimeout);
                        sharedSettingItem.setUserSessionTimeout(idleTimeout);
                    }
                }
                else {
                    sharedSettingItem.setTemporarySession(false);
                }
                settingList.add(sharedSettingItem.getPayloadDict());
                if (gracePeriod != -1) {
                    final ManagedSettingItem managedSettingItem = new ManagedSettingItem("PasscodeLockGracePeriod");
                    managedSettingItem.setPasscodeGracePeriod(gracePeriod);
                    settingList.add(managedSettingItem.getPayloadDict());
                }
                if (appAnalytics != 0) {
                    final ManagedSettingItem managedSettingItem = new ManagedSettingItem("AppAnalytics");
                    if (appAnalytics == 1) {
                        managedSettingItem.setAppAnalytics(Boolean.TRUE);
                    }
                    else {
                        managedSettingItem.setAppAnalytics(Boolean.FALSE);
                    }
                    settingList.add(managedSettingItem.getPayloadDict());
                }
                if (diagnosticsSubmission != 0) {
                    final ManagedSettingItem managedSettingItem = new ManagedSettingItem("DiagnosticSubmission");
                    if (diagnosticsSubmission == 1) {
                        managedSettingItem.setDiagnosticSubmission(Boolean.TRUE);
                    }
                    else {
                        managedSettingItem.setDiagnosticSubmission(Boolean.FALSE);
                    }
                    settingList.add(managedSettingItem.getPayloadDict());
                }
            }
        }
        catch (final Exception ex) {
            DO2SharedDeviceSettings.LOGGER.log(Level.SEVERE, "Exception in ex", ex);
        }
        return settingList;
    }
    
    public List<NSDictionary> getRemovalSharedRestriction() {
        final List<NSDictionary> settingList = new ArrayList<NSDictionary>();
        final ManagedSettingItem settingItem = new ManagedSettingItem("SharedDeviceConfiguration");
        settingItem.setGuestUserSessionTimeout(0);
        settingItem.setUserSessionTimeout(0);
        settingItem.setTemporarySession(false);
        settingList.add(settingItem.getPayloadDict());
        final ManagedSettingItem passcodeSettingItem = new ManagedSettingItem("PasscodeLockGracePeriod");
        passcodeSettingItem.setPasscodeGracePeriod(0);
        settingList.add(passcodeSettingItem.getPayloadDict());
        final ManagedSettingItem appAnalytics = new ManagedSettingItem("AppAnalytics");
        appAnalytics.setAppAnalytics(false);
        settingList.add(appAnalytics.getPayloadDict());
        final ManagedSettingItem diagnosticSubmission = new ManagedSettingItem("DiagnosticSubmission");
        diagnosticSubmission.setDiagnosticSubmission(true);
        settingList.add(diagnosticSubmission.getPayloadDict());
        return settingList;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
