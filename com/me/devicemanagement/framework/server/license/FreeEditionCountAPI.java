package com.me.devicemanagement.framework.server.license;

public interface FreeEditionCountAPI
{
    public static final int FREE_EDITION_COMPUTERS = 25;
    public static final int FREE_EDITION_COMPUTERS_SERVER_BASED = 20;
    public static final int FREE_EDITION_SERVERS = 5;
    public static final int FREE_EDITION_MOBILE_DEVICES = 25;
    public static final int FREE_EDITION_USERS = 1;
    public static final int FREE_EDITION_SERVER_MACHINE_DEPLOYMENTS = 1;
    public static final int FREE_EDITION_WORKSTATION_MACHINE_DEPLOYMENTS = 4;
    public static final int FREE_EDITION_NETWORK_DEVICES = 2;
    
    default int getFreeEditionComputers() {
        return 25;
    }
    
    default int getFreeEditionComputersServerBased() {
        return 20;
    }
    
    default int getFreeEditionServers() {
        return 5;
    }
    
    default int getFreeEditionMobileDevices() {
        return 25;
    }
    
    default int getFreeEditionUsers() {
        return 1;
    }
    
    default int getFreeEditionServerMachineDeployments() {
        return 1;
    }
    
    default int getFreeEditionWorkstationMachineDeployments() {
        return 4;
    }
    
    default int getFreeEditionNetworkDevices() {
        return 2;
    }
}
