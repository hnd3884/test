package com.me.mdm.onpremise.server.user;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import org.json.JSONObject;
import java.util.logging.Logger;

public class TwoFactorAuthenticationHandler
{
    private static TwoFactorAuthenticationHandler twoFactorAuthenticationHandler;
    private Logger logger;
    
    public TwoFactorAuthenticationHandler() {
        this.logger = Logger.getLogger("UserManagementLogger");
    }
    
    public static TwoFactorAuthenticationHandler getInstance() {
        if (TwoFactorAuthenticationHandler.twoFactorAuthenticationHandler == null) {
            TwoFactorAuthenticationHandler.twoFactorAuthenticationHandler = new TwoFactorAuthenticationHandler();
        }
        return TwoFactorAuthenticationHandler.twoFactorAuthenticationHandler;
    }
    
    public JSONObject getTFADetails() throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final String authType = TwoFactorAction.getTwoFactorAuthType();
        if (!authType.equalsIgnoreCase("disabled")) {
            final String otp = UserMgmtUtil.getUserMgmtParameter("otp");
            responseJSON.put("is_tfa_enabled", true);
            responseJSON.put("auth_type", (Object)authType);
            responseJSON.put("otp", (Object)otp);
        }
        else {
            responseJSON.put("is_tfa_enabled", false);
        }
        return responseJSON;
    }
    
    public JSONObject addOrUpdateTFADetails(final JSONObject requestJSON) throws Exception {
        final boolean enableTFA = requestJSON.optBoolean("is_tfa_enabled", false);
        JSONObject responseJSON = new JSONObject();
        if (enableTFA) {
            final String inputAuth = requestJSON.optString("auth_type");
            final String authType = TwoFactorAction.getTwoFactorAuthType();
            final int inputTimeout = requestJSON.optInt("otp", 0);
            final int currTimeout = TwoFactorAction.getOtpTimeout();
            if (!inputAuth.equalsIgnoreCase(authType) || inputTimeout != currTimeout || authType.equalsIgnoreCase("disabled")) {
                TwoFactorAction.setTwoFactorAuth();
                responseJSON = this.enableTFA(requestJSON);
                responseJSON.put("is_tfa_enabled", true);
                responseJSON.put("state_change", true);
            }
        }
        else {
            final String authType2 = TwoFactorAction.getTwoFactorAuthType();
            if (!authType2.equalsIgnoreCase("disabled")) {
                TwoFactorAction.UpdateTwoFactorDiabledDetails();
                this.disableTFA();
                responseJSON.put("is_tfa_enabled", false);
                responseJSON.put("state_change", true);
            }
        }
        return responseJSON;
    }
    
    private void disableTFA() throws Exception {
        try {
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            Criteria twoFactorParamCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"authType", 0);
            DataObject twoFactorParamDO = MDMUtil.getPersistence().get("UserMgmtParams", twoFactorParamCri);
            if (!twoFactorParamDO.isEmpty()) {
                final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("UserMgmtParams");
                twoFactorParamRow.set("PARAMS_VALUE", (Object)"disabled");
                twoFactorParamDO.updateRow(twoFactorParamRow);
            }
            MDMUtil.getPersistence().update(twoFactorParamDO);
            twoFactorParamCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"otp", 0);
            twoFactorParamDO = MDMUtil.getPersistence().get("UserMgmtParams", twoFactorParamCri);
            if (!twoFactorParamDO.isEmpty()) {
                final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("UserMgmtParams");
                twoFactorParamRow.set("PARAMS_VALUE", (Object)"0");
                twoFactorParamDO.updateRow(twoFactorParamRow);
            }
            MDMUtil.getPersistence().update(twoFactorParamDO);
            final Criteria googleAuthCri = new Criteria(Column.getColumn("GoogleAuthentication", "FIRST_LOGIN_STATUS"), (Object)true, 0);
            MDMUtil.getPersistence().delete(googleAuthCri);
            this.logger.log(Level.INFO, "Two Factor Authentication has been Disabled Successfully");
            DCEventLogUtil.getInstance().addEvent(715, userName, (HashMap)null, "dc.tfa.eventlog.disabled", (Object)null, true);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Caught exception while disabling TwoFactorAuthentication", ex);
        }
    }
    
    private JSONObject enableTFA(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        final String authType = String.valueOf(requestJSON.get("auth_type"));
        final String otp = requestJSON.optString("otp", "0");
        Criteria twoFactorCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"authType", 0);
        DataObject twoFactorParamDO = MDMUtil.getPersistence().get("UserMgmtParams", twoFactorCri);
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
        MDMUtil.getPersistence().update(twoFactorParamDO);
        twoFactorCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"otp", 0);
        twoFactorParamDO = MDMUtil.getPersistence().get("UserMgmtParams", twoFactorCri);
        if (twoFactorParamDO.isEmpty()) {
            final Row twoFactorParamRow = new Row("UserMgmtParams");
            twoFactorParamRow.set("PARAMS_NAME", (Object)"otp");
            twoFactorParamRow.set("PARAMS_VALUE", (Object)otp);
            twoFactorParamDO.addRow(twoFactorParamRow);
        }
        else {
            final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("UserMgmtParams");
            twoFactorParamRow.set("PARAMS_VALUE", (Object)otp);
            twoFactorParamDO.updateRow(twoFactorParamRow);
        }
        MDMUtil.getPersistence().update(twoFactorParamDO);
        final Criteria googleAuthCri = new Criteria(Column.getColumn("GoogleAuthentication", "FIRST_LOGIN_STATUS"), (Object)true, 0);
        MDMUtil.getPersistence().delete(googleAuthCri);
        this.logger.log(Level.INFO, "Two Factor Authentication has been Enabled Successfully");
        this.logger.log(Level.INFO, "Mode of Authentication has been set as {0}", authType);
        DCEventLogUtil.getInstance().addEvent(715, userName, (HashMap)null, "dc.tfa.eventlog.enabled", (Object)null, true);
        DCEventLogUtil.getInstance().addEvent(715, userName, (HashMap)null, "dc.tfa.eventlog.mode", (Object)authType, true);
        responseJSON.put("auth_type", (Object)authType);
        responseJSON.put("otp", (Object)otp);
        return responseJSON;
    }
    
    static {
        TwoFactorAuthenticationHandler.twoFactorAuthenticationHandler = null;
    }
}
