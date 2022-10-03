package com.me.mdm.server.profiles.ios;

import org.json.JSONArray;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSPasscodeRestrictionHandler
{
    public static final long SCHEDULED_RESTRICTION_TIME = 3600000L;
    private static final Logger LOGGER;
    private static final Logger CONFIGLOGGER;
    
    public void handlePasscodeComplianceForDevice(final Long resourceId) {
        final Long collectionId = this.getUnsuccessfulPasscodeRestriction(resourceId);
        if (collectionId != null) {
            IOSPasscodeRestrictionHandler.CONFIGLOGGER.log(Level.INFO, "Going to send the passcode restriction for resource:{0} & collection:{1}", new Object[] { resourceId, collectionId });
            this.addPasscodeRestrictionCommand(resourceId, collectionId);
        }
    }
    
    private Criteria getRestrictCriteria() {
        return new Criteria(new Column("PasscodePolicy", "FORCE_PASSCODE"), (Object)true, 0).and(new Column("PasscodePolicy", "RESTRICT_PASSCODE"), (Object)true, 0);
    }
    
    public void addPasscodeRestrictionCommand(final Long resourceId, final Long collectionId) {
        try {
            final List collectionList = new ArrayList();
            collectionList.add(collectionId);
            final List resourceList = new ArrayList();
            resourceList.add(resourceId);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "RestrictPasscode");
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
            NotificationHandler.getInstance().SendNotification(resourceList, 1);
        }
        catch (final Exception e) {
            IOSPasscodeRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception in add passcode restriction command", e);
        }
    }
    
    public void checkAndAddScheduleSecurityCommand(final Long resourceId) {
        try {
            final Long collectionId = this.getUnsuccessfulPasscodeRestriction(resourceId);
            if (collectionId != null) {
                final List<Long> resourceList = new ArrayList<Long>();
                resourceList.add(resourceId);
                IOSPasscodeRestrictionHandler.CONFIGLOGGER.log(Level.INFO, "Going to add security info command for passcode resource:{0}", new Object[] { resourceId });
                DeviceCommandRepository.getInstance().addSecurityCommand(resourceId, "SecurityInfo");
                NotificationHandler.getInstance().SendNotification(resourceList, 1);
            }
        }
        catch (final Exception e) {
            IOSPasscodeRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception in schedule securityinfo command", e);
        }
    }
    
    private Long getUnsuccessfulPasscodeRestriction(final Long resourceId) {
        Long collectionId = null;
        try {
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final JSONObject restrictionObject = new ProfileAssociateDataHandler().getRestrictionAppliedOnResource(resourceList, "PasscodePolicy", this.getRestrictCriteria());
            final List restrictionAppliedList = (List)restrictionObject.get("RESOURCE_ID");
            if (!restrictionAppliedList.isEmpty()) {
                final JSONObject filterObject = new JSONObject();
                filterObject.put("INSTALLED_SOURCE", 1);
                filterObject.put("PAYLOAD_IDENTIFIER", (Object)"com.mdm.passcode_restriction_install_profile");
                final JSONArray profileDetails = new DeviceConfigPayloadsDataHandler().getInstalledProfilesDetails(resourceId, filterObject);
                final JSONObject deviceRestriction = new JSONObject(InventoryUtil.getInstance().getIOSRestrictionDetails(resourceId).toJSONString());
                final Integer passcodeRestriction = deviceRestriction.getInt("ALLOW_MODIFI_PASSCODE");
                if ((profileDetails != null && profileDetails.length() == 0) || passcodeRestriction == 1) {
                    final JSONArray collectionAttay = restrictionObject.optJSONArray(resourceId.toString());
                    if (collectionAttay != null && collectionAttay.length() > 0) {
                        collectionId = (Long)collectionAttay.get(0);
                    }
                    else {
                        IOSPasscodeRestrictionHandler.CONFIGLOGGER.log(Level.INFO, "Problem:No collection for restrict passcode");
                    }
                }
            }
        }
        catch (final Exception e) {
            IOSPasscodeRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception in unsuccessful passcode restriction", e);
        }
        return collectionId;
    }
    
    public void handleClearPasscodeForPasscodeRestriction(final Long resourceId) {
        try {
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final JSONObject restrictionObject = new ProfileAssociateDataHandler().getRestrictionAppliedOnResource(resourceList, "PasscodePolicy", this.getRestrictCriteria());
            final List restrictionAppliedList = (List)restrictionObject.get("RESOURCE_ID");
            if (!restrictionAppliedList.isEmpty()) {
                final JSONArray collectionAttay = restrictionObject.optJSONArray(resourceId.toString());
                if (collectionAttay != null && collectionAttay.length() > 0) {
                    IOSPasscodeRestrictionHandler.CONFIGLOGGER.log(Level.INFO, "Sending clear passcode restriction for resource:{0}", resourceId);
                    DeviceCommandRepository.getInstance().addSecurityCommand(resourceId, "ClearPasscodeRestriction");
                    NotificationHandler.getInstance().SendNotification(resourceList, 1);
                }
                else {
                    Logger.getLogger("MDMLogger").log(Level.INFO, "Problem:No collection for restrict passcode");
                }
            }
        }
        catch (final Exception e) {
            IOSPasscodeRestrictionHandler.LOGGER.log(Level.SEVERE, "Exception in handle clear passcode for restriction", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        CONFIGLOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
