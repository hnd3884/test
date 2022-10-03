package com.adventnet.sym.winaccess;

public class BulkADFetchThreadLocal
{
    static ThreadLocal<String> callbackClass;
    
    public static void setCallBack(final String callBack) {
        BulkADFetchThreadLocal.callbackClass.set(callBack);
    }
    
    public static String getCallBack() {
        return BulkADFetchThreadLocal.callbackClass.get();
    }
    
    public static void clearCallBack() {
        BulkADFetchThreadLocal.callbackClass.remove();
    }
    
    static {
        BulkADFetchThreadLocal.callbackClass = new ThreadLocal<String>();
    }
}
