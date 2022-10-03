package com.adventnet.audit;

public final class OPERATIONAUDITRECORD
{
    public static final String TABLE = "OperationAuditRecord";
    public static final String AUDITID = "AUDITID";
    public static final int AUDITID_IDX = 1;
    public static final String HOSTNAME = "HOSTNAME";
    public static final int HOSTNAME_IDX = 2;
    public static final String RESOURCENAME = "RESOURCENAME";
    public static final int RESOURCENAME_IDX = 3;
    public static final String OPERATIONNAME = "OPERATIONNAME";
    public static final int OPERATIONNAME_IDX = 4;
    public static final String STARTTIME = "STARTTIME";
    public static final int STARTTIME_IDX = 5;
    public static final String COMPLETIONTIME = "COMPLETIONTIME";
    public static final int COMPLETIONTIME_IDX = 6;
    public static final String RESULT = "RESULT";
    public static final int RESULT_IDX = 7;
    public static final String SEVERITY = "SEVERITY";
    public static final int SEVERITY_IDX = 8;
    
    private OPERATIONAUDITRECORD() {
    }
}
