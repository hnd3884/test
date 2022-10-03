package com.me.devicemanagement.onpremise.server.metrack;

import java.util.Properties;
import java.util.HashMap;

public interface METrackerHandlerAPI
{
    public static final String LAST_SUCCESSFULLY_POSTDATA_FILENAME = "last_successfully_post_data.properties";
    public static final String LAST_POST_FAILED_STATUS_FILENAME = "last_post_failed_status.properties";
    public static final String FULL_POST = "0";
    public static final String DIFF_POST = "1";
    public static final String NO_DIFF = "2";
    public static final String METRACKING_TRACKER_FORMNAME = "inputmetrackingtracker";
    public static final String FULL_POST_DETAILS = "Full_Post_Details";
    public static final String DATA_GET_TIME_DUR = "Data_Get_Time_Dur";
    public static final String LAST_POST_FAILED_STATUS = "Last_Post_Failed_Status";
    public static final String UPDATE_METRACK_CONFIG_URL = "update_metrack_config_url";
    public static final String METRACKING_CONFIG_FILENAME = "metrack_config.properties";
    public static final String CONFIG_LAST_MODIFIED_SINCE = "metrack_config_last_modified_since";
    public static final String DIFF_APPLY_BUILD_NUMBER = "METrackDiffMinBuildNo";
    public static final String NOT_VALID_DNS_LIST = "NotValidDNSList";
    public static final String LAST_DOWNLOAD_STATUS = "LastDownloadStatus";
    public static final String METRACKING_CONF_FILENAME = "metracking.conf";
    public static final String METRACKING_UPDATER_CLASSNAME = "metracking_updater_classname";
    
    void postTrackingData(final HashMap<String, String> p0);
    
    Properties getInstallationDetails();
}
