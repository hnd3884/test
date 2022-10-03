package com.adventnet.sym.server.mdm.security;

import java.util.Hashtable;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.adventnet.iam.xss.IAMEncoder;
import java.util.Properties;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.common.MDMEventConstant;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.CommandUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.command.CommandStatusHandler;
import java.util.HashMap;
import com.me.mdm.server.android.message.ResetPasscodeTokenUpdator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ResetPasscodeHandler
{
    private Logger logger;
    public static final String NEW_PASSCODE = "NewPasscode";
    public static final String RESET_PASSCODE_TOKEN = "ResetPasscodeToken";
    private static ResetPasscodeHandler resetPasscode;
    public static final String MDM_DOMAIN_NAME = "MDM";
    
    public ResetPasscodeHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static ResetPasscodeHandler getInstance() {
        if (ResetPasscodeHandler.resetPasscode == null) {
            ResetPasscodeHandler.resetPasscode = new ResetPasscodeHandler();
        }
        return ResetPasscodeHandler.resetPasscode;
    }
    
    public boolean addorUpdateDevicePasscode(final JSONObject passcodeobj) {
        boolean success = true;
        try {
            final Long resourceID = (Long)passcodeobj.get("RESOURCE_ID");
            final String passcode = (String)passcodeobj.get("PASSCODE");
            final boolean sendEmail = (boolean)passcodeobj.get("EMAIL_SENT_TO_USER");
            final boolean adminEmail = (boolean)passcodeobj.get("EMAIL_SENT_TO_ADMIN");
            final Long userId = (Long)passcodeobj.get("UPDATED_BY");
            final Long updatedTime = (Long)passcodeobj.get("UPDATED_TIME");
            final Criteria resCriteria = new Criteria(Column.getColumn("MdDeviceResetPasscode", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject passcodeDO = MDMUtil.getPersistence().get("MdDeviceResetPasscode", resCriteria);
            if (passcodeDO.isEmpty()) {
                final Row row = new Row("MdDeviceResetPasscode");
                row.set("RESOURCE_ID", (Object)resourceID);
                row.set("PASSCODE", (Object)passcode);
                row.set("EMAIL_SENT_TO_USER", (Object)sendEmail);
                row.set("EMAIL_SENT_TO_ADMIN", (Object)adminEmail);
                row.set("UPDATED_BY", (Object)userId);
                row.set("UPDATED_TIME", (Object)updatedTime);
                passcodeDO.addRow(row);
                MDMUtil.getPersistence().add(passcodeDO);
            }
            else {
                final Row row = passcodeDO.getFirstRow("MdDeviceResetPasscode");
                row.set("PASSCODE", (Object)passcode);
                row.set("EMAIL_SENT_TO_USER", (Object)sendEmail);
                row.set("EMAIL_SENT_TO_ADMIN", (Object)adminEmail);
                row.set("UPDATED_BY", (Object)userId);
                row.set("UPDATED_TIME", (Object)updatedTime);
                passcodeDO.updateRow(row);
                MDMUtil.getPersistence().update(passcodeDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exceptin occurred while add or update device passcode. data :" + jsonObject);
            success = false;
        }
        return success;
    }
    
    public JSONObject getResetPasscodeRequestData(final Long resourceID) {
        final JSONObject resetPasscodeData = new JSONObject();
        String passcode = null;
        try {
            passcode = (String)DBUtil.getValueFromDB("MdDeviceResetPasscode", "RESOURCE_ID", (Object)resourceID, "PASSCODE");
            resetPasscodeData.put("NewPasscode", (Object)passcode);
            final String resetPasscodeToken = new ResetPasscodeTokenUpdator().getResetPasscodeToken(resourceID);
            resetPasscodeData.put("ResetPasscodeToken", (Object)resetPasscodeToken);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getResetPasscodeRequestData. res ID : " + resourceID + "Passcode = " + passcode, ex);
        }
        return resetPasscodeData;
    }
    
    public void handleResetPasscode(final HashMap resetPasscodeMap) {
        Long resourceID = null;
        Long customerID = null;
        try {
            int cmdStatus = 2;
            resourceID = resetPasscodeMap.get("RESOURCE_ID");
            customerID = resetPasscodeMap.get("CUSTOMER_ID");
            final JSONObject statusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, DeviceCommandRepository.getInstance().getCommandID("ResetPasscode"));
            final String newPasscode = resetPasscodeMap.get("PASSCODE");
            final boolean isPasscodeReset = resetPasscodeMap.get("isPasscodeReset");
            String actionLogRemarks = "dc.mdm.actionlog.securitycommands.success";
            final String commandDisplayName = CommandUtil.getInstance().getCommandDisplayName("ResetPasscode");
            final Object remarksArgs = commandDisplayName + "@@@" + ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
            if (newPasscode != null && !newPasscode.equals("")) {
                this.updatePasscodeForResource(resourceID, newPasscode);
                final boolean isPwdToUser = this.isSendEmailToUser(resourceID);
                final boolean isPwdToAdmin = this.isSendEmailToAdmin(resourceID);
                if (isPasscodeReset && isPwdToUser) {
                    this.sendEmail(resourceID);
                }
                if (isPasscodeReset && isPwdToAdmin) {
                    this.sendAdminEmail(resourceID, true);
                }
            }
            else if (newPasscode == null || !isPasscodeReset || newPasscode.equals("")) {
                this.sendAdminEmail(resourceID, false);
                actionLogRemarks = "dc.db.mdm.collection.failed_reset_passcode";
                cmdStatus = 0;
            }
            if (statusJSON.has("ADDED_BY")) {
                statusJSON.put("COMMAND_STATUS", cmdStatus);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, MDMEventConstant.DC_SYSTEM_USER, actionLogRemarks, remarksArgs, customerID);
                new CommandStatusHandler().populateCommandStatus(statusJSON);
            }
            getInstance().deletePasscodeForResource(resourceID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " Exception occurred in handleResetPasscode = " + resourceID, ex);
        }
    }
    
    private void updatePasscodeForResource(final Long resourceID, final String newPasscode) {
        try {
            final Criteria resCriteria = new Criteria(Column.getColumn("MdDeviceResetPasscode", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject pwdDO = MDMUtil.getPersistence().get("MdDeviceResetPasscode", resCriteria);
            if (!pwdDO.isEmpty()) {
                final Row pwdRow = pwdDO.getRow("MdDeviceResetPasscode");
                pwdRow.set("PASSCODE", (Object)newPasscode);
                pwdDO.updateRow(pwdRow);
                MDMUtil.getPersistence().update(pwdDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> " Exception occurred in updatePasscodeForResource resID = " + n);
        }
    }
    
    public void deletePasscodeForResource(final Long resourceID) {
        final Criteria resCriteria = new Criteria(Column.getColumn("MdDeviceResetPasscode", "RESOURCE_ID"), (Object)resourceID, 0);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceResetPasscode"));
        query.setCriteria(resCriteria);
        query.addSelectColumn(Column.getColumn("MdDeviceResetPasscode", "*"));
        try {
            final DataObject appDO = MDMUtil.getPersistence().get(query);
            appDO.deleteRows("MdDeviceResetPasscode", (Criteria)null);
            MDMUtil.getPersistence().update(appDO);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> " Exception occurred while deletePasscodeForResource res ID = " + n);
        }
    }
    
    public void sendEmail(final Long resourceID) {
        final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil(this.logger);
        final Properties prop = new Properties();
        try {
            final SelectQuery query = this.getResetPasswordUserQuery(resourceID);
            final DataObject passcodeDO = MDMUtil.getPersistence().get(query);
            final Row passcodeRow = passcodeDO.getRow("MdDeviceResetPasscode");
            final Row managedDeviceExtnRow = passcodeDO.getRow("ManagedDeviceExtn");
            final Row resourceRow = passcodeDO.getRow("Resource");
            final Row managedUserRow = passcodeDO.getRow("ManagedUser");
            final Row userResourceRow = passcodeDO.getRow("USERRESOURCE");
            ((Hashtable<String, Object>)prop).put("$user_name$", userResourceRow.get("NAME"));
            ((Hashtable<String, Object>)prop).put("$device_name$", managedDeviceExtnRow.get("NAME"));
            ((Hashtable<String, String>)prop).put("$passcode$", IAMEncoder.encodeHTML((String)passcodeRow.get("PASSCODE")));
            ((Hashtable<String, Object>)prop).put("$user_emailid$", managedUserRow.get("EMAIL_ADDRESS"));
            mailGenerator.sendMail(MDMAlertConstants.MDM_RESET_PASSCODE_USER, "MDM_RESET_PASSCODE_USER", (Long)resourceRow.get("CUSTOMER_ID"), prop);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exception occurred while send email to device user res ID : " + n);
        }
    }
    
    private void sendAdminEmail(final Long resourceID, final boolean isSuccess) {
        Long alertConstant = MDMAlertConstants.MDM_RESET_PASSCODE_ADMIN_SUCCESS;
        final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil(this.logger);
        final Properties prop = new Properties();
        try {
            final SelectQuery query = this.getResetPasswordUserQuery(resourceID);
            final DataObject passcodeDO = MDMUtil.getPersistence().get(query);
            final Row passcodeRow = passcodeDO.getRow("MdDeviceResetPasscode");
            final Row resourceRow = passcodeDO.getRow("Resource");
            final Row managedDeviceExtnRow = passcodeDO.getRow("ManagedDeviceExtn");
            final Row userResourceRow = passcodeDO.getRow("USERRESOURCE");
            final Long userId = (Long)passcodeRow.get("UPDATED_BY");
            final Properties contactProp = DMUserHandler.getContactInfoProp(userId);
            ((Hashtable<String, Object>)prop).put("$device_name$", managedDeviceExtnRow.get("NAME"));
            final String adminEmail = ((Hashtable<K, String>)contactProp).get("EMAIL_ID");
            ((Hashtable<String, Object>)prop).put("$user_name$", userResourceRow.get("NAME"));
            ((Hashtable<String, Boolean>)prop).put("appendFooter", true);
            if (adminEmail != null) {
                ((Hashtable<String, String>)prop).put("$user_emailid$", ((Hashtable<K, String>)contactProp).get("EMAIL_ID"));
                if (isSuccess) {
                    ((Hashtable<String, Object>)prop).put("$passcode$", passcodeRow.get("PASSCODE"));
                }
                else {
                    alertConstant = MDMAlertConstants.MDM_RESET_PASSCODE_ADMIN_FAILED;
                }
                mailGenerator.sendMail(alertConstant, "MDM_RESET_PASSCODE_ADMIN", (Long)resourceRow.get("CUSTOMER_ID"), prop);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exception occurred while send email to device user res ID : " + n);
        }
    }
    
    private SelectQuery getResetPasswordUserQuery(final Long resourceID) {
        SelectQuery query = null;
        try {
            query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceResetPasscode"));
            final Join resJoin = new Join("MdDeviceResetPasscode", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join managedDeviceExtnJoin = new Join("MdDeviceResetPasscode", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Join managedUserToDeviceJoin = new Join("Resource", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Join managedUserJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
            final Join userResourceJoin = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "USERRESOURCE", 2);
            query.addJoin(resJoin);
            query.addJoin(managedDeviceExtnJoin);
            query.addJoin(managedUserToDeviceJoin);
            query.addJoin(managedUserJoin);
            query.addJoin(userResourceJoin);
            final Criteria resCriteria = new Criteria(Column.getColumn("MdDeviceResetPasscode", "RESOURCE_ID"), (Object)resourceID, 0);
            query.setCriteria(resCriteria);
            query.addSelectColumn(Column.getColumn("MdDeviceResetPasscode", "*"));
            query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "*"));
            query.addSelectColumn(Column.getColumn("Resource", "*"));
            query.addSelectColumn(Column.getColumn("ManagedUser", "*"));
            query.addSelectColumn(Column.getColumn("USERRESOURCE", "*"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exception in getResetPasswordUserQuery : " + n);
        }
        return query;
    }
    
    public String getUserEmail(final Long resourceID) {
        String email = null;
        try {
            final Criteria resCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
            final Join managedUserJoin = new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
            final Join managedUserToDeviceJoin = new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            query.addJoin(managedUserJoin);
            query.addJoin(managedUserToDeviceJoin);
            query.setCriteria(resCriteria);
            query.addSelectColumn(Column.getColumn("ManagedUser", "*"));
            final DataObject emailDO = MDMUtil.getPersistence().get(query);
            if (!emailDO.isEmpty() && emailDO != null) {
                final Row managedUserRow = emailDO.getRow("ManagedUser");
                email = (String)managedUserRow.get("EMAIL_ADDRESS");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exception occurred while fetching email id of the user. resourceID : " + n);
        }
        return email;
    }
    
    public boolean isSendEmailToUser(final Long resourceID) {
        boolean isSendEmail = true;
        try {
            isSendEmail = (boolean)DBUtil.getValueFromDB("MdDeviceResetPasscode", "RESOURCE_ID", (Object)resourceID, "EMAIL_SENT_TO_USER");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exceptin occurred while check the email need to sent/not. resourceID=" + n);
        }
        return isSendEmail;
    }
    
    private boolean isSendEmailToAdmin(final Long resourceID) {
        boolean isSendEmail = true;
        try {
            isSendEmail = (boolean)DBUtil.getValueFromDB("MdDeviceResetPasscode", "RESOURCE_ID", (Object)resourceID, "EMAIL_SENT_TO_ADMIN");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exceptin occurred while check the email need to sent/not. resourceID=" + n);
        }
        return isSendEmail;
    }
    
    static {
        ResetPasscodeHandler.resetPasscode = null;
    }
}
