package com.adventnet.sym.server;

public class NativeException extends com.me.devicemanagement.framework.server.exception.NativeException
{
    public NativeException() {
    }
    
    public NativeException(final int nativeErrCode, final int dcErrCode, final Throwable cause) {
        super(nativeErrCode, dcErrCode, cause);
    }
    
    public NativeException(final int nativeErrCode, final int dcErrCode, final String errorMsg, final Throwable cause) {
        super(nativeErrCode, dcErrCode, errorMsg, cause);
    }
}
