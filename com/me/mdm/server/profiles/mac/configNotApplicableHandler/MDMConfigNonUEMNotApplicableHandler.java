package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MDMConfigNonUEMNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final String productType = ProductUrlLoader.getInstance().getValue("productcode");
        final Boolean isDCEE = productType.equals("DCEE");
        final boolean isEndpointServiceEnabled = LicenseProvider.getInstance().isEndpointServiceEnabled();
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final boolean isTrialCustomer = licenseType.equalsIgnoreCase("T");
        final boolean isFreeCustomer = licenseType.equalsIgnoreCase("F");
        final boolean isUemFeatureAllowed = isEndpointServiceEnabled || isTrialCustomer || isFreeCustomer;
        if (isDCEE && !isUemFeatureAllowed) {
            MDMConfigNonUEMNotApplicableHandler.LOGGER.log(Level.WARNING, "Config NotApplicable due to license : Following devices " + configNotApplicable.resourceList + " are not applicable for this collection " + configNotApplicable.collectionId + " due to incapable license");
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
            MDMConfigNonUEMNotApplicableHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in  MDMConfigNonUEMNotApplicableHandler setNotApplicableStatus():", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
