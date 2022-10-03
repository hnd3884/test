package com.sshtools.publickey;

public class PublicKeySubsystemException extends Exception
{
    public static final int ACCESS_DENIED = 1;
    public static final int STORAGE_EXCEEDED = 2;
    public static final int REQUEST_NOT_SUPPPORTED = 3;
    public static final int KEY_NOT_FOUND = 4;
    public static final int KEY_NOT_SUPPORTED = 5;
    public static final int GENERAL_FAILURE = 6;
    int b;
    
    public PublicKeySubsystemException(final int b, final String s) {
        super(s);
        this.b = b;
    }
    
    public int getStatus() {
        return this.b;
    }
}
