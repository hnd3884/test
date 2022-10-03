package com.me.devicemanagement.framework.server.license;

public class FreeEditionCount implements FreeEditionCountAPI
{
    private static final int FREE_EDITION_COMPUTERS = 25;
    private static final int FREE_EDITION_COMPUTERS_SERVER_BASED = 20;
    private static final int FREE_EDITION_SERVERS = 5;
    private static final int FREE_EDITION_MOBILE_DEVICES = 25;
    private static final int FREE_EDITION_USERS = 1;
    private static final int FREE_EDITION_SERVER_MACHINE_DEPLOYMENTS = 1;
    private static final int FREE_EDITION_WORKSTATION_MACHINE_DEPLOYMENTS = 4;
    private static final int FREE_EDITION_NETWORK_DEVICES = 2;
    
    @Override
    public int getFreeEditionComputers() {
        return 25;
    }
    
    @Override
    public int getFreeEditionComputersServerBased() {
        return 20;
    }
    
    @Override
    public int getFreeEditionServers() {
        return 5;
    }
    
    @Override
    public int getFreeEditionMobileDevices() {
        return 25;
    }
    
    @Override
    public int getFreeEditionUsers() {
        return 1;
    }
    
    @Override
    public int getFreeEditionServerMachineDeployments() {
        return 1;
    }
    
    @Override
    public int getFreeEditionWorkstationMachineDeployments() {
        return 4;
    }
    
    @Override
    public int getFreeEditionNetworkDevices() {
        return 2;
    }
}
