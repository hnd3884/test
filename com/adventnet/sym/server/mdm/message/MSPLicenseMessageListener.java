package com.adventnet.sym.server.mdm.message;

import java.util.Hashtable;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.lang.reflect.Method;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.alerts.MDMAlertConstants;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import com.me.mdm.server.license.MDMLicenseImplMSP;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.license.LicenseEvent;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.me.devicemanagement.framework.server.license.LicenseListener;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class MSPLicenseMessageListener extends ManagedDeviceListener implements LicenseListener
{
    @Override
    public void deviceUnmanaged(final DeviceEvent userEvent) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            MSPLicenseMessageListener.mdmlogger.info("Entering MSPLicenseMessageListener:deviceUnmanaged");
            MDMMessageHandler.getInstance().messageAction("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", userEvent.customerID);
            MSPLicenseMessageListener.mdmlogger.info("Exiting MSPLicenseMessageListener:deviceUnmanaged");
        }
        else {
            MSPLicenseMessageListener.mdmlogger.info("Skipping for non MSP Customer");
        }
    }
    
    @Override
    public void deviceManaged(final DeviceEvent userEvent) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            MSPLicenseMessageListener.mdmlogger.info("Entering MSPLicenseMessageListener:deviceManaged");
            MDMMessageHandler.getInstance().messageAction("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", userEvent.customerID);
            final boolean isDCMsp = CustomerInfoUtil.getInstance().isMSP() && ProductUrlLoader.getInstance().getGeneralProperites().getProperty("productcode").equals("DCMSP");
            if (isDCMsp) {
                this.validateLicenseAlert(userEvent);
            }
            else {
                this.checkAndSendLicenseLimitReachedMails(userEvent.customerID);
            }
            MSPLicenseMessageListener.mdmlogger.info("Exiting MSPLicenseMessageListener:deviceManaged");
        }
        else {
            MSPLicenseMessageListener.mdmlogger.info("Skipping for non MSP Customer");
        }
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent userEvent) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            MSPLicenseMessageListener.mdmlogger.info("Entering MSPLicenseMessageListener:deviceDeleted");
            MDMMessageHandler.getInstance().messageAction("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", userEvent.customerID);
            MSPLicenseMessageListener.mdmlogger.info("Exiting MSPLicenseMessageListener:deviceDeleted");
        }
        else {
            MSPLicenseMessageListener.mdmlogger.info("Skipping for non MSP Customer");
        }
    }
    
    public void licenseChanged(final LicenseEvent licenseEvent) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            MSPLicenseMessageListener.mdmlogger.info("Entering MSPLicenseMessageListener:licenseEvent");
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            if (!licenseType.equals("T")) {
                final int totalNoOfDevicesAlloc = new MDMLicenseImplMSP().getTotalDeviceAllocatedOrManaged();
                int totalNoOfDeviesManaged = 0;
                final String noOfDevicesManaged = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
                if (noOfDevicesManaged != null && !noOfDevicesManaged.equalsIgnoreCase("unlimited") && !noOfDevicesManaged.equals("")) {
                    totalNoOfDeviesManaged = Integer.valueOf(noOfDevicesManaged);
                }
                if ((licenseType.equals("R") && totalNoOfDevicesAlloc > totalNoOfDeviesManaged) || licenseType.equals("F")) {
                    this.resetDeviceAllocationForCustomers();
                }
            }
            MSPLicenseMessageListener.mdmlogger.info("Exiting MSPLicenseMessageListener:licenseEvent");
        }
        else {
            MSPLicenseMessageListener.mdmlogger.info("Skipping for non MSP Customer");
        }
    }
    
    private void checkAndSendLicenseLimitReachedMails(final Long customerID) {
        final MDMLicenseImplMSP mspLicesne = new MDMLicenseImplMSP();
        if (mspLicesne.isMobileDeviceLicenseReached(customerID)) {
            this.sendLicenseLimitReachedMails(customerID);
        }
    }
    
    private void sendLicenseLimitReachedMails(final Long customerID) {
        try {
            final MDMLicenseImplMSP mspLicesne = new MDMLicenseImplMSP();
            final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil();
            final Properties customerProp = CustomerInfoUtil.getInstance().getAllDetailsOfCustomer(customerID);
            final String allocatedLimitString = mspLicesne.getNoOfMobileDevicesAllocated(customerID);
            final int allocatedLimit = Integer.valueOf(allocatedLimitString);
            final String custName = customerProp.getProperty("CUSTOMER_NAME");
            MSPLicenseMessageListener.mdmlogger.log(Level.INFO, "****************************************************");
            MSPLicenseMessageListener.mdmlogger.log(Level.INFO, "Alloted Limit Reached for Customer {0} ID:{1}", new Object[] { custName, customerID });
            MSPLicenseMessageListener.mdmlogger.log(Level.INFO, "Alloted Limit : {0}", allocatedLimit);
            MSPLicenseMessageListener.mdmlogger.log(Level.INFO, "****************************************************");
            if (customerProp.getProperty("CUSTOMER_EMAIL") != null) {
                final Properties emailProp = new Properties();
                final String custEmail = customerProp.getProperty("CUSTOMER_EMAIL");
                ((Hashtable<String, String>)emailProp).put("$customerName$", custName);
                ((Hashtable<String, String>)emailProp).put("$user_emailid$", custEmail);
                ((Hashtable<String, Boolean>)emailProp).put("appendFooter", true);
                MSPLicenseMessageListener.mdmlogger.log(Level.INFO, "License Allocation Limit Reached : Sending Mail for Customer...{0} Alert Type {1}", new Object[] { emailProp, MDMAlertConstants.DEVICE_ALLOCATED_LIMIT_REACHED_TO_CUSTOMER });
                mailGenerator.sendMail(MDMAlertConstants.DEVICE_ALLOCATED_LIMIT_REACHED_TO_CUSTOMER, "MDM", customerID, emailProp);
            }
            final String storeUrl = I18N.getMsg(ProductUrlLoader.getInstance().getValue("store_url"), new Object[0]) + "?did=" + SyMUtil.getDIDValue();
            final ArrayList userList = CustomerInfoUtil.getInstance().getUsersForCustomer(customerID);
            final ArrayList adminRoleList = new ArrayList();
            final String adminRole = DMUserHandler.getRoleID("Administrator");
            final String custAdminRole = DMUserHandler.getRoleID("Customer Administrator");
            Long adminRoleId = -1L;
            Long custAdminRoleId = -1L;
            if (adminRole != null) {
                adminRoleId = Long.valueOf(adminRole);
                adminRoleList.add(adminRoleId);
            }
            if (custAdminRole != null) {
                custAdminRoleId = Long.valueOf(custAdminRole);
                adminRoleList.add(custAdminRoleId);
            }
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaContactInfo"));
            query.addJoin(new Join("AaaContactInfo", "AaaUserContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            query.addJoin(new Join("AaaUserContactInfo", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            query.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            query.addJoin(new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            query.addSelectColumn(new Column("AaaUser", "USER_ID"));
            query.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            query.addSelectColumn(new Column("UsersRoleMapping", "LOGIN_ID"));
            query.addSelectColumn(new Column("UsersRoleMapping", "UM_ROLE_ID"));
            query.addSelectColumn(new Column("AaaContactInfo", "EMAILID"));
            query.addSelectColumn(new Column("AaaContactInfo", "CONTACTINFO_ID"));
            query.addSelectColumn(new Column("AaaUserContactInfo", "USER_ID"));
            query.addSelectColumn(new Column("AaaUserContactInfo", "CONTACTINFO_ID"));
            final Criteria userCriteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)userList.toArray(), 8);
            final Criteria adminCriteria = new Criteria(Column.getColumn("UsersRoleMapping", "UM_ROLE_ID"), (Object)adminRoleList.toArray(), 8);
            query.setCriteria(userCriteria.and(adminCriteria));
            try {
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(query);
                if (dataObject != null && !dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("UsersRoleMapping");
                    while (iterator.hasNext()) {
                        final Row usersRoleMappingRow = iterator.next();
                        final Long userRole = (Long)usersRoleMappingRow.get("UM_ROLE_ID");
                        final Long loginId = (Long)usersRoleMappingRow.get("LOGIN_ID");
                        final Row aaaLoginRow = dataObject.getRow("AaaLogin", new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0));
                        final Long userId = (Long)aaaLoginRow.get("USER_ID");
                        final Long contactInfoId = (Long)dataObject.getRow("AaaUserContactInfo", new Criteria(Column.getColumn("AaaUserContactInfo", "USER_ID"), (Object)userId, 0)).get("CONTACTINFO_ID");
                        final Row aaaContactInfoRow = dataObject.getRow("AaaContactInfo", new Criteria(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"), (Object)contactInfoId, 0));
                        final String emailID = (String)aaaContactInfoRow.get("EMAILID");
                        if (emailID == null) {
                            continue;
                        }
                        final Properties emailProp2 = new Properties();
                        ((Hashtable<String, String>)emailProp2).put("$user_emailid$", emailID);
                        ((Hashtable<String, Integer>)emailProp2).put("$deviceallocatedLimit$", allocatedLimit);
                        ((Hashtable<String, String>)emailProp2).put("$storeurl$", storeUrl);
                        ((Hashtable<String, String>)emailProp2).put("$customerName$", custName);
                        ((Hashtable<String, Boolean>)emailProp2).put("appendFooter", true);
                        Long alertType = -1L;
                        if (userRole.equals(adminRoleId) && allocatedLimit == 0) {
                            alertType = MDMAlertConstants.DEVICE_LICENSE_LIMIT_REACHED_MAIL_TO_ADMIN;
                            ((Hashtable<String, String>)emailProp2).put("$desc$", I18N.getMsg("mdm.emailAlert.admin_license_content", new Object[] { storeUrl }));
                        }
                        else if (userRole.equals(adminRoleId) && allocatedLimit != 0) {
                            alertType = MDMAlertConstants.DEVICE_ALLOCATION_LIMIT_REACHED_MAIL_TO_ADMIN;
                            ((Hashtable<String, String>)emailProp2).put("$desc$", I18N.getMsg("mdm.emailAlert.admin_allocation_limit_content_admin", new Object[] { storeUrl }));
                        }
                        else if (userRole.equals(custAdminRoleId) && allocatedLimit == 0) {
                            ((Hashtable<String, String>)emailProp2).put("$desc$", I18N.getMsg("mdm.emailAlert.customer_admin_license_content", new Object[0]));
                            alertType = MDMAlertConstants.DEVICE_LICENSE_LIMIT_REACHED_MAIL_TO_ADMIN;
                        }
                        else if (userRole.equals(custAdminRoleId) && allocatedLimit != 0) {
                            ((Hashtable<String, String>)emailProp2).put("$desc$", "");
                            alertType = MDMAlertConstants.DEVICE_ALLOCATION_LIMIT_REACHED_MAIL_TO_ADMIN;
                        }
                        MSPLicenseMessageListener.mdmlogger.log(Level.INFO, "License Allocation Limit Reached : Sending Mail for Admins...{0} Alert Type {1}", new Object[] { emailProp2, alertType });
                        mailGenerator.sendMail(alertType, "MDM", customerID, emailProp2);
                    }
                }
            }
            catch (final Exception ex) {
                MSPLicenseMessageListener.mdmlogger.log(Level.SEVERE, "Exception in sendLicenseLimitReachedMailsForCustomer {0}", ex);
            }
        }
        catch (final Exception ex2) {
            MSPLicenseMessageListener.mdmlogger.log(Level.SEVERE, "Exception in sendLicenseLimitReachedMailsForCustomer {0}", ex2);
        }
    }
    
    public void validateLicenseAlert(final DeviceEvent userEvent) {
        MSPLicenseMessageListener.mdmlogger.log(Level.INFO, "Validating for License Reach Alert");
        try {
            if (userEvent.customerID != null) {
                final Long customerID = userEvent.customerID;
                final Properties emailProp = new Properties();
                ((Hashtable<String, Long>)emailProp).put("$customer_id$", customerID);
                ((Hashtable<String, String>)emailProp).put("$customerName$", CustomerInfoUtil.getInstance().getCustomerName(customerID));
                final String dcClassName = "com.me.dconpremise.integration.mdm.MDMLicenseAlertUtil";
                final Class<?> listenerImplClass = Class.forName(dcClassName);
                final Object listenerImplObj = listenerImplClass.newInstance();
                final String methodName = "sendDeviceAlert";
                final Method sendDeviceAlert = listenerImplObj.getClass().getMethod(methodName, Long.class);
                sendDeviceAlert.invoke(listenerImplObj, customerID);
            }
        }
        catch (final Exception e) {
            MSPLicenseMessageListener.mdmlogger.log(Level.SEVERE, "Error occurred while validating License Alert", e);
        }
    }
    
    private void resetDeviceAllocationForCustomers() {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DeviceLimitToCustomerMapping");
            updateQuery.setUpdateColumn("NO_OF_DEVICES", (Object)0);
            SyMUtil.getPersistence().update(updateQuery);
            final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerIds != null && customerIds.length >= 0) {
                for (final Long customerID : customerIds) {
                    MessageProvider.getInstance().hideMessage("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", customerID);
                }
            }
        }
        catch (final Exception ex) {
            MSPLicenseMessageListener.mdmlogger.log(Level.SEVERE, "Exception in resetDeviceAllocationForCustomers {0}", ex);
        }
    }
    
    @Override
    public void deviceDeprovisioned(final DeviceEvent deviceEvent) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            MSPLicenseMessageListener.mdmlogger.info("Entering MSPLicenseMessageListener:deviceDeprovisioned");
            MDMMessageHandler.getInstance().messageAction("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", deviceEvent.customerID);
            MSPLicenseMessageListener.mdmlogger.info("Exiting MSPLicenseMessageListener:deviceDeprovisioned");
        }
        else {
            MSPLicenseMessageListener.mdmlogger.info("Skipping for non MSP Customer");
        }
    }
}
