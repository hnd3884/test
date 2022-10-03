package com.me.mdm.server.license;

import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.license.FreeEditionHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.uem.actionconstants.LicenseAction;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.license.MDMLicenseAPI;

public class MDMLicenseImpl implements MDMLicenseAPI
{
    public String getNoOfMobileDevicesManaged() {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable()) {
            final JSONObject licenseDetails = MDMApiFactoryProvider.getMDMModernMgmtAPI().licenseListener(LicenseAction.GET_LICENSE_DETAILS);
            final String noOfMobileDevices = (licenseDetails != null) ? licenseDetails.optString("allowedCount", "0") : "0";
            return noOfMobileDevices;
        }
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType != null && licenseType.equalsIgnoreCase("F")) {
            return String.valueOf(FreeEditionHandler.getInstance().getFreeEditionMobileDevices());
        }
        if (licenseType != null && licenseType.equalsIgnoreCase("T")) {
            return "unlimited";
        }
        Properties MDMProps = null;
        final String propertyKey = "NumberOfMobileDevices";
        MDMProps = LicenseProvider.getInstance().getModuleProperties("MobileDevices");
        if (MDMProps == null) {
            return String.valueOf(FreeEditionHandler.getInstance().getFreeEditionMobileDevices());
        }
        final String numberOfMobileDevices = MDMProps.getProperty(propertyKey);
        if (numberOfMobileDevices.trim().length() == 0) {
            return String.valueOf(FreeEditionHandler.getInstance().getFreeEditionMobileDevices());
        }
        if (numberOfMobileDevices.equalsIgnoreCase("unlimited")) {
            return "unlimited";
        }
        if (!numberOfMobileDevices.equalsIgnoreCase("unlimited")) {
            final int mobileDeviceCount = Integer.parseInt(numberOfMobileDevices);
            if (mobileDeviceCount < 25) {
                return String.valueOf(FreeEditionHandler.getInstance().getFreeEditionMobileDevices());
            }
        }
        return numberOfMobileDevices;
    }
    
    public boolean isNoOfMobileDevicesManagedIsEmptyString() {
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        Properties MDMProps = null;
        final String propertyKey = "NumberOfMobileDevices";
        MDMProps = LicenseProvider.getInstance().getModuleProperties("MobileDevices");
        if (MDMProps != null) {
            final String numberOfMobileDevices = MDMProps.getProperty(propertyKey);
            if (numberOfMobileDevices.trim().length() == 0) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isMobileDeviceLicenseReached(final int managedDeviceCount) {
        try {
            String noOfMobileDevices = "0";
            JSONObject licenseDetails = null;
            final boolean isModernMgmtCapable = MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable();
            if (!isModernMgmtCapable) {
                noOfMobileDevices = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
            }
            else {
                licenseDetails = MDMApiFactoryProvider.getMDMModernMgmtAPI().licenseListener(LicenseAction.GET_USAGE_DETAILS);
                if (licenseDetails != null) {
                    noOfMobileDevices = licenseDetails.optString("allowedCount", "0");
                }
            }
            if (noOfMobileDevices == null || noOfMobileDevices.equalsIgnoreCase("unlimited")) {
                return false;
            }
            int allowedDeviceCount = Integer.valueOf(noOfMobileDevices);
            if (isModernMgmtCapable && licenseDetails != null) {
                final int managedComputerCount = licenseDetails.optInt("managedCount", 0);
                final int osdCommonCount = licenseDetails.optInt("osdCommonCount", 0);
                int deployedMachinesCount = licenseDetails.optInt("deployedMachineCount", 0);
                deployedMachinesCount = ((deployedMachinesCount == -1) ? 0 : deployedMachinesCount);
                allowedDeviceCount -= managedComputerCount + deployedMachinesCount - osdCommonCount;
            }
            if (managedDeviceCount >= allowedDeviceCount) {
                return true;
            }
        }
        catch (final Exception e) {
            Logger.getLogger(MDMLicenseImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }
    
    public boolean isMobileDeviceLicenseLimitExceed(final int managedDeviceCount) {
        try {
            String noOfMobileDevices = "0";
            JSONObject licenseDetails = null;
            final boolean isModernMgmtCapable = MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable();
            if (!isModernMgmtCapable) {
                noOfMobileDevices = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
            }
            else {
                licenseDetails = MDMApiFactoryProvider.getMDMModernMgmtAPI().licenseListener(LicenseAction.GET_USAGE_DETAILS);
                if (licenseDetails != null) {
                    noOfMobileDevices = licenseDetails.optString("allowedCount", "0");
                }
            }
            if (noOfMobileDevices == null || noOfMobileDevices.equalsIgnoreCase("unlimited")) {
                return false;
            }
            int allowedDeviceCount = Integer.valueOf(noOfMobileDevices);
            if (isModernMgmtCapable && licenseDetails != null) {
                final int managedComputerCount = licenseDetails.optInt("managedCount", 0);
                final int osdCommonCount = licenseDetails.optInt("osdCommonCount", 0);
                int deployedMachinesCount = licenseDetails.optInt("deployedMachineCount", 0);
                deployedMachinesCount = ((deployedMachinesCount == -1) ? 0 : deployedMachinesCount);
                allowedDeviceCount -= managedComputerCount + deployedMachinesCount - osdCommonCount;
            }
            if (managedDeviceCount > allowedDeviceCount) {
                return true;
            }
        }
        catch (final Exception e) {
            Logger.getLogger(MDMLicenseImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }
    
    public boolean isMDMLicenseLimitExceed() {
        try {
            final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount(MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck());
            return LicenseProvider.getInstance().isMobileDeviceLicenseLimitExceed(managedDeviceCount);
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMLicenseImpl.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean isMDMLicenseLimitReached() {
        try {
            final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount(MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck());
            return LicenseProvider.getInstance().isMobileDeviceLicenseReached(managedDeviceCount);
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMLicenseImpl.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public int getActiveMobileDevices() {
        try {
            return ManagedDeviceHandler.getInstance().getManagedDeviceCount();
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMLicenseImpl.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    public boolean isProfessionalLicenseEdition() {
        final String edition = LicenseProvider.getInstance().getMDMLicenseAPI().getMDMLiceseEditionType();
        if (edition.equalsIgnoreCase("Professional")) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    public boolean isEnterpriseLicenseEdition() {
        final String edition = LicenseProvider.getInstance().getMDMLicenseAPI().getMDMLiceseEditionType();
        if (edition.equalsIgnoreCase("Enterprise")) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    public String getMDMLiceseEditionType() {
        return MDMApiFactoryProvider.getMDMUtilAPI().getLicenseType();
    }
    
    public int getManagedDeviceCount() {
        return ManagedDeviceHandler.getInstance().getManagedDeviceCount(MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck());
    }
    
    public String getAllowedDeviceCount() {
        String allowedDevices = "";
        final boolean isModernMgmtCapable = MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable();
        if (!isModernMgmtCapable) {
            allowedDevices = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
        }
        else {
            final JSONObject licenseDetails = MDMApiFactoryProvider.getMDMModernMgmtAPI().licenseListener(LicenseAction.GET_USAGE_DETAILS);
            if (licenseDetails != null) {
                allowedDevices = licenseDetails.optString("allowedCount", "0");
            }
        }
        return allowedDevices;
    }
    
    public boolean isModernManagementCapable() {
        return MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable();
    }
    
    public String getPurchasedMobileCount() {
        try {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable()) {
                Properties MDMProps = null;
                final String propertyKey = "NumberOfMobileDevices";
                MDMProps = LicenseProvider.getInstance().getModuleProperties("MobileDevices");
                if (MDMProps == null) {
                    return String.valueOf(FreeEditionHandler.getInstance().getFreeEditionMobileDevices());
                }
                final String numberOfMobileDevices = MDMProps.getProperty(propertyKey);
                if (numberOfMobileDevices.trim().length() == 0) {
                    return String.valueOf(FreeEditionHandler.getInstance().getFreeEditionMobileDevices());
                }
                if (numberOfMobileDevices.equalsIgnoreCase("unlimited")) {
                    return "unlimited";
                }
                return numberOfMobileDevices;
            }
        }
        catch (final Exception e) {
            Logger.getLogger(MDMLicenseImpl.class.getName()).log(Level.SEVERE, "Exception while fetching license details for mobile", e);
        }
        return this.getNoOfMobileDevicesManaged();
    }
}
