package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MDMConfigStandardEditionNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final LicenseProvider licenseProvider = LicenseProvider.getInstance();
        final String mdmLiceseEditionType;
        final String licenseType = mdmLiceseEditionType = licenseProvider.getMDMLicenseAPI().getMDMLiceseEditionType();
        licenseProvider.getMDMLicenseAPI();
        if (mdmLiceseEditionType.equalsIgnoreCase("Standard")) {
            MDMConfigStandardEditionNotApplicableHandler.LOGGER.log(Level.WARNING, "Config NotApplicable due to license : Following devices {0} are not applicable for this collection {1} due to incapable license", new Object[] { configNotApplicable.resourceList, configNotApplicable.collectionId });
            return configNotApplicable.resourceList;
        }
        return new ArrayList<Long>();
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "mdm.profile.filevault_not_applicable_edition");
        }
        catch (final Exception ex) {
            MDMConfigStandardEditionNotApplicableHandler.LOGGER.log(Level.SEVERE, "Config NotApplicable due to license : Exception in MDMConfigStandardEditionNotApplicableHandler setNotApplicableStatus", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
