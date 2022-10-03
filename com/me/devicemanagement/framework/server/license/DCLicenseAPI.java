package com.me.devicemanagement.framework.server.license;

public interface DCLicenseAPI
{
    String getNoOfComutersManaged();
    
    String getNoOfWorkstationEndpointLicense();
    
    String getNoOfServerEndpointLicense();
    
    boolean isComputerLicenseReached(final int p0);
    
    boolean isComputerLicenseLimitExceed(final int p0);
    
    boolean isDCLicenseLimitExceed();
    
    boolean isDCLicenseLimitReached();
    
    boolean isManagedMacAddress(final String p0, final Long p1);
    
    int getManagedComputersCount();
    
    int getManagedServersCount();
    
    int getUnifiedMCAndImgDeploymentCount();
    
    int getUnifiedMCAndImgDeploymentCount(final String p0, final Integer p1, final Boolean p2);
    
    boolean isModernManagementEnabled();
    
    default String getNoOfServersLicense() {
        return "0";
    }
    
    default boolean isServerLicenseLimitExceed(final int managedServerCount) {
        return false;
    }
    
    default boolean isServerLicenseReached(final int managedServerCount) {
        return false;
    }
    
    default boolean isServerPropertyPresent() {
        return false;
    }
    
    default boolean isTotalMachinesLimitExceed(final int totalMachines) {
        return false;
    }
    
    default boolean isTotalMachineLimitReached(final int totalMachines) {
        return false;
    }
    
    default int getManagedComputersCount(final String domainName) {
        return 0;
    }
    
    default int getManagedServersCount(final String domainName) {
        return 0;
    }
    
    default int getManagedComputersCount(final boolean onlyComputers) {
        return 0;
    }
    
    default int getAllSoMComputersCount(final String domainName) {
        return 0;
    }
    
    default int getAllSoMComputersCount(final boolean onlyComputers) {
        return 0;
    }
    
    default int getManagedMobileDevicesCount() {
        return 0;
    }
    
    String getPurchasedComputerCount();
    
    Boolean isEndpointLimitReachedForMSP(final Long p0);
}
