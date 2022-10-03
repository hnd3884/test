package com.me.devicemanagement.framework.server.util;

public class SomTrackingParameters
{
    private static final String STATUS_UNKNOWN = "0";
    public static final int UNKNOWN_ENV = 0;
    public static final int NOT_JOINED_ENV = 1;
    public static final int WORKGROUP_ENV = 2;
    public static final int ACTIVE_DIRECTORY_ENV = 3;
    public int environment;
    public int adComputerCount;
    public int wgComputerCount;
    public String envErrorStatus;
    public String adCompCountErrorStatus;
    public String wgCompCountErrorStatus;
    
    public SomTrackingParameters() {
        this.environment = 0;
        this.adComputerCount = -1;
        this.wgComputerCount = -1;
        this.envErrorStatus = "0";
        this.adCompCountErrorStatus = "0";
        this.wgCompCountErrorStatus = "0";
    }
    
    public String getEnvironmentIdString() {
        String environmentId = null;
        switch (this.environment) {
            case 0: {
                environmentId = "uk";
                break;
            }
            case 1: {
                environmentId = "nj";
                break;
            }
            case 2: {
                environmentId = "wg";
                break;
            }
            case 3: {
                environmentId = "ad";
                break;
            }
            default: {
                environmentId = "uk";
                break;
            }
        }
        return environmentId;
    }
}
