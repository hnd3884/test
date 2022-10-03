package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MacFirmwareOSVersionNotApplicable implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final List<Long> naList = new MacFileVaultProfileOSVersionNotApplicableHandler().getResourcesWithLessthanGeraterThanGivenVersion(configNotApplicable.resourceList, 10.13f, false);
        MacFirmwareOSVersionNotApplicable.LOGGER.log(Level.INFO, "FileVaultLog: Not applicable mac OS version :{0}", naList);
        return naList;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "mdm.profile.firmware_os_not_compatable");
        }
        catch (final Exception ex) {
            MacFirmwareOSVersionNotApplicable.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in MacFileVaultProfileOSVersionNotApplicableHandler setNotApplicableStatus : ", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
