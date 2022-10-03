package com.me.mdm.server.profiles.ios.configNotApplicableHandler;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.mdm.server.profiles.mac.configNotApplicableHandler.NonMacDevicesNotApplicableHandler;
import com.me.mdm.server.config.MDMCollectionUtil;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMCollectionNotApplicableListener;

public class MacProfileNotApplicableDeviceHandler implements MDMCollectionNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final List resourceList, final Long collectionID, final List configId, final long customerId) {
        try {
            final int profilePlatformType = MDMCollectionUtil.getPlatformType(collectionID);
            if (profilePlatformType == 6) {
                final NonMacDevicesNotApplicableHandler handler = new NonMacDevicesNotApplicableHandler();
                return handler.getNonMacResourceIds(resourceList);
            }
        }
        catch (final Exception e) {
            MacProfileNotApplicableDeviceHandler.LOGGER.log(Level.SEVERE, "Exception in getting restriction applied.", e);
        }
        return new ArrayList<Long>();
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "dc.mdm.devicemgmt.not_supported_profile_platform");
        }
        catch (final DataAccessException e) {
            MacProfileNotApplicableDeviceHandler.LOGGER.log(Level.SEVERE, "Exception in setting the collection Status", (Throwable)e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
