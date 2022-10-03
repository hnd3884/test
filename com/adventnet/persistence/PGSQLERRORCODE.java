package com.adventnet.persistence;

public final class PGSQLERRORCODE
{
    public static final String TABLE = "PgSQLErrorCode";
    public static final String ERRORID = "ERRORID";
    public static final int ERRORID_IDX = 1;
    public static final String ERRORCODE = "ERRORCODE";
    public static final int ERRORCODE_IDX = 2;
    public static final String ERRORMESSAGE = "ERRORMESSAGE";
    public static final int ERRORMESSAGE_IDX = 3;
    
    private PGSQLERRORCODE() {
    }
}
