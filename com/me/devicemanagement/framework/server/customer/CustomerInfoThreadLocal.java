package com.me.devicemanagement.framework.server.customer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerInfoThreadLocal
{
    static Logger out;
    static ThreadLocal<String> customerId;
    static ThreadLocal<String> summaryPage;
    static ThreadLocal<String> skipCustomerFilter;
    static ThreadLocal<String> isClientCall;
    
    public static String getCustomerId() {
        return CustomerInfoThreadLocal.customerId.get();
    }
    
    public static String getSummaryPage() {
        CustomerInfoThreadLocal.out.log(Level.FINEST, "getSummaryPage : " + CustomerInfoThreadLocal.customerId.toString());
        return CustomerInfoThreadLocal.summaryPage.get();
    }
    
    public static void setCustomerId(final String custId) {
        CustomerInfoThreadLocal.customerId.set(custId);
    }
    
    public static void setSummaryPage(final String flag) {
        CustomerInfoThreadLocal.out.log(Level.FINEST, "setSummaryPage --> setSummaryPage : " + flag);
        CustomerInfoThreadLocal.summaryPage.set(flag);
    }
    
    public static void clearClientThreadSettings() {
        CustomerInfoThreadLocal.out.log(Level.FINEST, "clearClientThreadSettings called ...");
        CustomerInfoThreadLocal.customerId.remove();
        CustomerInfoThreadLocal.summaryPage.remove();
        CustomerInfoThreadLocal.skipCustomerFilter.remove();
        CustomerInfoThreadLocal.isClientCall.remove();
    }
    
    public static String getSkipCustomerFilter() {
        CustomerInfoThreadLocal.out.log(Level.FINEST, "getSkipCustomerFilter : " + CustomerInfoThreadLocal.skipCustomerFilter.toString());
        return CustomerInfoThreadLocal.skipCustomerFilter.get();
    }
    
    public static void setSkipCustomerFilter(final String flag) {
        CustomerInfoThreadLocal.skipCustomerFilter.set(flag);
    }
    
    public static String getIsClientCall() {
        CustomerInfoThreadLocal.out.log(Level.FINEST, "getIsClientCall : " + CustomerInfoThreadLocal.isClientCall.toString());
        return CustomerInfoThreadLocal.isClientCall.get();
    }
    
    public static void setIsClientCall(final String flag) {
        CustomerInfoThreadLocal.isClientCall.set(flag);
    }
    
    static {
        CustomerInfoThreadLocal.out = Logger.getLogger(CustomerInfoThreadLocal.class.getName());
        CustomerInfoThreadLocal.customerId = new ThreadLocal<String>();
        CustomerInfoThreadLocal.summaryPage = new ThreadLocal<String>();
        CustomerInfoThreadLocal.skipCustomerFilter = new ThreadLocal<String>();
        CustomerInfoThreadLocal.isClientCall = new ThreadLocal<String>();
    }
}
