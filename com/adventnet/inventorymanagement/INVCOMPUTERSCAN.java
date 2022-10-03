package com.adventnet.inventorymanagement;

public final class INVCOMPUTERSCAN
{
    public static final String TABLE = "InvComputerScan";
    public static final String COMPUTER_ID = "COMPUTER_ID";
    public static final int COMPUTER_ID_IDX = 1;
    public static final String LAST_SCAN_TYPE = "LAST_SCAN_TYPE";
    public static final int LAST_SCAN_TYPE_IDX = 2;
    public static final String FULL_SCAN_DATA_COUNT = "FULL_SCAN_DATA_COUNT";
    public static final int FULL_SCAN_DATA_COUNT_IDX = 3;
    public static final String EMPTY_SCAN_DATA_COUNT = "EMPTY_SCAN_DATA_COUNT";
    public static final int EMPTY_SCAN_DATA_COUNT_IDX = 4;
    public static final String PREVIOUS_SCAN_TIME = "PREVIOUS_SCAN_TIME";
    public static final int PREVIOUS_SCAN_TIME_IDX = 5;
    public static final String CURRENT_SCAN_TIME = "CURRENT_SCAN_TIME";
    public static final int CURRENT_SCAN_TIME_IDX = 6;
    public static final String IS_FIRST_SCAN = "IS_FIRST_SCAN";
    public static final int IS_FIRST_SCAN_IDX = 7;
    public static final String IS_LAST_SCAN_FAILED = "IS_LAST_SCAN_FAILED";
    public static final int IS_LAST_SCAN_FAILED_IDX = 8;
    public static final String IS_FULL_SCAN_INITIATED = "IS_FULL_SCAN_INITIATED";
    public static final int IS_FULL_SCAN_INITIATED_IDX = 9;
    
    private INVCOMPUTERSCAN() {
    }
}
