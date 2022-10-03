package com.adventnet.audit;

public final class AUDITCONFIG
{
    public static final String TABLE = "AuditConfig";
    public static final String ID = "ID";
    public static final int ID_IDX = 1;
    public static final String MODULE_ID = "MODULE_ID";
    public static final int MODULE_ID_IDX = 2;
    public static final String CUSTOMPROVIDER = "CUSTOMPROVIDER";
    public static final int CUSTOMPROVIDER_IDX = 3;
    public static final String CURRENTLEVEL = "CURRENTLEVEL";
    public static final int CURRENTLEVEL_IDX = 4;
    public static final String DISABLEALLAUDIT = "DISABLEALLAUDIT";
    public static final int DISABLEALLAUDIT_IDX = 5;
    public static final String ENABLECRITERIA = "ENABLECRITERIA";
    public static final int ENABLECRITERIA_IDX = 6;
    
    private AUDITCONFIG() {
    }
}
