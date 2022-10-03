package com.me.devicemanagement.framework.server.license;

import org.json.JSONObject;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FreeEditionHandler
{
    protected static Logger logger;
    protected static FreeEditionHandler sUtil;
    private static final String FREE_EDITION_COUNT_CONFIG = "free_edition_count";
    private static int free_edition_computers;
    private static int free_edition_computers_server_based;
    private static int free_edition_servers;
    private static int free_edition_mobile_devices;
    private static int free_edition_users;
    private static int free_edition_server_machine_deployments;
    private static int free_edition_workstation_machine_deployments;
    
    public static synchronized FreeEditionHandler getInstance() {
        if (FreeEditionHandler.sUtil == null) {
            FreeEditionHandler.sUtil = new FreeEditionHandler();
        }
        return FreeEditionHandler.sUtil;
    }
    
    public int getFreeEditionComputers() {
        return FreeEditionHandler.free_edition_computers;
    }
    
    public void setFreeEditionComputers(final int freeEditionCompCount) {
        FreeEditionHandler.free_edition_computers = freeEditionCompCount;
    }
    
    public static int getFreeEditionComputersServerBased() {
        return FreeEditionHandler.free_edition_computers_server_based;
    }
    
    public int getFreeEditionServers() {
        return FreeEditionHandler.free_edition_servers;
    }
    
    public int getFreeEditionMobileDevices() {
        return FreeEditionHandler.free_edition_mobile_devices + LicenseProvider.getInstance().getComplimentaryDevicesCount();
    }
    
    public int getFreeEditionOSDeployments(final String licenseProperty) {
        if (licenseProperty.equalsIgnoreCase("NumberOfServerMachines")) {
            return FreeEditionHandler.free_edition_server_machine_deployments;
        }
        if (licenseProperty.equalsIgnoreCase("NumberOfWorkstationMachines")) {
            return FreeEditionHandler.free_edition_workstation_machine_deployments;
        }
        return 0;
    }
    
    public int getFreeEditionUsers() {
        return FreeEditionHandler.free_edition_users;
    }
    
    public boolean isFreeEdition() {
        try {
            final LicenseProvider dclh = LicenseProvider.getInstance();
            final String licenseType = dclh.getLicenseType();
            final String productType = dclh.getProductType();
            FreeEditionHandler.logger.log(Level.FINEST, " Inside isFreeEdition method, licensetype : " + licenseType + " productType : " + productType);
            return licenseType != null && licenseType.equals("F");
        }
        catch (final Exception e) {
            FreeEditionHandler.logger.log(Level.WARNING, "Exception while fetching license", e);
            return false;
        }
    }
    
    static {
        FreeEditionHandler.logger = Logger.getLogger(FreeEditionHandler.class.getName());
        FreeEditionHandler.sUtil = null;
        FreeEditionHandler.free_edition_computers = LicenseProvider.getInstance().getFreeEditionCountAPI().getFreeEditionComputers();
        FreeEditionHandler.free_edition_computers_server_based = LicenseProvider.getInstance().getFreeEditionCountAPI().getFreeEditionComputersServerBased();
        FreeEditionHandler.free_edition_servers = LicenseProvider.getInstance().getFreeEditionCountAPI().getFreeEditionServers();
        FreeEditionHandler.free_edition_mobile_devices = LicenseProvider.getInstance().getFreeEditionCountAPI().getFreeEditionMobileDevices();
        FreeEditionHandler.free_edition_users = LicenseProvider.getInstance().getFreeEditionCountAPI().getFreeEditionUsers();
        FreeEditionHandler.free_edition_server_machine_deployments = LicenseProvider.getInstance().getFreeEditionCountAPI().getFreeEditionServerMachineDeployments();
        FreeEditionHandler.free_edition_workstation_machine_deployments = LicenseProvider.getInstance().getFreeEditionCountAPI().getFreeEditionWorkstationMachineDeployments();
        try {
            final JSONObject licenseConfigurations = LicenseConfigurations.getLicenseConfigurations();
            if (licenseConfigurations != null) {
                final JSONObject freeEditionCount = licenseConfigurations.getJSONObject("free_edition_count");
                FreeEditionHandler.free_edition_computers = freeEditionCount.getInt("FREE_EDITION_COMPUTERS");
                FreeEditionHandler.free_edition_computers_server_based = freeEditionCount.getInt("FREE_EDITION_COMPUTERS_SERVER_BASED");
                FreeEditionHandler.free_edition_servers = freeEditionCount.getInt("FREE_EDITION_SERVERS");
                FreeEditionHandler.free_edition_mobile_devices = freeEditionCount.getInt("FREE_EDITION_MOBILE_DEVICES");
                FreeEditionHandler.free_edition_users = freeEditionCount.getInt("FREE_EDITION_USERS");
                FreeEditionHandler.free_edition_server_machine_deployments = freeEditionCount.getInt("FREE_EDITION_SERVER_MACHINE_DEPLOYMENTS");
                FreeEditionHandler.free_edition_workstation_machine_deployments = freeEditionCount.getInt("FREE_EDITION_WORKSTATION_MACHINE_DEPLOYMENTS");
            }
            else {
                FreeEditionHandler.logger.log(Level.WARNING, "Free edition computers json file is corrupted");
            }
        }
        catch (final JSONException ex) {
            FreeEditionHandler.logger.log(Level.SEVERE, "Exception while retrieving data from framework configuration for free edition counts. " + ex);
        }
    }
}
