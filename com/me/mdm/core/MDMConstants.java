package com.me.mdm.core;

import java.io.File;

public class MDMConstants
{
    public static final String MDM_PROVIDER_NAME = "MEMDM";
    public static final String DEVICE_RESOURCE_ALIAS = "DeviceResource";
    public static final String MDM_UPLOAD_CACHE = "uploadCache";
    public static final String RUN_DIR;
    public static final String BASE_DIR;
    public static final String MDM_DIR;
    public static final String VIEW_SEARCH_COLUMN = "SEARCH_COLUMN";
    public static final String VIEW_SEARCH_VALUE = "SEARCH_VALUE";
    
    static {
        RUN_DIR = System.getProperty("user.dir");
        BASE_DIR = MDMConstants.RUN_DIR.substring(0, MDMConstants.RUN_DIR.length() - 4);
        MDM_DIR = MDMConstants.BASE_DIR + File.separator + "mdm";
    }
}
