package com.me.devicemanagement.framework.server.exception;

public class NativeException extends SyMException
{
    private int nativeErrorCode;
    
    public NativeException() {
        this.nativeErrorCode = -1;
    }
    
    public NativeException(final int nativeErrCode, final int dcErrCode, final Throwable cause) {
        super(dcErrCode, cause);
        this.nativeErrorCode = -1;
        this.nativeErrorCode = nativeErrCode;
    }
    
    public NativeException(final int nativeErrCode, final int dcErrCode, final String errorMsg, final Throwable cause) {
        super(dcErrCode, errorMsg, cause);
        this.nativeErrorCode = -1;
        this.nativeErrorCode = nativeErrCode;
    }
    
    public int getNativeErrorCode() {
        return this.nativeErrorCode;
    }
}
