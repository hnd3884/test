package com.me.devicemanagement.framework.webclient.customer;

import java.util.ArrayList;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import javax.servlet.http.Cookie;
import com.me.devicemanagement.framework.server.security.DMCookieUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class MSPWebClientUtil
{
    protected static Logger out;
    
    public static void setFirstCustomerIdInCookie(final HttpServletRequest request, final HttpServletResponse response) {
        final String custid = getFirstCustomerID(request);
        if (custid != null) {
            setCustomerInCookie(request, response, custid);
            setSummaryPageInCookie(request, response, "false");
            CustomerInfoThreadLocal.setCustomerId(custid);
            CustomerInfoThreadLocal.setSummaryPage("false");
        }
    }
    
    public static void setCustomerInCookie(final HttpServletRequest request, final HttpServletResponse response, final String customerID) {
        final Cookie custCookie = DMCookieUtil.generateDMCookies(request, "dc_customerid", "" + customerID);
        response.addCookie(custCookie);
    }
    
    public static void setSummaryPageInCookie(final HttpServletRequest request, final HttpServletResponse response, final String flag) {
        final Cookie spageCookie = DMCookieUtil.generateDMCookies(request, "summarypage", flag);
        response.addCookie(spageCookie);
    }
    
    @Deprecated
    public static Long getCustomerID(final HttpServletRequest request) {
        return getCustomerID(request, null);
    }
    
    public static Long getCustomerID(final String customerIDStr) {
        return getCustomerID(null, customerIDStr);
    }
    
    @Deprecated
    public static HashMap getClientSettingsFromCookie(final HttpServletRequest request) {
        return getClientSettingsFromCookie(request.getCookies());
    }
    
    @Deprecated
    public static HashMap getClientSettingsFromCookie(final Cookie[] cookies) {
        final HashMap<String, String> map = new HashMap<String, String>();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                final Cookie c = cookies[i];
                if (c.getName().equals("dc_customerid")) {
                    map.put("customerid", c.getValue());
                }
                else if (c.getName().equals("summarypage")) {
                    map.put("summarypage", c.getValue());
                }
            }
        }
        return map;
    }
    
    public static String getSummaryPageFromCookie(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                final Cookie c = cookies[i];
                if (c.getName().equals("summarypage")) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
    
    @Deprecated
    public static String getCustomerIDFromCookie(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                final Cookie c = cookies[i];
                if (c.getName().equals("dc_customerid")) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
    
    public static void removeClientSettingsFromCookie(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                final Cookie c = cookies[i];
                if (c.getName().equals("dc_customerid")) {
                    c.setMaxAge(0);
                    response.addCookie(c);
                }
                else if (c.getName().equals("summarypage")) {
                    c.setMaxAge(0);
                    response.addCookie(c);
                }
            }
        }
    }
    
    @Deprecated
    private static Long getCustomerID(final HttpServletRequest request, String customerIDStr) {
        final String sourceMethod = "MSPWebClientUtil::getCustomerID";
        Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS() && !isMSP) {
            final Long customerid = ApiFactoryProvider.getUtilAccessAPI().getCustomerID();
            MSPWebClientUtil.out.log(Level.FINEST, "IN SAS EDITION Default customer id is   " + customerid);
            return customerid;
        }
        Label_0247: {
            if (isMSP) {
                if (customerIDStr == null && request != null) {
                    final HashMap customerInfoHash = getClientSettingsFromCookie(request);
                    customerIDStr = customerInfoHash.get("customerid");
                }
                if (customerIDStr != null && !customerIDStr.equals("")) {
                    if (customerIDStr.equals("All")) {
                        try {
                            return CustomerInfoUtil.getInstance().getCustomerIDForLoginUser();
                        }
                        catch (final Exception e) {
                            MSPWebClientUtil.out.log(Level.WARNING, "=========Exception while getting customer id for login user ");
                            e.printStackTrace();
                            break Label_0247;
                        }
                    }
                    try {
                        final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                        final List customerList = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userId);
                        customerID = Long.parseLong(customerIDStr);
                        if (!isCustomerManagedByLoggedInUser(customerID)) {
                            final HashMap hMap = customerList.get(0);
                            customerID = hMap.get("CUSTOMER_ID");
                        }
                    }
                    catch (final Exception ex) {
                        MSPWebClientUtil.out.log(Level.SEVERE, sourceMethod + " --> customerID Not available in Cookie and Request either. Returning null.");
                    }
                }
            }
        }
        if (customerID == null) {
            MSPWebClientUtil.out.log(Level.INFO, sourceMethod + " --> customerID Not available in Cookie. Hence, trying to get from Request.");
            try {
                return CustomerInfoUtil.getInstance().getCustomerIDForLoginUser();
            }
            catch (final Exception e) {
                MSPWebClientUtil.out.log(Level.SEVERE, sourceMethod + " --> customerID Not available in Cookie and Request either. Returning null.");
                return customerID;
            }
        }
        MSPWebClientUtil.out.log(Level.INFO, sourceMethod + " --> customerID in cookie : " + customerID);
        return customerID;
    }
    
    public static String getFirstCustomerID(final HttpServletRequest request) {
        final String sourceMethod = "MSPWebClientUtil::getFirstCustomerID";
        String customerID = null;
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        if (!isMSP) {
            final Long lCustomerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            customerID = String.valueOf(lCustomerID);
        }
        else {
            final HashMap customerInfoHash = getClientSettingsFromCookie(request);
            final String customerIDStr = customerInfoHash.get("customerid");
            if (customerIDStr != null && !customerIDStr.equals("")) {
                if (customerIDStr.equals("All")) {
                    try {
                        final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                        final List customerList = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userId);
                        if (customerList.size() > 0) {
                            String cid = "";
                            final String sPrevCustomerId = getPrevCustomerIdFromCookie(request);
                            if (sPrevCustomerId != null && isCustomerIDAvailableInCustomerList(sPrevCustomerId, customerList)) {
                                cid = sPrevCustomerId;
                            }
                            else {
                                final HashMap firstCustomer = customerList.get(0);
                                cid = String.valueOf(firstCustomer.get("CUSTOMER_ID"));
                            }
                            return cid;
                        }
                    }
                    catch (final Exception e) {
                        MSPWebClientUtil.out.log(Level.WARNING, "=========Exception while getting customer id for login user ");
                        e.printStackTrace();
                    }
                }
                else {
                    customerID = customerIDStr;
                }
            }
        }
        MSPWebClientUtil.out.log(Level.INFO, sourceMethod + " --> customerID in cookie : " + customerID);
        return customerID;
    }
    
    public static boolean isCustomerIDAvailableInCustomerList(final String customerId, final List customerList) {
        if (customerId != null && customerList != null && customerList.size() > 0) {
            for (final HashMap map : customerList) {
                if (customerId.equals(String.valueOf(map.get("CUSTOMER_ID")))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void setCustomerIDSummaryInCookie(final HttpServletRequest request, final HttpServletResponse response, final String customerId) {
        String summaryPage = "false";
        if (customerId != null && !customerId.equals("")) {
            if (customerId.equals("All")) {
                summaryPage = "true";
            }
            setCustomerInCookie(request, response, customerId);
            setSummaryPageInCookie(request, response, summaryPage);
        }
    }
    
    public static void setPrevCustomerIdInCookie(final HttpServletRequest request, final HttpServletResponse response, final String customerID) {
        final Cookie custCookie = DMCookieUtil.generateDMCookies(request, "prevcustomerid", "" + customerID);
        response.addCookie(custCookie);
    }
    
    public static String getPrevCustomerIdFromCookie(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                final Cookie c = cookies[i];
                if (c.getName().equals("prevcustomerid")) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
    
    public static boolean isCustomerManagedByLoggedInUser(final Long customerId) {
        try {
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final boolean isAdmin = DMUserHandler.isUserInAdminRole(loginId);
            final List customerList = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userId);
            return isAdmin || isCustomerIDAvailableInCustomerList(customerId + "", customerList);
        }
        catch (final Exception ex) {
            MSPWebClientUtil.out.log(Level.SEVERE, " Exception while checking isCustomerManagedByLoggedInUser " + customerId);
            return false;
        }
    }
    
    public static Long isCustomerManagedByLoggedInUser(final String customerId) {
        if (customerId == null) {
            return null;
        }
        final Long customerIdLng = Long.valueOf(customerId);
        return isCustomerManagedByLoggedInUser(customerIdLng) ? customerIdLng : null;
    }
    
    public static ArrayList<Long> areCustomersManagedByloginUser(final String customerList, final String delimiter) {
        final ArrayList<Long> customerListLng = new ArrayList<Long>();
        for (final String customerId : customerList.split(delimiter)) {
            final Long customerIdLng = Long.valueOf(customerId);
            if (!isCustomerManagedByLoggedInUser(customerIdLng)) {
                return null;
            }
            customerListLng.add(customerIdLng);
        }
        return customerListLng;
    }
    
    static {
        MSPWebClientUtil.out = Logger.getLogger(MSPWebClientUtil.class.getName());
    }
}
