package com.me.mdm.server.profiles.ios.configNotApplicableHandler;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class IOSAppNotificationProfileAlreadyExistsNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final List<Long> resourceList = configNotApplicable.resourceList;
        final int configId = 528;
        try {
            final List<Long> configAppliedResourceIds = new ProfileAssociateDataHandler().getConfigAppliedForResources(resourceList, configId);
            if (configAppliedResourceIds.size() > 0) {
                IOSAppNotificationProfileAlreadyExistsNotApplicableHandler.LOGGER.log(Level.INFO, "IOSAppNotificationPolicyNAHandlerLog: App notification policy already exists in these devices : " + configAppliedResourceIds);
                return configAppliedResourceIds;
            }
            return new ArrayList<Long>();
        }
        catch (final Exception ex) {
            IOSAppNotificationProfileAlreadyExistsNotApplicableHandler.LOGGER.log(Level.SEVERE, "NotApplicableHander: Exception in  IOSAppNotificationProfileNotApplicableDeviceHandler:", ex);
            return null;
        }
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 7, "mdm.profile.ios.appnotificationpolicy.not_applicable");
        }
        catch (final Exception ex) {
            IOSAppNotificationProfileAlreadyExistsNotApplicableHandler.LOGGER.log(Level.SEVERE, "NotApplicableHander: Exception in  IOSAppNotificationProfileNotApplicableDeviceHandler:", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
