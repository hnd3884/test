package com.me.mdm.server.user;

import java.util.Hashtable;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.core.UserEvent;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.Properties;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.webclient.mdm.MDMEnrollAction;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public abstract class BaseManagedUserImportTask extends CSVTask
{
    public Logger logger;
    private final int range = 50;
    
    public BaseManagedUserImportTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    protected void performOperation(final JSONObject json) throws Exception {
        try {
            boolean sendInvite = false;
            if (json.containsKey((Object)"send_invite")) {
                sendInvite = (boolean)json.get((Object)"send_invite");
            }
            for (int totalRowCount = DBUtil.getRecordCount("ManagedUserImportInfo", "MANAGED_USER_IMPORT_ID", new Criteria(Column.getColumn("ManagedUserImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0).and(new Criteria(Column.getColumn("ManagedUserImportInfo", "ERROR_REMARKS"), (Object)null, 0))), j = 0; j <= totalRowCount / 50; ++j) {
                int completedCount = 0;
                final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel("ManagedUserImport"), (long)this.customerID);
                if (countStr != null) {
                    completedCount = Integer.parseInt(countStr);
                }
                final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserImportInfo"));
                squery.addSelectColumn(Column.getColumn("ManagedUserImportInfo", "*"));
                squery.setCriteria(new Criteria(Column.getColumn("ManagedUserImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0).and(new Criteria(Column.getColumn("ManagedUserImportInfo", "ERROR_REMARKS"), (Object)null, 0)));
                squery.setRange(new Range((j * 50 == 0) ? 0 : (j * 50 + 1), 50));
                squery.addSortColumn(new SortColumn(Column.getColumn("ManagedUserImportInfo", "MANAGED_USER_IMPORT_ID"), true));
                DataObject dobj = MDMUtil.getPersistence().get(squery);
                int processed = 0;
                do {
                    final Iterator<Row> iter = dobj.getRows("ManagedUserImportInfo");
                    final List<Long> deleteRowList = new ArrayList<Long>();
                    while (iter.hasNext()) {
                        final Row inputRow = iter.next();
                        ++processed;
                        final Long importID = (Long)inputRow.get("MANAGED_USER_IMPORT_ID");
                        final String email = (String)inputRow.get("EMAIL_ADDRESS");
                        final String username = (String)inputRow.get("USER_NAME");
                        final String phoneNumber = (String)inputRow.get("PHONE_NUMBER");
                        try {
                            final String columnList = this.getMissingFields(username, email);
                            if (!columnList.isEmpty()) {
                                inputRow.set("ERROR_REMARKS", (Object)"dc.mdm.msg.enroll.columns_cannot_be_null");
                                inputRow.set("ERROR_REMARKS_ARGS", (Object)columnList);
                                dobj.updateRow(inputRow);
                            }
                            else {
                                final String domainName = "MDM";
                                final String emailEditEnabled = SyMUtil.getSyMParameter("IsEmailEditEnabled");
                                final boolean isEmailEditEnabled = emailEditEnabled != null && emailEditEnabled.equalsIgnoreCase("true");
                                final String newEmail = (String)inputRow.get("NEW_EMAIL_ADDRESS");
                                this.importUser(importID, domainName, username, email, newEmail, isEmailEditEnabled, phoneNumber);
                                if (sendInvite) {
                                    final Properties properties = MDMEnrollmentUtil.getInstance().buildEnrollmentProperties(domainName, username, null, email, String.valueOf(2), this.customerID, true, String.valueOf(0), false);
                                    ((Hashtable<String, Boolean>)properties).put("KNOX_LIC_DS", false);
                                    ((Hashtable<String, Boolean>)properties).put("isAzure", false);
                                    ((Hashtable<String, Boolean>)properties).put("byAdmin", false);
                                    boolean sendEmail = false;
                                    if (MDMUtil.getInstance().isValidEmail(email)) {
                                        sendEmail = true;
                                    }
                                    boolean sendSMS = false;
                                    if (!MDMUtil.isStringEmpty(phoneNumber) && MDMEnrollmentUtil.isValidPhone(phoneNumber)) {
                                        CustomerInfoUtil.getInstance();
                                        if (CustomerInfoUtil.isSAS()) {
                                            sendSMS = true;
                                        }
                                    }
                                    MDMEnrollmentUtil.getInstance().setEnrollmentInvitationProperties(properties, phoneNumber, String.valueOf(sendEmail), String.valueOf(sendSMS));
                                    ((Hashtable<String, Long>)properties).put("USER_ID", this.userID);
                                    new MDMEnrollAction().addEnrollmentRequest(properties);
                                }
                                deleteRowList.add(importID);
                            }
                        }
                        catch (final SyMException e) {
                            inputRow.set("ERROR_REMARKS", (Object)I18N.getMsg(e.getErrorKey(), new Object[0]));
                            dobj.updateRow(inputRow);
                        }
                    }
                    dobj.deleteRows("ManagedUserImportInfo", new Criteria(Column.getColumn("ManagedUserImportInfo", "MANAGED_USER_IMPORT_ID"), (Object)deleteRowList.toArray(), 8));
                    MDMUtil.getPersistence().update(dobj);
                    CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel("ManagedUserImport"), String.valueOf(completedCount + processed), (long)this.customerID);
                    dobj = MDMUtil.getPersistence().get(squery);
                } while (!dobj.isEmpty());
                this.setFailureCount(this.customerID);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(BaseManagedUserImportTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setFailureCount(final long customerID) throws Exception {
        try {
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)CSVProcessor.getFailedLabel(new ManagedUserImportProcessor().getOperationLabel()), (Object)String.valueOf(DBUtil.getRecordCount("ManagedUserImportInfo", "MANAGED_USER_IMPORT_ID", new Criteria(Column.getColumn("ManagedUserImportInfo", "ERROR_REMARKS"), (Object)null, 1).and(new Criteria(Column.getColumn("ManagedUserImportInfo", "CUSTOMER_ID"), (Object)customerID, 0)))));
            jsonObj.put((Object)CSVProcessor.getStatusLabel(new ManagedUserImportProcessor().getOperationLabel()), (Object)"COMPLETED");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerID);
            this.logger.info("Persisted failure count in Customer Params");
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private String getMissingFields(final String username, final String email) throws SyMException {
        String columnList = "";
        if (username == null || username.isEmpty() || username.equalsIgnoreCase("--")) {
            columnList += "USER_NAME";
        }
        if (email == null || email.isEmpty() || email.equalsIgnoreCase("--")) {
            columnList = (columnList.isEmpty() ? "EMAIL_ADDRESS" : (columnList + "," + "EMAIL_ADDRESS"));
        }
        return columnList;
    }
    
    private void importUser(final Long importID, final String domainName, final String username, final String email, final String newEmail, final boolean isEmailEditEnabled, final String phoneNumber) throws Exception {
        this.validateEmailAddress(email);
        this.validateUserName(username, domainName);
        if (phoneNumber != null) {
            this.validatePhoneNumber(phoneNumber);
        }
        this.addOrUpdateManagedUser(username, email, domainName, phoneNumber);
        final Object remarksArgs = username;
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, DMUserHandler.getUserNameFromUserID(this.userID), "mdm.user.add_user", remarksArgs, this.customerID);
        MDMOneLineLogger.log(Level.INFO, "ADD_LOCAL_USER", "create-success");
    }
    
    private void invokeUserModifiedListeners(final List<UserEvent> userEventList) {
        final Iterator<UserEvent> userEventIterator = userEventList.iterator();
        while (userEventIterator.hasNext()) {
            ManagedUserHandler.getInstance().invokeUserListeners(userEventIterator.next(), 3);
        }
    }
    
    private void addUserModifiedEventLog(final String username, final String email) {
        final Object remarksArgs = username + "@@@" + email + "@@@";
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(this.userID)), "dc.mdm.enroll.change_user_name_by_email_actionlog", remarksArgs, this.customerID);
        MDMOneLineLogger.log(Level.INFO, "MODIFY_USER", "update-success");
    }
    
    private void validateEmailAddress(final String email) throws SyMException {
        if (!new MDMUtil().isValidEmail(email)) {
            throw new SyMException(14003, "Email Address is invalid", "dc.mdm.enroll.invalid_email", (Throwable)null);
        }
    }
    
    private void validateUserName(final String username, final String domainName) throws SyMException, Exception {
        final HashMap map = ManagedUserHandler.getInstance().getManagedUserDetailsForUserName(username, domainName, this.customerID);
        if (!map.isEmpty() && map.containsKey("MANAGED_USER_ID")) {
            throw new SyMException(52103, "A user already exists for given email address", I18N.getMsg("dc.mdm.enroll.error.change_user_name_exists", new Object[0]), (Throwable)null);
        }
        if (!this.isValidStr(username)) {
            throw new SyMException(51014, "Enter a valid user name", "dc.mdm.enroll.valid_user", (Throwable)null);
        }
    }
    
    private void addOrUpdateManagedUser(final String username, final String email, final String domainName, final String phoneNumber) {
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("NAME", username);
            ((Hashtable<String, String>)properties).put("DISPLAY_NAME", username);
            ((Hashtable<String, String>)properties).put("FIRST_NAME", username);
            ((Hashtable<String, String>)properties).put("EMAIL_ADDRESS", email);
            ((Hashtable<String, String>)properties).put("DOMAIN_NETBIOS_NAME", domainName);
            ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", this.customerID);
            if (phoneNumber != null) {
                ((Hashtable<String, String>)properties).put("PHONE_NUMBER", phoneNumber);
            }
            ManagedUserHandler.getInstance().addOrUpdateManagedUser(properties);
        }
        catch (final SyMException ex) {
            Logger.getLogger(BaseManagedUserImportTask.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    private boolean isValidStr(final String username) {
        final Pattern pattern = Pattern.compile("^[^\\\\/:*?\\\"<>|{};]+$");
        final Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
    
    private void validatePhoneNumber(final String phoneNumber) throws Exception {
        final Iterator iterator = DBUtil.getRowsFromDB("ManagedUser", "PHONE_NUMBER", (Object)phoneNumber);
        if (iterator != null) {
            throw new SyMException(52103, "A user already exists for given phone number", "dc.mdm.enroll.change_user_email.error.phone_exists", (Throwable)null);
        }
        MDMEnrollmentUtil.getInstance();
        if (!MDMEnrollmentUtil.isValidPhone(phoneNumber)) {
            throw new SyMException(51014, "Enter a valid phone number", "dc.mdm.inv.valid_phone_number", (Throwable)null);
        }
    }
    
    private SelectQuery getCompleteUserDataQuery(final Long importID, final String domainName) {
        final SelectQuery derivedSelectQueryForPhone = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
        derivedSelectQueryForPhone.addSelectColumn(Column.getColumn("ManagedUser", "*"));
        final DerivedTable dtabForPhone = new DerivedTable("NewUserPhone", (Query)derivedSelectQueryForPhone);
        final SelectQuery derivedSelectQueryForUserName = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        derivedSelectQueryForUserName.addSelectColumn(Column.getColumn("Resource", "*"));
        derivedSelectQueryForUserName.setCriteria(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false));
        final DerivedTable dtabForUserName = new DerivedTable("NewUserResource", (Query)derivedSelectQueryForUserName);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserImportInfo"));
        selectQuery.addJoin(new Join("ManagedUserImportInfo", "ManagedUser", new Criteria(Column.getColumn("ManagedUserImportInfo", "EMAIL_ADDRESS"), (Object)Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), 0, false), 2));
        selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "Resource", 2));
        selectQuery.addJoin(new Join(Table.getTable("ManagedUserImportInfo"), (Table)dtabForUserName, new String[] { "USER_NAME" }, new String[] { "NAME" }, 1));
        selectQuery.addJoin(new Join("NewUserResource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, "NewUserResource", "NewUserDetails", 1));
        selectQuery.addJoin(new Join(Table.getTable("ManagedUserImportInfo"), (Table)dtabForPhone, new String[] { "PHONE_NUMBER" }, new String[] { "PHONE_NUMBER" }, 1));
        selectQuery.addJoin(new Join("NewUserPhone", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, "NewUserPhone", "NewUserPhoneManagedUser", 1));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUserImportInfo", "*"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "*"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "*"));
        selectQuery.addSelectColumn(Column.getColumn("NewUserDetails", "*"));
        selectQuery.addSelectColumn(Column.getColumn("NewUserPhoneManagedUser", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedUserImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 0)).and(new Criteria(Column.getColumn("ManagedUserImportInfo", "ERROR_REMARKS"), (Object)null, 0)).and(new Criteria(Column.getColumn("ManagedUserImportInfo", "MANAGED_USER_IMPORT_ID"), (Object)importID, 0)));
        return selectQuery;
    }
    
    public abstract void validateAndUpdateEmail(final String p0, final String p1, final boolean p2) throws Exception;
    
    protected JSONObject getInputs(final Properties taskProps) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put((Object)"send_invite", (Object)Boolean.parseBoolean(((Hashtable<K, String>)taskProps).get("send_invite")));
        return responseJSON;
    }
}
