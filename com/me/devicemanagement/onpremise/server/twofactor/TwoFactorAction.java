package com.me.devicemanagement.onpremise.server.twofactor;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.alerts.DCEMailAlertsHandler;
import org.json.JSONObject;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class TwoFactorAction
{
    private static Logger logger;
    
    public static String getUserName(final Long userId) {
        String userName = "";
        try {
            final Criteria twoFactorParamsCri = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)userId, 0);
            final DataObject twoFactorParamDO = SyMUtil.getPersistence().get("AaaUser", twoFactorParamsCri);
            if (!twoFactorParamDO.isEmpty()) {
                final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("AaaUser");
                final String TwoFactorUser = userName = (String)twoFactorParamRow.get("FIRST_NAME");
            }
        }
        catch (final Exception e) {
            TwoFactorAction.logger.log(Level.SEVERE, "Exception in fetching userName from userID " + e);
        }
        return userName;
    }
    
    public static String getEmailId(final Long userId) {
        final DataObject dataObject = DMUserHandler.getContactInfoDO(userId);
        String userEmail = "";
        try {
            if (!dataObject.isEmpty()) {
                final Iterator aaaContactInfoIter = dataObject.getRows("AaaContactInfo");
                final Row aaaContactInfoRow = aaaContactInfoIter.next();
                userEmail = (String)aaaContactInfoRow.get("EMAILID");
            }
        }
        catch (final Exception ex) {
            TwoFactorAction.logger.log(Level.SEVERE, "Exception in getting EmailId from UserID" + ex);
        }
        return userEmail;
    }
    
    public static void sendEmailInvitation(final Long userId) throws Exception {
        try {
            final String strToAddress = getEmailId(userId);
            final JSONObject obj = new JSONObject();
            obj.put("userID", (Object)userId);
            obj.put("Email", (Object)strToAddress);
            obj.put("TaskID", (Object)"googleAuthentication");
            DCEMailAlertsHandler.getInstance().sendEMailAlerts("google-authentication-alert", "googleAuthentication", obj);
            TwoFactorAction.logger.log(Level.INFO, "Email Invitation sent successfully for " + strToAddress);
        }
        catch (final Exception e) {
            TwoFactorAction.logger.log(Level.WARNING, "Exception while sending e-mail invitation :", e);
        }
    }
    
    @Deprecated
    public static String getTwoFactorAuthType() {
        String authType = "disabled";
        try {
            final Criteria twoFactorParamsCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"authType", 0);
            final DataObject twoFactorParamDO = SyMUtil.getPersistence().get("UserMgmtParams", twoFactorParamsCri);
            if (!twoFactorParamDO.isEmpty()) {
                final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("UserMgmtParams");
                authType = (String)twoFactorParamRow.get("PARAMS_VALUE");
                TwoFactorAction.logger.log(Level.FINE, "Enabled TwoFactor Type:" + authType);
            }
        }
        catch (final Exception e) {
            TwoFactorAction.logger.log(Level.SEVERE, "Exception in fetching UserMgmtParams data: " + e);
        }
        return authType;
    }
    
    public static int getOtpTimeout() {
        int timeOut = 0;
        try {
            final Criteria twoFactorParamCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"otp", 0);
            final DataObject twofactorParamDO = SyMUtil.getPersistence().get("UserMgmtParams", twoFactorParamCri);
            if (!twofactorParamDO.isEmpty()) {
                final Row twoFactorParamRow = twofactorParamDO.getFirstRow("UserMgmtParams");
                final String timeString = (String)twoFactorParamRow.get("PARAMS_VALUE");
                timeOut = Integer.parseInt(timeString);
                TwoFactorAction.logger.log(Level.FINE, "timeOut fetched from UserMgmtParams");
            }
        }
        catch (final Exception e) {
            TwoFactorAction.logger.log(Level.SEVERE, "Exception in fetching timeOut" + e);
        }
        return timeOut;
    }
    
    public static void UpdateTwoFactorDiabledDetails() {
        try {
            final UpdateQueryImpl updateTwoFactor = new UpdateQueryImpl("AaaUserTwoFactorDetails");
            updateTwoFactor.setUpdateColumn("ENABLED", (Object)false);
            SyMUtil.getPersistence().update((UpdateQuery)updateTwoFactor);
        }
        catch (final Exception e) {
            TwoFactorAction.logger.log(Level.SEVERE, "Exception in updating TwoFactorDisabledDetails" + e);
        }
    }
    
    public static void setTwoFactorAuth() throws Exception {
        final UpdateQueryImpl updateTwoFactor = new UpdateQueryImpl("AaaUserTwoFactorDetails");
        updateTwoFactor.setUpdateColumn("ENABLED", (Object)true);
        SyMUtil.getPersistence().update((UpdateQuery)updateTwoFactor);
        final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
        final JSONObject userAccountJsonObject = (JSONObject)frameworkConfigurations.get("user_account_handling");
        if (userAccountJsonObject != null && userAccountJsonObject.get("enable_tfa_plugin_users").equals("false")) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUserTwoFactorDetails"));
            selectQuery.addSelectColumn(new Column("AaaUserTwoFactorDetails", "USER_ID"));
            selectQuery.addSelectColumn(new Column("AaaUserTwoFactorDetails", "ENABLED"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "USER_ID"));
            selectQuery.addSelectColumn(new Column("DCAaaLogin", "LOGIN_ID"));
            selectQuery.addJoin(new Join("AaaUserTwoFactorDetails", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaLogin", "DCAaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            final DataObject aaaloginDO = SyMUtil.getPersistence().get(selectQuery);
            if (!aaaloginDO.isEmpty()) {
                final Iterator aaauserIter = aaaloginDO.getRows("AaaUserTwoFactorDetails");
                while (aaauserIter.hasNext()) {
                    final Row rowList = aaauserIter.next();
                    rowList.set("ENABLED", (Object)false);
                    aaaloginDO.updateRow(rowList);
                }
            }
            SyMUtil.getPersistence().update(aaaloginDO);
        }
        System.setProperty("2factor.auth", "com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword");
    }
    
    public static boolean isTwoFactorEnabledGlobaly() {
        boolean isTwoFactorEnabled = false;
        final String authType = getTwoFactorAuthType();
        if (authType.equalsIgnoreCase("mail") || authType.equalsIgnoreCase("googleApp")) {
            isTwoFactorEnabled = true;
        }
        return isTwoFactorEnabled;
    }
    
    static {
        TwoFactorAction.logger = Logger.getLogger("UserManagementLogger");
    }
}
