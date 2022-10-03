package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.inv.ProcessorType;
import java.util.Map;
import com.me.mdm.server.security.mac.MacFirmwareConfigHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MacFirmwareRemovalNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        Map<Long, ProcessorType> macProcessorMap = null;
        try {
            macProcessorMap = MDMUtil.getMacProcessorType(configNotApplicable.resourceList);
        }
        catch (final Exception e) {
            MacFirmwareRemovalNotApplicableHandler.LOGGER.log(Level.SEVERE, e, () -> "Exception while getting processor type for resources: " + mdmConfigNotApplicable.resourceList);
            this.updateFatalFailureStatusForResources(configNotApplicable.resourceList, configNotApplicable.collectionId);
            return configNotApplicable.resourceList;
        }
        final List<Long> unknownProcessorMacs = MDMUtil.filterUnknownProcessorMacs(macProcessorMap);
        final List<Long> applicableResources = new ArrayList<Long>(configNotApplicable.resourceList);
        applicableResources.removeAll(unknownProcessorMacs);
        if (!unknownProcessorMacs.isEmpty()) {
            this.updateUnknownProcessorStatusForResources(unknownProcessorMacs, configNotApplicable.collectionId);
        }
        if (!applicableResources.isEmpty()) {
            MacFirmwareRemovalNotApplicableHandler.LOGGER.log(Level.INFO, "Firmware/Recovery Lock: removing profile for following devices: {0}", new Object[] { applicableResources });
            MacFirmwareConfigHandler.processFirmwareProfileDisAssociationToDevices(applicableResources, configNotApplicable.collectionId);
        }
        return unknownProcessorMacs;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collectionId) {
    }
    
    private void updateUnknownProcessorStatusForResources(final List<Long> resourceIDList, final Long collectionId) {
        try {
            MacFirmwareRemovalNotApplicableHandler.LOGGER.log(Level.INFO, "Updating unknown processor status for collection: {0}, resources: {1}", new Object[] { collectionId, resourceIDList });
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collectionId, 7, "mdm.mac.recovery.unknown_processor_disassociate");
        }
        catch (final DataAccessException e) {
            final String exMessage = "Exception while marking unknown processor type for collection: " + collectionId + ", resources: " + resourceIDList;
            MacFirmwareRemovalNotApplicableHandler.LOGGER.log(Level.SEVERE, exMessage, (Throwable)e);
        }
    }
    
    private void updateFatalFailureStatusForResources(final List<Long> resourceIDList, final Long collectionId) {
        try {
            MacFirmwareRemovalNotApplicableHandler.LOGGER.log(Level.INFO, "Updating fatal failure status for collection: {0}, resources: {1}", new Object[] { collectionId, resourceIDList });
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collectionId, 7, "mdm.mac.recovery.unknown_error");
        }
        catch (final DataAccessException e) {
            MacFirmwareRemovalNotApplicableHandler.LOGGER.log(Level.SEVERE, (Throwable)e, () -> "Exception while updating fatal failure reason during rec lock/ firmware association for resources: " + list);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
