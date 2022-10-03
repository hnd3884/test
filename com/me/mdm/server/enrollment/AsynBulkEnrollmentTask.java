package com.me.mdm.server.enrollment;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.i18n.I18N;
import com.me.idps.core.util.ADSyncDataHandler;
import java.util.ArrayList;
import java.text.DateFormat;
import com.me.idps.core.util.NumberValidationUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Iterator;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.JSONObject;
import java.util.Properties;
import java.util.Hashtable;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public class AsynBulkEnrollmentTask extends CSVTask
{
    private static Logger logger;
    private final Boolean apnsUploaded;
    private final Boolean apnsExpired;
    private Boolean sendInvitation;
    private Boolean sendSMS;
    private Boolean sendEmail;
    private Integer authMode;
    private final int range = 25;
    List domainList;
    private final TreeMap domainListMap;
    private HashMap<String, List<String>> newUserEntriesMap;
    
    public AsynBulkEnrollmentTask() {
        this.domainList = DMDomainDataHandler.getInstance().getAllDMManagedProps(this.customerID);
        this.domainListMap = MDMEnrollmentUtil.getInstance().getDomainListAsTreeMap(this.domainList);
        this.newUserEntriesMap = new HashMap<String, List<String>>();
        final HashMap apnsCertificateDetails = (HashMap)APNsCertificateHandler.getAPNSCertificateDetails();
        if (apnsCertificateDetails.size() > 0) {
            this.apnsUploaded = Boolean.TRUE;
            Hashtable ht = null;
            try {
                ht = DateTimeUtil.determine_From_To_Times("today");
            }
            catch (final Exception e) {
                AsynBulkEnrollmentTask.logger.log(Level.SEVERE, "Exception while getting date: {0}", e);
            }
            if (ht != null && ht.get("date1") > apnsCertificateDetails.get("EXPIRY_DATE")) {
                this.apnsExpired = Boolean.TRUE;
            }
            else {
                this.apnsExpired = Boolean.FALSE;
            }
        }
        else {
            this.apnsUploaded = Boolean.FALSE;
            this.apnsExpired = Boolean.FALSE;
        }
    }
    
    protected JSONObject getInputs(final Properties taskProps) throws Exception {
        Boolean sendInvitation = false;
        final Long customerID = Long.parseLong(((Hashtable<K, String>)taskProps).get("customerID"));
        sendInvitation = Boolean.parseBoolean(((Hashtable<K, String>)taskProps).get("sendInvitation"));
        final Boolean sendSMS = Boolean.parseBoolean(((Hashtable<K, String>)taskProps).get("sendSMS"));
        Boolean sendEmail = Boolean.parseBoolean(((Hashtable<K, String>)taskProps).get("sendEmail"));
        if (!sendEmail && !sendSMS) {
            sendEmail = true;
        }
        final Long userID = Long.parseLong(((Hashtable<K, String>)taskProps).get("userID"));
        final JSONObject jsonObj = new JSONObject();
        jsonObj.put((Object)"sendInvitation", (Object)sendInvitation);
        jsonObj.put((Object)"sendSMS", (Object)sendSMS);
        jsonObj.put((Object)"sendEmail", (Object)sendEmail);
        AsynBulkEnrollmentTask.logger.log(Level.INFO, "In executeTask input json:{0}", jsonObj.toJSONString());
        return jsonObj;
    }
    
    protected void performOperation(final JSONObject jsonObj) throws Exception {
        this.sendInvitation = (Boolean)jsonObj.get((Object)"sendInvitation");
        this.sendSMS = (Boolean)jsonObj.get((Object)"sendSMS");
        this.sendEmail = (Boolean)jsonObj.get((Object)"sendEmail");
        this.sendMultipleEnrollmentRequests();
    }
    
    public void sendMultipleEnrollmentRequests() throws Exception {
        try {
            this.authMode = EnrollmentSettingsHandler.getInstance().getInvitationEnrollmentSettings(this.customerID).getInt("AUTH_MODE");
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("BulkEnrollmentImportInfo"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "BULK_ENROLLMENT_ID"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "USER_NAME"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "DOMAIN_NAME"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "EMAIL_ADDRESS"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "PLATFORM_TYPE"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "OWNED_BY"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "GROUP_NAME"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "UDID"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "CUSTOMER_ID"));
            sQuery.addSelectColumn(Column.getColumn("BulkEnrollmentImportInfo", "PHONE_NUMBER"));
            sQuery.setCriteria(new Criteria(Column.getColumn("BulkEnrollmentImportInfo", "ERROR_REMARKS"), (Object)null, 0).and(new Criteria(Column.getColumn("BulkEnrollmentImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0)));
            sQuery.setRange(new Range(0, 25));
            sQuery.addSortColumn(new SortColumn(Column.getColumn("BulkEnrollmentImportInfo", "BULK_ENROLLMENT_ID"), true));
            DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            final SelectQuery unifiedQuery = this.getUnifiedSelectQuery();
            unifiedQuery.setRange(new Range(0, 25));
            DataObject unifiedDObj = MDMUtil.getPersistence().get(unifiedQuery);
            int processed = 0;
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel("BulkEnroll"), (long)this.customerID);
            if (countStr != null) {
                processed = Integer.parseInt(countStr);
            }
            do {
                for (Row r = dobj.getRow("BulkEnrollmentImportInfo", new Criteria(Column.getColumn("BulkEnrollmentImportInfo", "ERROR_REMARKS"), (Object)null, 0)); r != null; r = dobj.getRow("BulkEnrollmentImportInfo", new Criteria(Column.getColumn("BulkEnrollmentImportInfo", "ERROR_REMARKS"), (Object)null, 0))) {
                    ++processed;
                    try {
                        this.sendSingleEnrollmentRequest(r, unifiedDObj);
                        dobj.deleteRow(r);
                    }
                    catch (final SyMException e) {
                        if (e.getErrorCode() == 13005) {
                            r.set("ERROR_REMARKS_ARGS", (Object)e.getMessage());
                            r.set("ERROR_REMARKS", (Object)"dc.mdm.msg.enroll.columns_cannot_be_null");
                        }
                        else if (e.getErrorCode() == 14012) {
                            r.set("ERROR_REMARKS_ARGS", (Object)e.getErrorKey());
                            r.set("ERROR_REMARKS", (Object)e.getMessage());
                        }
                        else {
                            r.set("ERROR_REMARKS", (Object)e.getMessage());
                        }
                        dobj.updateRow(r);
                    }
                    catch (final Exception e2) {
                        r.set("ERROR_REMARKS", (Object)"dc.mdm.enroll.UNABLE_TO_ADD_ENROLLMENT_REQUEST");
                        dobj.updateRow(r);
                    }
                }
                MDMUtil.getPersistence().update(dobj);
                CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel("BulkEnroll"), String.valueOf(processed), (long)this.customerID);
                dobj = MDMUtil.getPersistence().get(sQuery);
                unifiedDObj = MDMUtil.getPersistence().get(unifiedQuery);
                this.newUserEntriesMap = new HashMap<String, List<String>>();
            } while (!dobj.isEmpty());
            this.setFailureCount(this.customerID);
        }
        catch (final Exception ex) {
            AsynBulkEnrollmentTask.logger.log(Level.SEVERE, "Exception in sendMultipleEnrollmentRequests :{0}", ex);
            throw ex;
        }
    }
    
    private SelectQuery getUnifiedSelectQuery() {
        final SelectQuery unifiedQuery = (SelectQuery)new SelectQueryImpl(new Table("BulkEnrollmentImportInfo"));
        Join join = new Join("BulkEnrollmentImportInfo", "ManagedUser", new String[] { "EMAIL_ADDRESS" }, new String[] { "EMAIL_ADDRESS" }, 2);
        unifiedQuery.addJoin(join);
        join = new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        unifiedQuery.addJoin(join);
        join = new Join("DeviceEnrollmentRequest", "DEVICEENROLLREQTOSMS", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        unifiedQuery.addJoin(join);
        join = new Join("DEVICEENROLLREQTOSMS", "InvitationEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        unifiedQuery.addJoin(join);
        unifiedQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        unifiedQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"));
        unifiedQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"));
        unifiedQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"));
        unifiedQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"));
        unifiedQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUESTED_TIME"));
        unifiedQuery.addSelectColumn(Column.getColumn("DEVICEENROLLREQTOSMS", "ENROLLMENT_REQUEST_ID", "DEVICEENROLLREQTOSMS.ENROLLMENT_REQUEST_ID"));
        unifiedQuery.addSelectColumn(Column.getColumn("DEVICEENROLLREQTOSMS", "SMS_CODE"));
        unifiedQuery.addSelectColumn(Column.getColumn("DEVICEENROLLREQTOSMS", "PHONE_NUMBER"));
        unifiedQuery.addSelectColumn(Column.getColumn("InvitationEnrollmentRequest", "ENROLLMENT_REQUEST_ID", "INVITATIONENROLLMENTREQUEST.ENROLLMENT_REQUEST_ID"));
        unifiedQuery.addSelectColumn(Column.getColumn("InvitationEnrollmentRequest", "REGISTRATION_STATUS"));
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        Criteria criteria = new Criteria(Column.getColumn("BulkEnrollmentImportInfo", "ERROR_REMARKS"), (Object)null, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("BulkEnrollmentImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("DEVICEENROLLREQTOSMS", "SMS_CODE"), (Object)0, 0));
        unifiedQuery.setCriteria(criteria.and(userNotInTrashCriteria));
        unifiedQuery.addSortColumn(new SortColumn(Column.getColumn("BulkEnrollmentImportInfo", "BULK_ENROLLMENT_ID"), true));
        return unifiedQuery;
    }
    
    private Row getActiveRequestPresentInDO(final DataObject dataObject, final long managedUserID, final String phoneNumber, final int platformType) {
        try {
            if (dataObject.isEmpty()) {
                return null;
            }
            Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), (Object)managedUserID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platformType, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)1, 0));
            final Iterator iterator = dataObject.getRows("DeviceEnrollmentRequest", criteria);
            Row row = null;
            while (iterator.hasNext()) {
                row = iterator.next();
                criteria = new Criteria(Column.getColumn("DEVICEENROLLREQTOSMS", "ENROLLMENT_REQUEST_ID"), row.get("ENROLLMENT_REQUEST_ID"), 0);
                criteria = criteria.and(new Criteria(Column.getColumn("DEVICEENROLLREQTOSMS", "PHONE_NUMBER"), (Object)phoneNumber, 0));
                Row row2 = dataObject.getRow("DEVICEENROLLREQTOSMS", criteria);
                if (row2 != null) {
                    criteria = new Criteria(Column.getColumn("InvitationEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), row.get("ENROLLMENT_REQUEST_ID"), 0);
                    criteria = criteria.and(new Criteria(Column.getColumn("InvitationEnrollmentRequest", "REGISTRATION_STATUS"), (Object)3, 1));
                    row2 = dataObject.getRow("InvitationEnrollmentRequest", criteria);
                    if (row2 != null) {
                        return row;
                    }
                    continue;
                }
            }
        }
        catch (final Exception e) {
            AsynBulkEnrollmentTask.logger.log(Level.INFO, "Exception while getting Active Enrollment Request", e);
        }
        return null;
    }
    
    private void sendSingleEnrollmentRequest(final Row r, final DataObject unifiedDObj) throws Exception {
        try {
            AsynBulkEnrollmentTask.logger.log(Level.INFO, "Sending enrollment request for Row: {0} SendInvitation:{1} customerID:{2} userID:{3}", new Object[] { r, this.sendInvitation, this.customerID, this.userID });
            String columnList = "";
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS() && r.get("USER_NAME") == null) {
                if (columnList.equals("")) {
                    columnList = "USER_NAME";
                }
                else {
                    columnList += ", USER_NAME";
                }
            }
            if (r.get("EMAIL_ADDRESS") == null) {
                if (columnList.equals("")) {
                    columnList = "EMAIL_ADDRESS";
                }
                else {
                    columnList += ", EMAIL_ADDRESS";
                }
            }
            if (r.get("PLATFORM_TYPE") == null) {
                if (columnList.equals("")) {
                    columnList = "PLATFORM_TYPE";
                }
                else {
                    columnList += ", PLATFORM_TYPE";
                }
            }
            if (!columnList.equals("")) {
                throw new SyMException(13005, columnList, (Throwable)null);
            }
            final Properties properties = new Properties();
            ((Hashtable<String, Boolean>)properties).put("sendInvitation", this.sendInvitation);
            ((Hashtable<String, Boolean>)properties).put("sendSMS", this.sendSMS);
            ((Hashtable<String, Boolean>)properties).put("sendEmail", this.sendEmail);
            ((Hashtable<String, Boolean>)properties).put("isBulkEnroll", true);
            final String email = (String)r.get("EMAIL_ADDRESS");
            String userName = null;
            String domainName = null;
            String phoneNumber = "";
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                phoneNumber = (String)r.get("PHONE_NUMBER");
                if (!new MDMUtil().isValidEmail(email)) {
                    throw new SyMException(14003, "Invalid mail id", "dc.mdm.enroll.invalid_email", (Throwable)null);
                }
                final org.json.JSONObject json = new org.json.JSONObject();
                json.put("USER_IDENTIFIER", (Object)"EMAIL_ADDRESS");
                json.put("EMAIL_ADDRESS", (Object)email);
                json.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
                json.put("CUSTOMER_ID", (Object)this.customerID);
                final org.json.JSONObject userDetailsJson = ManagedUserHandler.getInstance().getManagedUserDetails(json);
                if (userDetailsJson != null && userDetailsJson.has("NAME")) {
                    userName = String.valueOf(userDetailsJson.get("NAME"));
                    domainName = String.valueOf(userDetailsJson.get("DOMAIN_NETBIOS_NAME"));
                }
                else {
                    if (r.get("USER_NAME") == null || ((String)r.get("USER_NAME")).isEmpty() || ((String)r.get("USER_NAME")).equalsIgnoreCase("--")) {
                        userName = ((String)r.get("EMAIL_ADDRESS")).split("@")[0];
                        domainName = (String)r.get("DOMAIN_NAME");
                    }
                    else {
                        userName = (String)r.get("USER_NAME");
                        domainName = (String)r.get("DOMAIN_NAME");
                    }
                    final org.json.JSONObject userjson = new org.json.JSONObject();
                    userjson.put("USER_IDENTIFIER", (Object)"NAME");
                    userjson.put("NAME", (Object)userName);
                    userjson.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
                    userjson.put("CUSTOMER_ID", (Object)this.customerID);
                    final org.json.JSONObject newuserDetailsJson = ManagedUserHandler.getInstance().getManagedUserDetails(userjson);
                    if (newuserDetailsJson != null && newuserDetailsJson.has("NAME")) {
                        final Object[] errorParams = { ProductUrlLoader.getInstance().getValue("mdmUrl") };
                        throw new SyMException(52103, "A user already exists for given email address", "dc.mdm.enroll.error.change_user_email_exists", errorParams, (Throwable)null);
                    }
                }
                if (phoneNumber != null) {
                    try {
                        phoneNumber = NumberValidationUtil.validateWithAutoFillCountryCode(phoneNumber)[0];
                        this.validatePhoneNumber(phoneNumber, email);
                    }
                    catch (final NumberFormatException e) {
                        throw new SyMException(51014, "Invalid ad user", "dc.mdm.inv.valid_phone_number", (Throwable)null);
                    }
                }
                if (this.sendSMS && phoneNumber != null && userDetailsJson != null && userDetailsJson.has("MANAGED_USER_ID")) {
                    final String platform = (String)r.get("PLATFORM_TYPE");
                    int platformType;
                    if (platform.equalsIgnoreCase("ios")) {
                        platformType = 1;
                    }
                    else if (platform.equalsIgnoreCase("android")) {
                        platformType = 2;
                    }
                    else if (platform.equalsIgnoreCase("windows")) {
                        platformType = 3;
                    }
                    else {
                        if (!platform.equalsIgnoreCase("neutral")) {
                            throw new SyMException(14004, "Invalid platform type", "dc.mdm.enroll.invalid_platform", (Throwable)null);
                        }
                        platformType = 0;
                    }
                    final String key = email + "_" + platformType;
                    final Row row = this.getActiveRequestPresentInDO(unifiedDObj, (long)userDetailsJson.get("MANAGED_USER_ID"), phoneNumber, platformType);
                    if (row != null) {
                        final long sentTime = (long)row.get("REQUESTED_TIME");
                        AsynBulkEnrollmentTask.logger.info("Enrollment request already sent via SMS on " + DateFormat.getDateInstance().format(sentTime));
                        throw new SyMException(14012, "dc.mdm.enroll.active_request_sms_sent_msg", DateFormat.getDateInstance().format(sentTime), (Throwable)null);
                    }
                    if (this.newUserEntriesMap.containsKey(key)) {
                        final List<String> phoneNumberList = this.newUserEntriesMap.get(key);
                        if (phoneNumberList.contains(phoneNumber)) {
                            final long sentTime2 = System.currentTimeMillis();
                            AsynBulkEnrollmentTask.logger.info("Enrollment request already sent via SMS on " + DateFormat.getDateInstance().format(sentTime2));
                            throw new SyMException(14012, "dc.mdm.enroll.active_request_sms_sent_msg", DateFormat.getDateInstance().format(sentTime2), (Throwable)null);
                        }
                        phoneNumberList.add(phoneNumber);
                    }
                    else {
                        final List<String> phoneNumberList = new ArrayList<String>();
                        phoneNumberList.add(phoneNumber);
                        this.newUserEntriesMap.put(key, phoneNumberList);
                    }
                }
            }
            else {
                userName = (String)r.get("USER_NAME");
                domainName = (String)r.get("DOMAIN_NAME");
            }
            Properties userProps = new Properties();
            if (!MDMUtil.isStringValid(domainName)) {
                domainName = "MDM";
            }
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                if (this.sendSMS && !this.sendEmail && phoneNumber == null) {
                    throw new SyMException(14002, "Phone Number should be provided to send SMS notification", "dc.mdm.enroll.phone_number_not_found", (Throwable)null);
                }
                if (phoneNumber != null) {
                    ((Hashtable<String, String>)properties).put("PHONE_NUMBER", phoneNumber);
                }
                ((Hashtable<String, String>)properties).put("DOMAIN_NETBIOS_NAME", (domainName == null || domainName.isEmpty()) ? "MDM" : domainName);
                ((Hashtable<String, Integer>)properties).put("AUTH_MODE", this.authMode);
            }
            else if (domainName.equalsIgnoreCase("MDM")) {
                ((Hashtable<String, String>)properties).put("DOMAIN_NETBIOS_NAME", domainName);
                ((Hashtable<String, Integer>)properties).put("AUTH_MODE", 1);
            }
            if (!domainName.equalsIgnoreCase("MDM")) {
                if (this.domainListMap == null || !this.domainListMap.containsValue(domainName.toUpperCase())) {
                    throw new SyMException(14002, "Invalid domain name", "dc.mdm.enroll.invalid_domain_name", (Throwable)null);
                }
                userProps = ADSyncDataHandler.getInstance().getDirUserProps(this.customerID, domainName, userName);
                if (userProps == null || userProps.isEmpty()) {
                    throw new SyMException(14010, I18N.getMsg("dc.mdm.enroll.invalid_user_name", (Object[])new String[] { userName, domainName }), "dc.mdm.enroll.invalid_user_name", (Throwable)null);
                }
                ((Hashtable<String, String>)properties).put("DOMAIN_NETBIOS_NAME", domainName);
                ((Hashtable<String, Integer>)properties).put("AUTH_MODE", this.authMode);
            }
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS() && !new MDMUtil().isValidEmail(email)) {
                throw new SyMException(14003, "Invalid E-mail", "dc.mdm.enroll.invalid_email", (Throwable)null);
            }
            ((Hashtable<String, String>)properties).put("NAME", userName);
            ((Hashtable<String, String>)properties).put("EMAIL_ADDRESS", email);
            domainName = (MDMStringUtils.isEmpty(domainName) ? "MDM" : domainName);
            if (DMDomainDataHandler.getInstance().isADManagedDomain(domainName, this.customerID) && userProps.isEmpty()) {
                final org.json.JSONObject userValidationJSON = new org.json.JSONObject();
                userValidationJSON.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
                userValidationJSON.put("NAME", (Object)userName);
                userValidationJSON.put("customerID", (Object)this.customerID);
                final org.json.JSONObject userDataJSON = MDMEnrollmentRequestHandler.getInstance().validateUserName(userValidationJSON);
                if (userDataJSON.has("USER_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("USER_NAME")))) {
                    ((Hashtable<String, String>)properties).put("NAME", String.valueOf(userDataJSON.get("USER_NAME")));
                }
                if (userDataJSON.has("EMAIL_ADDRESS") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("EMAIL_ADDRESS")))) {
                    ((Hashtable<String, String>)properties).put("EMAIL_ADDRESS", String.valueOf(userDataJSON.get("EMAIL_ADDRESS")));
                }
                if (userDataJSON.has("FIRST_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("FIRST_NAME")))) {
                    ((Hashtable<String, String>)properties).put("FIRST_NAME", String.valueOf(userDataJSON.get("FIRST_NAME")));
                }
                if (userDataJSON.has("MIDDLE_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("MIDDLE_NAME")))) {
                    ((Hashtable<String, String>)properties).put("MIDDLE_NAME", String.valueOf(userDataJSON.get("MIDDLE_NAME")));
                }
                if (userDataJSON.has("LAST_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("LAST_NAME")))) {
                    ((Hashtable<String, String>)properties).put("LAST_NAME", String.valueOf(userDataJSON.get("LAST_NAME")));
                }
                if (userDataJSON.has("DISPLAY_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("DISPLAY_NAME")))) {
                    ((Hashtable<String, String>)properties).put("DISPLAY_NAME", String.valueOf(userDataJSON.get("DISPLAY_NAME")));
                }
            }
            if (userProps != null && !userProps.isEmpty()) {
                properties.putAll(userProps);
            }
            final String platform2 = (String)r.get("PLATFORM_TYPE");
            if (platform2.equalsIgnoreCase("ios")) {
                if (!this.apnsUploaded) {
                    throw new SyMException(14007, "Apns not uploaded", "dc.mdm.enroll.anps_not_uploaded", (Throwable)null);
                }
                ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 1);
                if (this.apnsExpired) {
                    throw new SyMException(51016, "Apns expired", "dc.mdm.msg.apns_expired.title", (Throwable)null);
                }
                ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 1);
            }
            else if (platform2.equalsIgnoreCase("android")) {
                ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 2);
            }
            else if (platform2.equalsIgnoreCase("windows")) {
                ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 3);
            }
            else {
                if (!platform2.equalsIgnoreCase("unknown")) {
                    throw new SyMException(14004, "Invalid platform", "dc.mdm.enroll.invalid_platform", (Throwable)null);
                }
                ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 0);
            }
            final String sOwnedBy = (String)r.get("OWNED_BY");
            int iOwnedBy;
            if (sOwnedBy != null && !sOwnedBy.isEmpty()) {
                if (sOwnedBy.equalsIgnoreCase("corporate")) {
                    iOwnedBy = 1;
                }
                else {
                    if (!sOwnedBy.equalsIgnoreCase("personal")) {
                        throw new SyMException(14005, "Invalid owned by", "dc.mdm.enroll.invalid_owned_by", (Throwable)null);
                    }
                    iOwnedBy = 2;
                }
            }
            else {
                iOwnedBy = 1;
            }
            ((Hashtable<String, Integer>)properties).put("OWNED_BY", iOwnedBy);
            final String sGroupName = (String)r.get("GROUP_NAME");
            Long groupId = 0L;
            if (sGroupName != null && !sGroupName.isEmpty() && !sGroupName.equals("--")) {
                final int platformType2 = ((Hashtable<K, Integer>)properties).get("PLATFORM_TYPE");
                final JSONObject groupDetails = MDMGroupHandler.getCustomGroupDetails(sGroupName, this.userID, this.customerID);
                groupId = (Long)groupDetails.get((Object)"RESOURCE_ID");
            }
            ((Hashtable<String, Long>)properties).put("GROUP_RESOURCE_ID", groupId);
            ((Hashtable<String, Integer>)properties).put("ENROLLMENT_TYPE", 1);
            ((Hashtable<String, Boolean>)properties).put("IS_SELF_ENROLLMENT", Boolean.FALSE);
            ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", this.customerID);
            ((Hashtable<String, Long>)properties).put("USER_ID", this.userID);
            MDMEnrollmentRequestHandler.getInstance().sendEnrollmentRequest(properties);
        }
        catch (final SyMException e2) {
            AsynBulkEnrollmentTask.logger.log(Level.SEVERE, "Exception while sending enrollment request :{0}", e2.getMessage());
        }
        catch (final Exception ex) {
            AsynBulkEnrollmentTask.logger.log(Level.SEVERE, "Exception while sending enrollment request", ex);
            throw ex;
        }
    }
    
    private void setFailureCount(final long customerID) throws Exception {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("BulkEnrollmentImportInfo"));
            final Column countColumn = Column.getColumn("BulkEnrollmentImportInfo", "BULK_ENROLLMENT_ID").count();
            countColumn.setColumnAlias("BULK_ENROLLMENT_ID");
            sQuery.addSelectColumn(countColumn);
            sQuery.setCriteria(new Criteria(Column.getColumn("BulkEnrollmentImportInfo", "ERROR_REMARKS"), (Object)null, 1).and(new Criteria(Column.getColumn("BulkEnrollmentImportInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            ds.next();
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)CSVProcessor.getFailedLabel("BulkEnroll"), (Object)String.valueOf(ds.getValue("BULK_ENROLLMENT_ID")));
            jsonObj.put((Object)CSVProcessor.getStatusLabel("BulkEnroll"), (Object)"COMPLETED");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerID);
            long totalCount = 0L;
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getTotalLabel("BulkEnroll"), customerID);
            if (countStr != null) {
                totalCount = Long.parseLong(countStr);
            }
            final long failureCount = new Long(ds.getValue("BULK_ENROLLMENT_ID").toString());
            final long successCount = totalCount - failureCount;
            this.updateMETrackingParam(successCount);
            this.updateActionLog(successCount, failureCount);
            MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", customerID);
            AsynBulkEnrollmentTask.logger.info("Persisted failure count in Customer Params");
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void updateMETrackingParam(final long successCount) throws Exception {
        try {
            String bulkEnrollCount = (String)DBUtil.getValueFromDB("SystemParams", "PARAM_NAME", (Object)"Bulk_Enroll_Device_Count", "PARAM_VALUE");
            if (bulkEnrollCount == null) {
                bulkEnrollCount = String.valueOf(successCount);
            }
            else {
                bulkEnrollCount = String.valueOf(Long.parseLong(bulkEnrollCount) + successCount);
            }
            MDMUtil.updateSyMParameter("Bulk_Enroll_Device_Count", bulkEnrollCount);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void updateActionLog(final long successCount, final long failureCount) {
        final String sEventLogRemarks = "dc.mdm.actionlog.bulk_enrollment.mail_sent";
        final Object remarksArgs = successCount + "@@@" + failureCount;
        final Long loginId = DMUserHandler.getLoginIdForUserId(this.userID);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, DMUserHandler.getDCUser(loginId), sEventLogRemarks, remarksArgs, this.customerID);
        MDMOneLineLogger.log(Level.INFO, "SENT_ENROLLMENT_REQUEST", "mail-delivery-success");
    }
    
    private void validatePhoneNumber(final String phoneNumber, final String email) throws Exception {
        final SelectQuery selectQueryForEmail = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
        final Criteria phoneNumberCri = new Criteria(Column.getColumn("ManagedUser", "PHONE_NUMBER"), (Object)phoneNumber, 0);
        final Criteria emailCri = new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)email, 1);
        selectQueryForEmail.setCriteria(phoneNumberCri.and(emailCri));
        selectQueryForEmail.addSelectColumn(Column.getColumn("ManagedUser", "*"));
        final DataObject existingUsersWithEmail = SyMUtil.getPersistence().get(selectQueryForEmail);
        if (existingUsersWithEmail.size("ManagedUser") > 0) {
            throw new SyMException(52103, "A user already exists for given phone number", "dc.mdm.enroll.change_user_email.error.phone_exists", (Throwable)null);
        }
        MDMEnrollmentUtil.getInstance();
        if (!MDMEnrollmentUtil.isValidPhone(phoneNumber)) {
            throw new SyMException(51014, "Enter a valid phone number", "dc.mdm.inv.valid_phone_number", (Throwable)null);
        }
    }
    
    static {
        AsynBulkEnrollmentTask.logger = Logger.getLogger("MDMEnrollment");
    }
}
