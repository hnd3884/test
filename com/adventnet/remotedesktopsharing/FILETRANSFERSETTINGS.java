package com.adventnet.remotedesktopsharing;

public final class FILETRANSFERSETTINGS
{
    public static final String TABLE = "FileTransferSettings";
    public static final String FTSETTINGS_ID = "FTSETTINGS_ID";
    public static final int FTSETTINGS_ID_IDX = 1;
    public static final String CUSTOMER_ID = "CUSTOMER_ID";
    public static final int CUSTOMER_ID_IDX = 2;
    public static final String LAN_NUMBER_OF_THREADS = "LAN_NUMBER_OF_THREADS";
    public static final int LAN_NUMBER_OF_THREADS_IDX = 3;
    public static final String WAN_NUMBER_OF_THREADS = "WAN_NUMBER_OF_THREADS";
    public static final int WAN_NUMBER_OF_THREADS_IDX = 4;
    public static final String IS_COMPRESSION_ENABLED = "IS_COMPRESSION_ENABLED";
    public static final int IS_COMPRESSION_ENABLED_IDX = 5;
    public static final String LAN_COMPRESSION_TYPE = "LAN_COMPRESSION_TYPE";
    public static final int LAN_COMPRESSION_TYPE_IDX = 6;
    public static final String WAN_COMPRESSION_TYPE = "WAN_COMPRESSION_TYPE";
    public static final int WAN_COMPRESSION_TYPE_IDX = 7;
    public static final String IS_CHECKSUM_ENABLED = "IS_CHECKSUM_ENABLED";
    public static final int IS_CHECKSUM_ENABLED_IDX = 8;
    public static final String LAN_TYPE_CHUNKSIZE = "LAN_TYPE_CHUNKSIZE";
    public static final int LAN_TYPE_CHUNKSIZE_IDX = 9;
    public static final String WAN_TYPE_CHUNKSIZE = "WAN_TYPE_CHUNKSIZE";
    public static final int WAN_TYPE_CHUNKSIZE_IDX = 10;
    
    private FILETRANSFERSETTINGS() {
    }
}
