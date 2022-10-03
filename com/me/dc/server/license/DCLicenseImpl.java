package com.me.dc.server.license;

import java.util.Properties;
import com.me.devicemanagement.framework.server.license.FreeEditionHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.license.DCLicenseAPI;

public class DCLicenseImpl implements DCLicenseAPI
{
    public String getNoOfComutersManaged() {
        Properties numOfSystems = LicenseProvider.getInstance().getModuleProperties("Users");
        String propertyKey = "NumberOfUsers";
        if (numOfSystems == null) {
            propertyKey = "NumberOfComputers";
            final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
            if (version.equals("11")) {
                numOfSystems = LicenseProvider.getInstance().getModuleProperties("SOM");
            }
            else {
                numOfSystems = LicenseProvider.getInstance().getModuleProperties("Computers");
            }
        }
        if (numOfSystems != null) {
            final String numberOfSystems = numOfSystems.getProperty(propertyKey);
            return numberOfSystems;
        }
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType != null && licenseType.equalsIgnoreCase("T")) {
            return "unlimited";
        }
        return String.valueOf(FreeEditionHandler.getInstance().getFreeEditionComputers());
    }
    
    public boolean isComputerLicenseReached(final int managedCompCount) {
        return false;
    }
    
    public boolean isComputerLicenseLimitExceed(final int managedCompCount) {
        return false;
    }
    
    public boolean isDCLicenseLimitExceed() {
        return false;
    }
    
    public boolean isDCLicenseLimitReached() {
        return false;
    }
    
    public int getUnifiedMCAndImgDeploymentCount() {
        return 0;
    }
    
    public int getManagedComputersCount() {
        return 0;
    }
    
    public int getManagedServersCount() {
        return 0;
    }
    
    public int getUnifiedMCAndImgDeploymentCount(final String domainName, final Integer agentStatus, final Boolean isServer) {
        return 0;
    }
    
    public boolean isManagedMacAddress(final String macAddress, final Long customerID) {
        return false;
    }
    
    public String getNoOfServerEndpointLicense() {
        return "unlimited";
    }
    
    public String getNoOfWorkstationEndpointLicense() {
        return "unlimited";
    }
    
    public boolean isModernManagementEnabled() {
        return false;
    }
    
    public boolean isTotalMachinesLimitExceed(final int totalMachines) {
        return false;
    }
    
    public String getPurchasedComputerCount() {
        return null;
    }
    
    public Boolean isEndpointLimitReachedForMSP(final Long customerID) {
        return false;
    }
    
    public String getNoOfServersLicense() {
        return "";
    }
    
    public boolean isServerLicenseLimitExceed(final int managedServerCount) {
        return false;
    }
    
    public boolean isServerLicenseReached(final int managedServerCount) {
        return false;
    }
    
    public boolean isServerPropertyPresent() {
        return false;
    }
}
