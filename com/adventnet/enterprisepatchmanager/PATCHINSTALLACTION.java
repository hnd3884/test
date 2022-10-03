package com.adventnet.enterprisepatchmanager;

public final class PATCHINSTALLACTION
{
    public static final String TABLE = "PatchInstallAction";
    public static final String PATCHID = "PATCHID";
    public static final int PATCHID_IDX = 1;
    public static final String INSTALLTYPE = "INSTALLTYPE";
    public static final int INSTALLTYPE_IDX = 2;
    public static final String INSTALLER = "INSTALLER";
    public static final int INSTALLER_IDX = 3;
    public static final String INSTALLFILEPATH = "INSTALLFILEPATH";
    public static final int INSTALLFILEPATH_IDX = 4;
    public static final String ARGUMENTS = "ARGUMENTS";
    public static final int ARGUMENTS_IDX = 5;
    
    private PATCHINSTALLACTION() {
    }
}
