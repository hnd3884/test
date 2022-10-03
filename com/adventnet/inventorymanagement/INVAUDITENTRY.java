package com.adventnet.inventorymanagement;

public final class INVAUDITENTRY
{
    public static final String TABLE = "InvAuditEntry";
    public static final String AUDIT_ID = "AUDIT_ID";
    public static final int AUDIT_ID_IDX = 1;
    public static final String AUDIT_START_TIME = "AUDIT_START_TIME";
    public static final int AUDIT_START_TIME_IDX = 2;
    public static final String AUDIT_END_TIME = "AUDIT_END_TIME";
    public static final int AUDIT_END_TIME_IDX = 3;
    public static final String AUDIT_TYPE = "AUDIT_TYPE";
    public static final int AUDIT_TYPE_IDX = 4;
    public static final String AUDIT_COMPUTER_COUNT = "AUDIT_COMPUTER_COUNT";
    public static final int AUDIT_COMPUTER_COUNT_IDX = 5;
    public static final String AUDIT_COMPLETION_STATUS = "AUDIT_COMPLETION_STATUS";
    public static final int AUDIT_COMPLETION_STATUS_IDX = 6;
    
    private INVAUDITENTRY() {
    }
}
