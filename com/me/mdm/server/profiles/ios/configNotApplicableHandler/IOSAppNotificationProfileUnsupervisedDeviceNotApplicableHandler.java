package com.me.mdm.server.profiles.ios.configNotApplicableHandler;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import java.util.ArrayList;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class IOSAppNotificationProfileUnsupervisedDeviceNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final List<Long> resourceList = configNotApplicable.resourceList;
        final List<Long> notSupervisedDevices = new ArrayList<Long>();
        for (final Long resourceId : resourceList) {
            if (!InventoryUtil.getInstance().isSupervisedDevice(resourceId)) {
                notSupervisedDevices.add(resourceId);
            }
        }
        if (notSupervisedDevices.size() > 0) {
            IOSAppNotificationProfileUnsupervisedDeviceNotApplicableHandler.LOGGER.log(Level.INFO, "IOSAppNotificationPolicyNAHandlerLog: Unsupervised resources are not applicable : " + notSupervisedDevices);
        }
        return notSupervisedDevices;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 7, "mdm.profile.ios.supervised.only");
        }
        catch (final Exception ex) {
            IOSAppNotificationProfileUnsupervisedDeviceNotApplicableHandler.LOGGER.log(Level.SEVERE, "NotApplicableHander: Exception in  IOSAppNotificationProfileNotApplicableDeviceHandler:", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
