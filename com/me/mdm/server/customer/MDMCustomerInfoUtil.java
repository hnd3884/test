package com.me.mdm.server.customer;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.List;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.mdm.server.enrollment.ios.IOSUpgradeMobileConfigCommandHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;

public class MDMCustomerInfoUtil extends CustomerInfoUtil
{
    private static MDMCustomerInfoUtil custInfoUtil;
    public static final String DEFAULT_CUSTOMER = "DEFAULT_CUSTOMER";
    public static final String DEFAULT_CUSTOMER_EMAIL = "no-reply@mdmmsp.com";
    
    private MDMCustomerInfoUtil() {
        this.isMSP();
        isSAS();
    }
    
    public static synchronized MDMCustomerInfoUtil getInstance() {
        if (MDMCustomerInfoUtil.custInfoUtil == null) {
            MDMCustomerInfoUtil.custInfoUtil = new MDMCustomerInfoUtil();
        }
        return MDMCustomerInfoUtil.custInfoUtil;
    }
    
    public Long addCustomer(final String customerName, final Properties customerInfo) {
        Long customerId = new Long(-2L);
        try {
            customerId = super.addCustomer(customerName, customerInfo);
            if (customerId.equals(-1L) || customerId.equals(-2L)) {
                return customerId;
            }
            this.addOrUpdateCustomerInfo(customerId, customerInfo);
            final Boolean addUser = Boolean.parseBoolean(((Hashtable<K, String>)customerInfo).get("ADD_USER"));
            if (addUser) {
                ((Hashtable<String, Long>)customerInfo).put("CUSTOMER_ID", customerId);
                MDMApiFactoryProvider.getMDMLoginUserAPI().addUserOnCustomerAddition(customerInfo);
            }
        }
        catch (final Exception exp) {
            MDMCustomerInfoUtil.out.log(Level.INFO, "Exception while allocating customer", exp);
        }
        return customerId;
    }
    
    public Long updateCustomer(final Long customerId, final String customerName, final Properties customerInfo) {
        Long customerID = new Long(-2L);
        try {
            final String oldCompanyName = this.getCompanyName(customerId);
            if (oldCompanyName != null && !oldCompanyName.equals(((Hashtable<K, Object>)customerInfo).get("COMPANY_NAME"))) {
                Logger.getLogger("MDMIosEnrollmentClientCertificateLogger").log(Level.INFO, "MDMCustomerInfoUtil: Adding upgrade mobile config for eligible devices.");
                IOSUpgradeMobileConfigCommandHandler.getInstance().addIosUpgradeMobileConfigCommand(customerId, false, false);
            }
            customerID = super.updateCustomer(customerId, customerName, customerInfo);
            if (customerId.equals(-1L) || customerId.equals(-2L)) {
                return customerId;
            }
            this.addOrUpdateCustomerInfo(customerID, customerInfo);
        }
        catch (final Exception exp) {
            MDMCustomerInfoUtil.out.log(Level.INFO, "Exception while updating customer computer allocation", exp);
        }
        return customerID;
    }
    
    private void addOrUpdateCustomerInfo(final Long customerId, final Properties customerInfo) {
        final String numDevices = ((Hashtable<K, String>)customerInfo).get("NO_OF_DEVICES");
        int noOfDevices = 0;
        if (numDevices != null) {
            if (!numDevices.equals("")) {
                noOfDevices = Integer.valueOf(numDevices);
            }
            MDMCustomerLicenseUtil.getInstance().addorUpdateDeviceAllocation(customerId, noOfDevices);
        }
    }
    
    public void addDefaultCustomerForEval() {
        final Properties defaultCustomerprop = new Properties();
        ((Hashtable<String, Long>)defaultCustomerprop).put("CUSTOMER_ID", -2L);
        ((Hashtable<String, String>)defaultCustomerprop).put("CUSTOMER_NAME", "DEFAULT_CUSTOMER");
        ((Hashtable<String, String>)defaultCustomerprop).put("CUSTOMER_EMAIL", "no-reply@mdmmsp.com");
        ((Hashtable<String, String>)defaultCustomerprop).put("NO_OF_DEVICES", "");
        this.addCustomer("DEFAULT_CUSTOMER", defaultCustomerprop);
    }
    
    public boolean isCustomerIDValidForUser(final Long userID, final Long customerID) throws Exception {
        boolean isValid = false;
        final ArrayList customers = super.getCustomerDetailsForUser(userID);
        for (final Object customer : customers) {
            final HashMap temp = (HashMap)customer;
            if (temp.get("CUSTOMER_ID").equals(customerID)) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }
    
    public void validateCustomerForUserId(final Long userId, final Long customerId) {
        try {
            if (!this.isCustomerIDValidForUser(userId, customerId)) {
                throw new APIHTTPException("COM0008", new Object[] { "Customer Id:" + customerId });
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            MDMCustomerInfoUtil.logger.log(Level.SEVERE, "Cannot validate customer id to user {0}", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public String getCompanyName(final Long customerId) {
        String company_name = null;
        try {
            if (customerId != -1L) {
                final Properties customerDetails = this.getAllDetailsOfCustomer(customerId);
                company_name = customerDetails.getProperty("COMPANY_NAME");
            }
        }
        catch (final Exception e) {
            MDMCustomerInfoUtil.out.log(Level.SEVERE, "Exception while updating Company Name", e);
        }
        return company_name;
    }
    
    public void updateCompanyName(final Long customerId, String companyName) {
        try {
            if (customerId != null && customerId != -1L) {
                Properties customerDetails = this.getAllDetailsOfCustomer(customerId);
                final String presentCompanyName = customerDetails.getProperty("COMPANY_NAME");
                MDMCustomerInfoUtil.out.log(Level.FINE, "CompanyNameUpdate method called : Before trim : Old company name : --{0}--, new name : --{1}--, customer ID : {2}", new Object[] { presentCompanyName, companyName, customerId });
                companyName = companyName.trim();
                MDMCustomerInfoUtil.out.log(Level.FINE, "After trim: Old company name : --{0}--, new name : --{1}--", new Object[] { presentCompanyName, companyName });
                if (presentCompanyName == null || !companyName.equals(presentCompanyName.trim())) {
                    MDMCustomerInfoUtil.out.log(Level.INFO, "company name mismatch - Going to update company name : {0} with new name : {1} for customer ID : {2}", new Object[] { presentCompanyName, companyName, customerId });
                    final String customerName = customerDetails.getProperty("CUSTOMER_NAME");
                    final String emailAddr = customerDetails.getProperty("CUSTOMER_EMAIL");
                    customerDetails = new Properties();
                    ((Hashtable<String, String>)customerDetails).put("COMPANY_NAME", companyName);
                    if (emailAddr != null) {
                        ((Hashtable<String, String>)customerDetails).put("CUSTOMER_EMAIL", emailAddr);
                    }
                    ((Hashtable<String, String>)customerDetails).put("COMPANY_NAME", companyName);
                    this.updateCustomer(customerId, customerName, customerDetails);
                    final String companyNameIdDB = this.getCompanyName(customerId);
                    if (companyNameIdDB == null || !companyNameIdDB.trim().equals(companyName)) {
                        throw new Exception("Company name not updated, new company name : " + companyName + ", company name in DB " + companyNameIdDB);
                    }
                }
            }
        }
        catch (final Exception e) {
            MDMCustomerInfoUtil.out.log(Level.SEVERE, "Exception while updating Company Name", e);
        }
    }
    
    public List getValidCustomerIds(final List<Long> customerIds) throws Exception {
        final ArrayList<Long> actualCustomerIds = new ArrayList<Long>();
        final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        final ArrayList<Long> userCustomerIds = CustomerInfoUtil.getInstance().getCustomerIDsForLogIn(loginID);
        for (final Long customerId : customerIds) {
            if (userCustomerIds.contains(customerId)) {
                actualCustomerIds.add(customerId);
            }
        }
        return actualCustomerIds;
    }
    
    public Boolean validateAccessForAllCustomers(final Long loginId) throws Exception {
        final ArrayList<Long> loginUserAllCustomerId = CustomerInfoUtil.getInstance().getCustomerIDsForLogIn(loginId);
        final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
        if (loginUserAllCustomerId.size() != customerIds.length) {
            return false;
        }
        Boolean flag = true;
        for (final Long customerId : customerIds) {
            if (!loginUserAllCustomerId.contains(customerId)) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    
    static {
        MDMCustomerInfoUtil.custInfoUtil = null;
    }
}
