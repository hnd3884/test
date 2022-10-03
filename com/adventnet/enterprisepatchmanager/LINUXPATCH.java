package com.adventnet.enterprisepatchmanager;

public final class LINUXPATCH
{
    public static final String TABLE = "LinuxPatch";
    public static final String PATCHID = "PATCHID";
    public static final int PATCHID_IDX = 1;
    public static final String ARCHITECTURE = "ARCHITECTURE";
    public static final int ARCHITECTURE_IDX = 2;
    public static final String APPNAME = "APPNAME";
    public static final int APPNAME_IDX = 3;
    public static final String APPVERSION = "APPVERSION";
    public static final int APPVERSION_IDX = 4;
    public static final String APPRELEASE = "APPRELEASE";
    public static final int APPRELEASE_IDX = 5;
    public static final String CHECKSUM = "CHECKSUM";
    public static final int CHECKSUM_IDX = 6;
    
    private LINUXPATCH() {
    }
}
