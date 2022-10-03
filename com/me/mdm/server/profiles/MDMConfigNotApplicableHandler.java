package com.me.mdm.server.profiles;

import org.apache.commons.collections.MultiHashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.MultiMap;
import java.util.logging.Logger;

public class MDMConfigNotApplicableHandler
{
    private static final Logger LOGGER;
    private static final MultiMap GET_NOT_APPLICABLE_HANDER;
    private static final MultiMap GET_NOT_APPLICABLE_REMOVE_HANDLER;
    private Long collectionID;
    private Long profileId;
    
    public MDMConfigNotApplicableHandler() {
        this.collectionID = null;
        this.profileId = null;
    }
    
    public static MDMConfigNotApplicableHandler getInstance(final Long profileId, final Long collectionID) {
        final MDMConfigNotApplicableHandler listener = new MDMConfigNotApplicableHandler();
        listener.collectionID = collectionID;
        listener.profileId = profileId;
        return listener;
    }
    
    public List<Long> invokeConfigNotApplicationListeners(final List<Integer> configIdList, final List<Long> resourceList, final Long customerId) {
        final List<Long> overAllNotApplicableList = new ArrayList<Long>();
        try {
            if (configIdList != null && !configIdList.isEmpty()) {
                for (final Object configId : configIdList) {
                    try {
                        final List<String> configHandlerList = (List<String>)MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.get(configId);
                        if (configHandlerList == null || configHandlerList.isEmpty()) {
                            continue;
                        }
                        for (final String listner : configHandlerList) {
                            if (resourceList.size() > 0) {
                                final MDMConfigNotApplicableListener notApplicableListner = (MDMConfigNotApplicableListener)Class.forName(listner).newInstance();
                                final MDMConfigNotApplicable configNotApplicable = new MDMConfigNotApplicable(this.collectionID, this.profileId, new ArrayList<Long>(resourceList), customerId);
                                final List notApplicableList = notApplicableListner.getNotApplicableDeviceList(configNotApplicable);
                                notApplicableListner.setNotApplicableStatus(notApplicableList, this.collectionID);
                                resourceList.removeAll(notApplicableList);
                                overAllNotApplicableList.addAll(notApplicableList);
                            }
                        }
                    }
                    catch (final ClassNotFoundException e) {
                        MDMConfigNotApplicableHandler.LOGGER.log(Level.INFO, "No class for invokeConfigNotApplicationListeners :", e);
                    }
                }
            }
        }
        catch (final Exception ex) {
            MDMConfigNotApplicableHandler.LOGGER.log(Level.SEVERE, "Exception in invoker listner", ex);
        }
        return overAllNotApplicableList;
    }
    
    public List<Long> invokeConfigNotApplicableRemoveListeners(final List<Integer> configIdList, final List<Long> resourceList, final List<Long> directRemovalResource) {
        final List<Long> overAllNotApplicableList = new ArrayList<Long>();
        try {
            if (configIdList != null && !configIdList.isEmpty()) {
                for (final Object configId : configIdList) {
                    try {
                        final List<String> configHandlerList = (List<String>)MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_REMOVE_HANDLER.get(configId);
                        if (configHandlerList == null || configHandlerList.isEmpty()) {
                            continue;
                        }
                        for (final String listner : configHandlerList) {
                            if (resourceList.size() > 0) {
                                final MDMConfigNotApplicableListener notApplicableListner = (MDMConfigNotApplicableListener)Class.forName(listner).newInstance();
                                final MDMConfigNotApplicable configNotApplicable = new MDMConfigNotApplicable(this.collectionID, this.profileId, new ArrayList<Long>(resourceList), new ArrayList<Long>(directRemovalResource));
                                final List notApplicableList = notApplicableListner.getNotApplicableDeviceList(configNotApplicable);
                                notApplicableListner.setNotApplicableStatus(notApplicableList, this.collectionID);
                                resourceList.removeAll(notApplicableList);
                                overAllNotApplicableList.addAll(notApplicableList);
                            }
                        }
                    }
                    catch (final ClassNotFoundException e) {
                        MDMConfigNotApplicableHandler.LOGGER.log(Level.INFO, "No class for invokeConfigNotApplicationListeners :", e);
                    }
                }
            }
        }
        catch (final Exception ex) {
            MDMConfigNotApplicableHandler.LOGGER.log(Level.SEVERE, "Exception in invoker listner", ex);
        }
        return overAllNotApplicableList;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
        GET_NOT_APPLICABLE_HANDER = (MultiMap)new MultiHashMap();
        GET_NOT_APPLICABLE_REMOVE_HANDLER = (MultiMap)new MultiHashMap();
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)753, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MDMConfigStandardEditionNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)753, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.NonMacDevicesNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)753, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MacFirmwareOSVersionNotApplicable");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)753, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MacFirmwareNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)753, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MDMConfigNonUEMNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)770, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MDMConfigStandardEditionNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)770, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.NonMacDevicesNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)770, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MacFileVaultProfileOSVersionNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)770, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)770, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MacFileVaultProfileAlreadyExistsNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)770, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MacFileVaultProfileAlreadyDistributedInProgressByMDMNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)770, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MacFileVaultNotApplicableOnNonUserApprovedMDMHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)770, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MDMConfigNonUEMNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)173, (Object)"com.me.mdm.server.profiles.ios.configNotApplicableHandler.IOSCheckAndAddWifiRestriction");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_REMOVE_HANDLER.put((Object)177, (Object)"com.me.mdm.server.profiles.ios.configNotApplicableHandler.IOSRestrictWifiProfileRemovalOnWifiRestrictionApplied");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_REMOVE_HANDLER.put((Object)774, (Object)"com.me.mdm.server.profiles.ios.configNotApplicableHandler.IOSRestrictWifiProfileRemovalOnWifiRestrictionApplied");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_REMOVE_HANDLER.put((Object)753, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MacFirmwareRemovalNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_REMOVE_HANDLER.put((Object)770, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MACFilevaultProfileRemoveHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)613, (Object)"com.me.mdm.server.profiles.windows.configNotApplicableHandler.WindowsBitlockerAlreadyTurnedOnNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)613, (Object)"com.me.mdm.server.profiles.windows.configNotApplicableHandler.WindowsBitlockerPorfileAlreadyExistsNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_REMOVE_HANDLER.put((Object)613, (Object)"com.me.mdm.server.profiles.windows.configNotApplicableHandler.WindowsBitlockerPorfileAlreadyExistsNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)521, (Object)"com.me.mdm.server.profiles.ios.configNotApplicableHandler.IOSPerAppVPNAppNotInstalledHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)528, (Object)"com.me.mdm.server.profiles.ios.configNotApplicableHandler.IOSAppNotificationProfileUnsupervisedDeviceNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)528, (Object)"com.me.mdm.server.profiles.ios.configNotApplicableHandler.IOSAppNotificationProfileAlreadyExistsNotApplicableHandler");
        MDMConfigNotApplicableHandler.GET_NOT_APPLICABLE_HANDER.put((Object)767, (Object)"com.me.mdm.server.profiles.mac.configNotApplicableHandler.MDMConfigNonUEMNotApplicableHandler");
    }
}
