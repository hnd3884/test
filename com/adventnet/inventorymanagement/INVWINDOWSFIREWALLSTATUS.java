package com.adventnet.inventorymanagement;

public final class INVWINDOWSFIREWALLSTATUS
{
    public static final String TABLE = "InvWindowsFirewallStatus";
    public static final String COMPUTER_ID = "COMPUTER_ID";
    public static final int COMPUTER_ID_IDX = 1;
    public static final String STANDARD_PROFILE_STATUS = "STANDARD_PROFILE_STATUS";
    public static final int STANDARD_PROFILE_STATUS_IDX = 2;
    public static final String DOMAIN_PROFILE_STATUS = "DOMAIN_PROFILE_STATUS";
    public static final int DOMAIN_PROFILE_STATUS_IDX = 3;
    public static final String PUBLIC_PROFILE_STATUS = "PUBLIC_PROFILE_STATUS";
    public static final int PUBLIC_PROFILE_STATUS_IDX = 4;
    public static final String IS_EXTERNAL_FIREWALL_ENABLED = "IS_EXTERNAL_FIREWALL_ENABLED";
    public static final int IS_EXTERNAL_FIREWALL_ENABLED_IDX = 5;
    public static final String EXTERNAL_FIREWALL_NAME = "EXTERNAL_FIREWALL_NAME";
    public static final int EXTERNAL_FIREWALL_NAME_IDX = 6;
    
    private INVWINDOWSFIREWALLSTATUS() {
    }
}
