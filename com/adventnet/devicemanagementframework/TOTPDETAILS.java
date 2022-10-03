package com.adventnet.devicemanagementframework;

public final class TOTPDETAILS
{
    public static final String TABLE = "TotpDetails";
    public static final String TOTP_ID = "TOTP_ID";
    public static final int TOTP_ID_IDX = 1;
    public static final String SECRET = "SECRET";
    public static final int SECRET_IDX = 2;
    public static final String ALGORITHM = "ALGORITHM";
    public static final int ALGORITHM_IDX = 3;
    public static final String VALIDITY_TIME = "VALIDITY_TIME";
    public static final int VALIDITY_TIME_IDX = 4;
    public static final String TOLERANCE = "TOLERANCE";
    public static final int TOLERANCE_IDX = 5;
    
    private TOTPDETAILS() {
    }
}
