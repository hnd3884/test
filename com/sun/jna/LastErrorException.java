package com.sun.jna;

public class LastErrorException extends RuntimeException
{
    private int errorCode;
    
    private static String formatMessage(final int code) {
        return Platform.isWindows() ? ("GetLastError() returned " + code) : ("errno was " + code);
    }
    
    private static String parseMessage(final String m) {
        try {
            return formatMessage(Integer.parseInt(m));
        }
        catch (final NumberFormatException e) {
            return m;
        }
    }
    
    public LastErrorException(final String msg) {
        super(parseMessage(msg));
        try {
            this.errorCode = Integer.parseInt(msg);
        }
        catch (final NumberFormatException e) {
            this.errorCode = -1;
        }
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public LastErrorException(final int code) {
        super(formatMessage(code));
        this.errorCode = code;
    }
}
