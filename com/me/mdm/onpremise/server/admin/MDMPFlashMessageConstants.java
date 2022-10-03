package com.me.mdm.onpremise.server.admin;

import com.me.devicemanagement.onpremise.server.common.FlashMessageConstants;

public class MDMPFlashMessageConstants extends FlashMessageConstants
{
    public static final String NO_OF_MOBILES = "noofmobile";
    public static final String NO_OF_DEVICES_DIVIDE_BY_VALUE = "NoOfDeviceDivideByValue";
    public static final Object TASKCHECKER;
    public static final String UPDATES_CHECKER_TASK = "MDMPUpdatesCheckerTask";
    
    static {
        TASKCHECKER = new Object();
    }
}
