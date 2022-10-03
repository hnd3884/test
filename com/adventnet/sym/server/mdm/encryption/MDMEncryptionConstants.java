package com.adventnet.sym.server.mdm.encryption;

public class MDMEncryptionConstants
{
    public static final int MDM_ENCRYPTION_TYPE_FILEVAULT = 1;
    public static final int MDM_FILEVAULT_TYPE_PERSONAL = 1;
    public static final int MDM_FILEVAULT_TYPE_INSTITUTIONAL = 2;
    public static final int MDM_FILEVAULT_TYPE_COMBINED = 3;
    public static final int FILEVAULT_DEVICE_NOT_COMPATABLE = 0;
    public static final int FILEVAULT_DISABLED_ON_DEVICE = 10;
    public static final int FILEVAULT_ENABLED_BUT_NOT_BY_MDM = 20;
    public static final int FILEVAULT_ENABLED_BY_MDM_PERSONALKEY_ONLY = 21;
    public static final int FILEVAULT_ENABLED_BY_MDM_INSTITUTIONAL_ONLY = 22;
    public static final int FILEVAULT_ENABLED_BY_MDM_COMBINATION = 23;
}
