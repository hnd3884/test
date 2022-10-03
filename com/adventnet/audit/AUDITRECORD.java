package com.adventnet.audit;

public final class AUDITRECORD
{
    public static final String TABLE = "AuditRecord";
    public static final String AUDITID = "AUDITID";
    public static final int AUDITID_IDX = 1;
    public static final String PRINCIPAL = "PRINCIPAL";
    public static final int PRINCIPAL_IDX = 2;
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final int TIMESTAMP_IDX = 3;
    public static final String RECORDTYPE = "RECORDTYPE";
    public static final int RECORDTYPE_IDX = 4;
    
    private AUDITRECORD() {
    }
}
