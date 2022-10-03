package com.adventnet.patchmanagement;

public final class PATCHSTORECLEANUPSETTINGS
{
    public static final String TABLE = "PatchStoreCleanupSettings";
    public static final String CLEANUP_ID = "CLEANUP_ID";
    public static final int CLEANUP_ID_IDX = 1;
    public static final String NOTIFY_IF_SPACE_EXCEED = "NOTIFY_IF_SPACE_EXCEED";
    public static final int NOTIFY_IF_SPACE_EXCEED_IDX = 2;
    public static final String MAX_SPACE_LIMIT = "MAX_SPACE_LIMIT";
    public static final int MAX_SPACE_LIMIT_IDX = 3;
    public static final String SUPERSEDE_CLEANUP = "SUPERSEDE_CLEANUP";
    public static final int SUPERSEDE_CLEANUP_IDX = 4;
    public static final String DELETE_NOT_REQUIRED_PATCHES = "DELETE_NOT_REQUIRED_PATCHES";
    public static final int DELETE_NOT_REQUIRED_PATCHES_IDX = 5;
    public static final String DELETE_PATCHES_BY_PERIOD = "DELETE_PATCHES_BY_PERIOD";
    public static final int DELETE_PATCHES_BY_PERIOD_IDX = 6;
    
    private PATCHSTORECLEANUPSETTINGS() {
    }
}
