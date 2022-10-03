package com.me.ems.onpremise.uac.core;

import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.SecureRandom;
import com.adventnet.persistence.DataAccess;
import java.util.Map;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Persistence;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class TFAUtil
{
    private static Logger logger;
    public static final long COMPLETE_DAY_TIME = 86400000L;
    public static final String IS_TFA_TO_BE_ENABLED = "isTFAToBeEnabled";
    public static final String TFA_DISABLE_EXPIRY = "TFADisableExpiry";
    public static final String IS_TFA_EXTENDED = "isTFAExtended";
    public static final String IS_TFA_PERMANENT_DISABLE = "isTfaPermanentDisable";
    
    public static String getTwoFactorAuthType() throws Exception {
        String authType = "disabled";
        final Criteria twoFactorParamsCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"authType", 0);
        final DataObject twoFactorParamDO = SyMUtil.getPersistence().get("UserMgmtParams", twoFactorParamsCri);
        if (!twoFactorParamDO.isEmpty()) {
            final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("UserMgmtParams");
            authType = (String)twoFactorParamRow.get("PARAMS_VALUE");
            TFAUtil.logger.log(Level.FINE, "Enabled TwoFactor Type:" + authType);
        }
        return authType;
    }
    
    public static int getOTPValidity() throws Exception {
        final Criteria twoFactorParamsCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"otp", 0);
        final DataObject twoFactorParamDO = SyMUtil.getPersistence().get("UserMgmtParams", twoFactorParamsCri);
        if (!twoFactorParamDO.isEmpty()) {
            final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("UserMgmtParams");
            return Integer.parseInt((String)twoFactorParamRow.get("PARAMS_VALUE"));
        }
        return 0;
    }
    
    public static void setTwoFactorAuth() throws APIException {
        try {
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
                System.setProperty("2factor.auth", "com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword");
            }
        }
        catch (final DataAccessException e) {
            TFAUtil.logger.log(Level.SEVERE, "Exception occurred in setTwoFactAuth ", (Throwable)e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "Internal Server Error");
        }
    }
    
    public static Response enableTwoFactor(String authType, final String otpValidity, final String userName) throws APIException {
        try {
            final Persistence per = SyMUtil.getPersistence();
            Criteria twoFactorCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"authType", 0);
            DataObject twoFactorParamDO = per.get("UserMgmtParams", twoFactorCri);
            if (twoFactorParamDO.isEmpty()) {
                final Row twoFactorParamRow = new Row("UserMgmtParams");
                twoFactorParamRow.set("PARAMS_NAME", (Object)"authType");
                twoFactorParamRow.set("PARAMS_VALUE", (Object)authType);
                twoFactorParamDO.addRow(twoFactorParamRow);
            }
            else {
                final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("UserMgmtParams");
                twoFactorParamRow.set("PARAMS_VALUE", (Object)authType);
                twoFactorParamDO.updateRow(twoFactorParamRow);
            }
            per.update(twoFactorParamDO);
            twoFactorCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"otp", 0);
            twoFactorParamDO = per.get("UserMgmtParams", twoFactorCri);
            if (twoFactorParamDO.isEmpty()) {
                final Row twoFactorParamRow = new Row("UserMgmtParams");
                twoFactorParamRow.set("PARAMS_NAME", (Object)"otp");
                twoFactorParamRow.set("PARAMS_VALUE", (Object)otpValidity);
                twoFactorParamDO.addRow(twoFactorParamRow);
            }
            else {
                final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("UserMgmtParams");
                twoFactorParamRow.set("PARAMS_VALUE", (Object)otpValidity);
                twoFactorParamDO.updateRow(twoFactorParamRow);
            }
            per.update(twoFactorParamDO);
            final Criteria googleAuthCri = new Criteria(Column.getColumn("GoogleAuthentication", "FIRST_LOGIN_STATUS"), (Object)true, 0);
            per.delete(googleAuthCri);
            UserMgmtUtil.deleteUserMgmtParameter(new String[] { "TFADisableExpiry", "isTFAExtended", "isTfaPermanentDisable" });
            SecurityUtil.deleteSecurityParameters(new String[] { "TFADisablingKey" });
            if (authType.equals("mail")) {
                authType = I18N.getMsg("dc.tfa.email", new Object[0]);
            }
            else {
                authType = I18N.getMsg("dc.tfa.googleauth", new Object[0]);
            }
            ApiFactoryProvider.getCacheAccessAPI().putCache("isTFAToBeEnabled", (Object)false);
            MessageProvider.getInstance().hideMessage("TFA_ENABLING_ALERT");
            authType = authType + "@@@" + otpValidity;
            DCEventLogUtil.getInstance().addEvent(715, userName, (HashMap)null, "dc.tfa.eventlog.mode", (Object)authType, true);
            TFAUtil.logger.log(Level.INFO, I18N.getMsg("dc.tfa.eventlog.mode", new Object[] { authType }));
            return Response.ok().build();
        }
        catch (final Exception exception) {
            TFAUtil.logger.log(Level.SEVERE, "Caught exception while enabling TwoFactorAuthentication", exception);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "Internal Server Error");
        }
    }
    
    public static boolean isMailNotProvided() throws Exception {
        boolean MAIL_NOT_PROVIDED = true;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
        query.addJoin(new Join("AaaLogin", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        query.addJoin(new Join("AaaUserStatus", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        query.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
        query.addSelectColumn(new Column("AaaLogin", "USER_ID"));
        query.addSelectColumn(new Column("AaaUserStatus", "USER_ID"));
        query.addSelectColumn(new Column("AaaUserStatus", "STATUS"));
        query.addSelectColumn(new Column("AaaUserContactInfo", "CONTACTINFO_ID"));
        query.addSelectColumn(new Column("AaaUserContactInfo", "USER_ID"));
        query.addSelectColumn(new Column("AaaContactInfo", "CONTACTINFO_ID"));
        query.addSelectColumn(new Column("AaaContactInfo", "EMAILID"));
        Criteria emailCriteria = new Criteria(new Column("AaaContactInfo", "EMAILID"), (Object)"", 0);
        emailCriteria = emailCriteria.and(new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 12));
        query.setCriteria(emailCriteria);
        final DataObject emailDO = SyMUtil.getPersistence().get(query);
        if (emailDO.isEmpty()) {
            MAIL_NOT_PROVIDED = false;
        }
        else {
            final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
            final JSONObject userAccountJsonObject = (JSONObject)frameworkConfigurations.get("user_account_handling");
            if (userAccountJsonObject != null && userAccountJsonObject.get("enable_tfa_plugin_users").equals("true")) {
                MAIL_NOT_PROVIDED = true;
            }
            else {
                final int dcUserSize = DBUtil.getDOSize(emailDO, "AaaContactInfo");
                final SelectQuery sdpUserQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCAaaLogin"));
                sdpUserQuery.addJoin(new Join("DCAaaLogin", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
                sdpUserQuery.addJoin(new Join("AaaLogin", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
                sdpUserQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
                sdpUserQuery.addSelectColumn(new Column("DCAaaLogin", "LOGIN_ID"));
                sdpUserQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
                sdpUserQuery.addSelectColumn(new Column("AaaLogin", "USER_ID"));
                sdpUserQuery.addSelectColumn(new Column("AaaContactInfo", "CONTACTINFO_ID"));
                sdpUserQuery.addSelectColumn(new Column("AaaContactInfo", "EMAILID"));
                sdpUserQuery.addSelectColumn(new Column("AaaUserContactInfo", "CONTACTINFO_ID"));
                sdpUserQuery.addSelectColumn(new Column("AaaUserContactInfo", "USER_ID"));
                emailCriteria = new Criteria(new Column("AaaContactInfo", "EMAILID"), (Object)"", 0);
                sdpUserQuery.setCriteria(emailCriteria);
                final DataObject sdpUserDO = SyMUtil.getPersistence().get(sdpUserQuery);
                final int sdpUserSize = DBUtil.getDOSize(sdpUserDO, "AaaContactInfo");
                MAIL_NOT_PROVIDED = (dcUserSize != sdpUserSize);
            }
        }
        return MAIL_NOT_PROVIDED;
    }
    
    public static boolean isTwoFactorEnabled() throws Exception {
        boolean isTwoFactorEnabled = false;
        final String authType = getTwoFactorAuthType();
        if (authType.equalsIgnoreCase("mail") || authType.equalsIgnoreCase("googleApp")) {
            isTwoFactorEnabled = true;
        }
        return isTwoFactorEnabled;
    }
    
    public static Integer getTFAExpiryInDays() throws Exception {
        final String tfaExpiryParam = UserMgmtUtil.getUserMgmtParameter("TFADisableExpiry");
        if (tfaExpiryParam != null) {
            return calculateTFAExpiryInDays(Long.parseLong(tfaExpiryParam));
        }
        return 0;
    }
    
    public static Integer calculateTFAExpiryInDays(Long tfaExpiryDate) throws Exception {
        tfaExpiryDate = (tfaExpiryDate - System.currentTimeMillis()) / 86400000L;
        return (tfaExpiryDate > 0L) ? tfaExpiryDate.intValue() : 0;
    }
    
    public static boolean isTFAToBeEnabled() {
        boolean isTFAToBeEnabled = false;
        try {
            final Map tfaDbData = UserMgmtUtil.getUserMgmtParams((Object[])new String[] { "isTfaPermanentDisable", "TFADisableExpiry" });
            final boolean isPermanentlyDisabled = Boolean.valueOf(tfaDbData.getOrDefault("isTfaPermanentDisable", "false"));
            if (!LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T") && !isTwoFactorEnabled() && !ApiFactoryProvider.getDemoUtilAPI().isDemoMode() && isTFAEnforcementEnabled() && !isPermanentlyDisabled) {
                final int tfaExpiryInDays = calculateTFAExpiryInDays(Long.parseLong(tfaDbData.getOrDefault("TFADisableExpiry", "0")));
                if (tfaExpiryInDays <= 0) {
                    isTFAToBeEnabled = true;
                }
            }
        }
        catch (final Exception ex) {
            TFAUtil.logger.log(Level.INFO, "Exception in isTFAToBeEnabled", ex);
        }
        return isTFAToBeEnabled;
    }
    
    public static void setTFAHomePageAlertMessage() {
        try {
            if (LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T") && !isTwoFactorEnabled()) {
                MessageProvider.getInstance().unhideMessage("TFA_ENABLING_ALERT");
            }
            else {
                MessageProvider.getInstance().hideMessage("TFA_ENABLING_ALERT");
            }
        }
        catch (final Exception exception) {
            TFAUtil.logger.log(Level.INFO, "Exception in setTFAHomePageAlertMessage", exception);
        }
    }
    
    public static void updateTFAExpiryDate() {
        try {
            final String isPermanentlyDisabled = UserMgmtUtil.getUserMgmtParameter("isTfaPermanentDisable");
            if (!isTwoFactorEnabled() && isTFAEnforcementEnabled() && !Boolean.valueOf(isPermanentlyDisabled)) {
                final Column col = Column.getColumn("UserMgmtParams", "PARAMS_NAME");
                final Criteria criteria = new Criteria(col, (Object)"TFADisableExpiry", 0, false);
                final DataObject twoFactorParamDO = DataAccess.get("UserMgmtParams", criteria);
                Row twoFactorParamRow = twoFactorParamDO.getRow("UserMgmtParams");
                if (twoFactorParamDO.isEmpty()) {
                    twoFactorParamRow = new Row("UserMgmtParams");
                    twoFactorParamRow.set("PARAMS_NAME", (Object)"TFADisableExpiry");
                    twoFactorParamRow.set("PARAMS_VALUE", (Object)(System.currentTimeMillis() + 864000000L));
                    twoFactorParamDO.addRow(twoFactorParamRow);
                    DataAccess.add(twoFactorParamDO);
                }
                else if (calculateTFAExpiryInDays(Long.parseLong((String)twoFactorParamRow.get("PARAMS_VALUE"))) < 10) {
                    twoFactorParamRow.set("PARAMS_VALUE", (Object)(System.currentTimeMillis() + 864000000L));
                    twoFactorParamDO.updateRow(twoFactorParamRow);
                    DataAccess.update(twoFactorParamDO);
                }
            }
        }
        catch (final Exception ex) {
            TFAUtil.logger.log(Level.INFO, "Exception in updateTFAExpiryDate()", ex);
        }
    }
    
    public static void insertTFAExpiryDate() {
        try {
            final String isPermanentlyDisabled = UserMgmtUtil.getUserMgmtParameter("isTfaPermanentDisable");
            if (!isTwoFactorEnabled() && isTFAEnforcementEnabled() && !Boolean.valueOf(isPermanentlyDisabled)) {
                final Column col = Column.getColumn("UserMgmtParams", "PARAMS_NAME");
                final Criteria criteria = new Criteria(col, (Object)"TFADisableExpiry", 0, false);
                final DataObject twoFactorParamDO = DataAccess.get("UserMgmtParams", criteria);
                if (twoFactorParamDO.isEmpty()) {
                    final Row twoFactorParamRow = new Row("UserMgmtParams");
                    twoFactorParamRow.set("PARAMS_NAME", (Object)"TFADisableExpiry");
                    twoFactorParamRow.set("PARAMS_VALUE", (Object)(System.currentTimeMillis() + 864000000L));
                    twoFactorParamDO.addRow(twoFactorParamRow);
                    DataAccess.add(twoFactorParamDO);
                }
            }
        }
        catch (final Exception ex) {
            TFAUtil.logger.log(Level.INFO, "Exception in insertTFAExpiryDate()", ex);
        }
    }
    
    public static void disableTwoFactorAuthentication(final String userName, final Map dataMap) throws Exception {
        try {
            final Persistence persistence = SyMUtil.getPersistence();
            UserMgmtUtil.addOrUpdateUserMgmtParameters(dataMap);
            UserMgmtUtil.deleteUserMgmtParameter(new String[] { "TFADisableExpiry", "isTFAExtended" });
            final Criteria googleAuthCri = new Criteria(Column.getColumn("GoogleAuthentication", "FIRST_LOGIN_STATUS"), (Object)true, 0);
            persistence.delete(googleAuthCri);
            TFAUtil.logger.log(Level.INFO, I18N.getMsg("dc.tfa.eventlog.disabled", new Object[0]));
            DCEventLogUtil.getInstance().addEvent(715, userName, (HashMap)null, "dc.tfa.eventlog.disabled", (Object)null, true);
        }
        catch (final Exception exception) {
            TFAUtil.logger.log(Level.SEVERE, "disableTwoFactor():- Caught exception while disabling TwoFactorAuthentication", exception);
            throw exception;
        }
    }
    
    public static void updateTwoFactorAuthDisabledDetails() throws Exception {
        try {
            final UpdateQueryImpl updateTwoFactor = new UpdateQueryImpl("AaaUserTwoFactorDetails");
            updateTwoFactor.setUpdateColumn("ENABLED", (Object)false);
            SyMUtil.getPersistence().update((UpdateQuery)updateTwoFactor);
        }
        catch (final Exception exception) {
            TFAUtil.logger.log(Level.SEVERE, "UpdateTwoFactorDisabledDetails():- Exception in updating TwoFactorDisabledDetails", exception);
            throw exception;
        }
    }
    
    public static boolean isSupportContacted() throws Exception {
        boolean isSupportContacted = false;
        if (SecurityUtil.getSecurityParameter("TFADisablingKey") != null) {
            isSupportContacted = true;
        }
        return isSupportContacted;
    }
    
    public static boolean isTFAEnforcementEnabled() throws Exception {
        boolean isTFAEnforcementEnabled = false;
        final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
        final JSONObject userAccountJsonObject = (JSONObject)frameworkConfigurations.get("user_account_handling");
        if (userAccountJsonObject != null && userAccountJsonObject.get("enable_tfa_enforcement").equals("true")) {
            isTFAEnforcementEnabled = true;
        }
        return isTFAEnforcementEnabled;
    }
    
    private static String getSaltString() {
        final String saltChars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
        final StringBuilder salt = new StringBuilder();
        final SecureRandom secureRandom = new SecureRandom();
        while (salt.length() <= 7) {
            final int index = (int)(secureRandom.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        final String saltStr = salt.toString();
        return saltStr;
    }
    
    public static String getTFADisableKey() throws Exception {
        try {
            final String inputStr = getSaltString() + System.currentTimeMillis();
            return SecurityUtil.getSHA256HashFromInputStream((InputStream)new ByteArrayInputStream(inputStr.getBytes()));
        }
        catch (final Exception exception) {
            TFAUtil.logger.log(Level.SEVERE, "getTFADisableKey():- " + exception);
            throw exception;
        }
    }
    
    public static boolean isTFABannerEnabled() {
        boolean tfaEnabledStatus = false;
        try {
            if (!TwoFactorAction.isTwoFactorEnabledGlobaly()) {
                final Integer tfaStatus = ApiFactoryProvider.getTFAService().getTFAEnforcementDetails().get("tfaStatus");
                tfaEnabledStatus = tfaStatus.equals(1);
            }
        }
        catch (final Exception e) {
            TFAUtil.logger.log(Level.WARNING, "Exception while getting the TFA Banner details!", e);
        }
        return tfaEnabledStatus;
    }
    
    static {
        TFAUtil.logger = Logger.getLogger("UserManagementLogger");
    }
}
