package com.adventnet.sym.winaccess;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.List;
import com.me.devicemanagement.framework.server.api.ADSyncAPI;
import com.me.devicemanagement.framework.server.api.ADReportsAPI;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ADReportsHandler
{
    private static ADReportsHandler adReportsHandler;
    private static Logger logger;
    
    public static ADReportsHandler getInstance() {
        if (ADReportsHandler.adReportsHandler == null) {
            ADReportsHandler.adReportsHandler = new ADReportsHandler();
        }
        return ADReportsHandler.adReportsHandler;
    }
    
    public void convertADDataAndAddToQueue(final ArrayList list, final String netBIOSName, final int nResType, final int nObjectCount, final int nStartIndex, final int nEndIndex, final boolean isFirstList, final boolean isLastList) throws ClassNotFoundException {
        final String callbackClass = BulkADFetchThreadLocal.getCallBack();
        ADReportsHandler.logger.log(Level.INFO, "convertADDataAndAddToQueue : callbackClass : " + callbackClass);
        try {
            final String[] multiImplProductClass;
            final String[] callBackClassesImpl = multiImplProductClass = ProductClassLoader.getMultiImplProductClass(callbackClass);
            for (final String className : multiImplProductClass) {
                final String s = callbackClass;
                switch (s) {
                    case "DM_ADREPORTS_IMPL_CLASS": {
                        ((ADReportsAPI)Class.forName(className).newInstance()).convertADDataAndAddToQueue(list, netBIOSName, nResType, nObjectCount, nStartIndex, nEndIndex, isFirstList, isLastList);
                        break;
                    }
                    case "DM_ADSYNC_IMPL_CLASS": {
                        ((ADSyncAPI)Class.forName(className).newInstance()).proccessFetchedADData((Long)null, list, netBIOSName, (Long)null, nResType, nObjectCount, nStartIndex, nEndIndex, isFirstList, isLastList);
                        break;
                    }
                }
            }
        }
        catch (final Exception ex) {
            ADReportsHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void convertDCUserDDataAndAddToQueue(final ArrayList list, final String netBIOSName, final int nResType, final int nObjectCount, final int nStartIndex, final int nEndIndex, final boolean isFirstList, final boolean isLastList) throws Exception {
        final String callbackClass = BulkADFetchThreadLocal.getCallBack();
        ADReportsHandler.logger.log(Level.INFO, "convertDCUserDDataAndAddToQueue : callbackClass : " + callbackClass);
        try {
            final String[] multiImplProductClass;
            final String[] callBackClassesImpl = multiImplProductClass = ProductClassLoader.getMultiImplProductClass(callbackClass);
            for (final String className : multiImplProductClass) {
                final String s = callbackClass;
                switch (s) {
                    case "DM_ADSYNC_IMPL_CLASS": {
                        ((ADSyncAPI)Class.forName(className).newInstance()).proccessFetchedADData((Long)null, list, netBIOSName, (Long)null, nResType, nObjectCount, nStartIndex, nEndIndex, isFirstList, isLastList);
                        break;
                    }
                    case "DM_ADREPORTS_IMPL_CLASS": {
                        ((ADReportsAPI)Class.forName(className).newInstance()).convertDCUserDDataAndAddToQueue(list, netBIOSName, nResType, nObjectCount, nStartIndex, nEndIndex, isFirstList, isLastList);
                        break;
                    }
                }
            }
        }
        catch (final Exception ex) {
            ADReportsHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized int fetchBulkADdata(final String strNetBIOSName, final String strDCName, final String strLdapPath, final String strBindUserName, final String strPassword, final int nResType, final List lisAttributes, final String strFilter, final int nSearchLevel, final long nModifiedTime, final int fetchCount, final boolean isDCUserFetch, final String callbackClass, final boolean isSSL, final int portNo) throws Exception {
        if (callbackClass == null) {
            throw new Exception("Setting a Call back class is a must");
        }
        BulkADFetchThreadLocal.setCallBack(callbackClass);
        ADReportsHandler.logger.log(Level.INFO, "strNetBIOSName = {0}, strDCName = {1}, strLdapPath = {2},strBindUserName ={3},strPassword={4},nResType={5},lisAttributes={6},strFilter ={7}, nSearchLevel={8},nModifiedTime={9},fetchCount={10}, isDCUserFetch={11},callbackClass={12}", new Object[] { strNetBIOSName, strDCName, strLdapPath, strBindUserName, "---", nResType, lisAttributes.toString(), strFilter, nSearchLevel, nModifiedTime, fetchCount, isDCUserFetch, callbackClass });
        final int resCount = this.nativeFetchBulkADData(strNetBIOSName, strDCName, strLdapPath, strBindUserName, strPassword, nResType, lisAttributes, strFilter, nSearchLevel, nModifiedTime, fetchCount, isDCUserFetch, isSSL, portNo);
        ADReportsHandler.logger.log(Level.INFO, "strNetBIOSName = {0}, strDCName = {1}, strLdapPath = {2},strBindUserName ={3},strPassword={4},nResType={5},lisAttributes={6},strFilter ={7}, nSearchLevel={8},nModifiedTime={9},fetchCount={10}, isDCUserFetch={11},callbackClass={12},resCount={13}", new Object[] { strNetBIOSName, strDCName, strLdapPath, strBindUserName, "---", nResType, lisAttributes.toString(), strFilter, nSearchLevel, nModifiedTime, fetchCount, isDCUserFetch, callbackClass, resCount });
        return resCount;
    }
    
    private native int nativeFetchBulkADData(final String p0, final String p1, final String p2, final String p3, final String p4, final int p5, final List p6, final String p7, final int p8, final long p9, final int p10, final boolean p11, final boolean p12, final int p13) throws SyMException;
    
    static {
        ADReportsHandler.adReportsHandler = null;
        ADReportsHandler.logger = Logger.getLogger(ADReportsHandler.class.getName());
    }
}
