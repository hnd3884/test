package com.me.devicemanagement.framework.server.license;

public interface OSDLicenseAPI
{
    String getLicensedDeploymentCount();
    
    String getLicensedMachineCount();
    
    String getLicensedMachineCount(final int p0);
    
    String getLicensedImagesCount();
    
    int getDeployedImagesCount();
    
    int getDeployedMachinesCount();
    
    int getDeployedMachinesCount(final int p0);
    
    boolean isLicenseLimitReachedOrExceeded();
    
    default boolean isLicenseLimitReachedOrExceeded(final int osType) {
        return true;
    }
    
    boolean isDeployedImagesCountReached();
    
    boolean isDeployedImagesCountExceeded();
}
