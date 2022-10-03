package com.adventnet.mdm;

public final class OTPPASSWORD
{
    public static final String TABLE = "OTPPassword";
    public static final String ENROLLMENT_REQUEST_ID = "ENROLLMENT_REQUEST_ID";
    public static final int ENROLLMENT_REQUEST_ID_IDX = 1;
    public static final String GENERATED_TIME = "GENERATED_TIME";
    public static final int GENERATED_TIME_IDX = 2;
    public static final String EXPIRE_TIME = "EXPIRE_TIME";
    public static final int EXPIRE_TIME_IDX = 3;
    public static final String OTP_PASSWORD = "OTP_PASSWORD";
    public static final int OTP_PASSWORD_IDX = 4;
    public static final String FAILED_ATTEMPTS = "FAILED_ATTEMPTS";
    public static final int FAILED_ATTEMPTS_IDX = 5;
    
    private OTPPASSWORD() {
    }
}
