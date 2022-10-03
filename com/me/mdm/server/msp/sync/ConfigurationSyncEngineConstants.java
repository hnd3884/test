package com.me.mdm.server.msp.sync;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationSyncEngineConstants
{
    public static final int PROFILE_PUBLISH = 100;
    public static final int PROFILE_TRASH = 101;
    public static final int PROFILE_DELETE_PERMANENTLY = 102;
    public static final int PROFILE_RESTORE = 103;
    public static final int NEW_APP_ADD = 201;
    public static final int NEW_APP_VERSION = 202;
    public static final int APP_VERSION_UPDATE = 203;
    public static final int APP_VERSION_APPROVAL = 204;
    public static final int APP_MOVE_TO_ALL_CUSTOMER = 205;
    public static final int APP_CONFIGURATION_ADD = 206;
    public static final int APP_CONFIGURATION_UPDATE = 207;
    public static final int APP_CONFIGURATION_DELETE = 208;
    public static final int APP_TRASH = 209;
    public static final int APP_PERMANENT_DELETE = 210;
    public static final int APP_RESTORE = 211;
    public static final int APP_VERSION_DELETE = 212;
    public static final String APP_UNIQUE_IDENTIFIER = "app_unique_identifier";
    public static final String EXISTING_APP_UNIQUE_IDENTIFIER = "existing_app_unique_identifier";
    public static final String PARENT_APP_PATH = "parent_app_path";
    public static final String PARENT_DISPLAY_IMAGE_PATH = "parent_display_image_path";
    public static final String PARENT_FULL_IMAGE_PATH = "parent_full_image_path";
    public static List<Integer> restrictedPayloads;
    
    static {
        ConfigurationSyncEngineConstants.restrictedPayloads = new ArrayList<Integer>(Arrays.asList(174, 175, 176, 177, 516, 515, 521, 526, 553, 554, 555, 564, 566, 556));
    }
}
