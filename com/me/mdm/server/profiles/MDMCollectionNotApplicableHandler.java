package com.me.mdm.server.profiles;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MDMCollectionNotApplicableHandler
{
    private static final Logger LOGGER;
    private Long collectionId;
    private static final List<String> GENERAL_NOT_APPLICABLE_LIST;
    private static final List<String> GENERAL_REMOVE_NOT_APPLICABLE_LIST;
    
    public MDMCollectionNotApplicableHandler(final Long collectionId) {
        this.collectionId = null;
        this.collectionId = collectionId;
    }
    
    public List<Long> invokeCollectionNotApplicableListener(final List configIds, final List<Long> resourceList, final long customerId) {
        final List<Long> overallNotApplicable = new ArrayList<Long>();
        try {
            for (final String className : MDMCollectionNotApplicableHandler.GENERAL_NOT_APPLICABLE_LIST) {
                final MDMCollectionNotApplicableListener mdmCollectionNotApplicableListener = (MDMCollectionNotApplicableListener)Class.forName(className).newInstance();
                final List naList = mdmCollectionNotApplicableListener.getNotApplicableDeviceList(resourceList, this.collectionId, configIds, customerId);
                mdmCollectionNotApplicableListener.setNotApplicableStatus(naList, this.collectionId);
                resourceList.removeAll(naList);
                overallNotApplicable.addAll(naList);
            }
        }
        catch (final ClassNotFoundException e) {
            MDMCollectionNotApplicableHandler.LOGGER.log(Level.INFO, "Class not found for collection applicable");
        }
        catch (final Exception e2) {
            MDMCollectionNotApplicableHandler.LOGGER.log(Level.SEVERE, "Exception in collection not applicable listener", e2);
        }
        return overallNotApplicable;
    }
    
    public List<Long> invokeCollectionNotApplicableRemoveListener(final List configIds, final List<Long> resourceList, final long customerId) {
        final List<Long> overallNotApplicable = new ArrayList<Long>();
        try {
            for (final String className : MDMCollectionNotApplicableHandler.GENERAL_REMOVE_NOT_APPLICABLE_LIST) {
                final MDMCollectionNotApplicableListener mdmCollectionNotApplicableHandler = (MDMCollectionNotApplicableListener)Class.forName(className).newInstance();
                final List naList = mdmCollectionNotApplicableHandler.getNotApplicableDeviceList(resourceList, this.collectionId, configIds, customerId);
                mdmCollectionNotApplicableHandler.setNotApplicableStatus(naList, this.collectionId);
                resourceList.removeAll(naList);
                overallNotApplicable.addAll(naList);
            }
        }
        catch (final ClassNotFoundException e) {
            MDMCollectionNotApplicableHandler.LOGGER.log(Level.INFO, "Class not found for remove collection applicable");
        }
        catch (final Exception e2) {
            MDMCollectionNotApplicableHandler.LOGGER.log(Level.SEVERE, "Exception in remove collection not applicable listener", e2);
        }
        return overallNotApplicable;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
        GENERAL_NOT_APPLICABLE_LIST = new ArrayList<String>() {
            {
                this.add("com.me.mdm.server.profiles.ios.configNotApplicableHandler.IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied");
                this.add("com.me.mdm.server.profiles.ios.configNotApplicableHandler.MacProfileNotApplicableDeviceHandler");
                this.add("com.me.mdm.server.profiles.ios.configNotApplicableHandler.TvOSProfileNotApplicableDeviceHandler");
                this.add("com.adventnet.sym.server.mdm.certificates.scep.DynamicScepNotApplicableHandler");
            }
        };
        GENERAL_REMOVE_NOT_APPLICABLE_LIST = new ArrayList<String>() {
            {
                this.add("com.me.mdm.server.profiles.ios.configNotApplicableHandler.IOSRestrictWifiProfileUpdateRemovalOnWifiRestrictionApplied");
                this.add("com.me.mdm.server.profiles.ios.configNotApplicableHandler.MacProfileNotApplicableDeviceHandler");
                this.add("com.me.mdm.server.profiles.ios.configNotApplicableHandler.TvOSProfileNotApplicableDeviceHandler");
            }
        };
    }
}
