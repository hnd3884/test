package com.me.mdm.server.updates.osupdates;

public class OSUpdateConstants
{
    public static final class DeviceStatus
    {
        public static final Integer AVAILABLE;
        public static final Integer DOWNLOADING;
        public static final Integer INSTALLING;
        public static final Integer DOWNLOADED;
        public static final Integer GENERAL_FAILED;
        public static final Integer DOWNLOAD_FAILED;
        public static final Integer INSTALL_FAILED;
        
        static {
            AVAILABLE = 1;
            DOWNLOADING = 2;
            INSTALLING = 3;
            DOWNLOADED = 4;
            GENERAL_FAILED = 5;
            DOWNLOAD_FAILED = 6;
            INSTALL_FAILED = 7;
        }
    }
}
