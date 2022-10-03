package com.me.mdm.onpremise.server.user;

import javax.transaction.SystemException;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.factory.PersonalizationAPI;

public class PersonalizationAPIMDMPImpl implements PersonalizationAPI
{
    public Logger logger;
    
    public PersonalizationAPIMDMPImpl() {
        this.logger = Logger.getLogger("UserManagementLogger");
    }
    
    public JSONObject getPersonalizeSettings() {
        final JSONObject personalizeSettings = new JSONObject();
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Row row = SyMUtil.getUserAccountSettings(userID);
            if (row != null) {
                final Integer timeout = (Integer)row.get("SESSION_EXPIRY_TIME");
                final int time = timeout / 60;
                personalizeSettings.put("session_expiry_time", (Object)new Integer(time));
                personalizeSettings.put("personalise_time", (Object)DMUserHandler.getUserTimeFormat());
                personalizeSettings.put("personalise_time_zone", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID());
            }
            else {
                personalizeSettings.put("session_expiry_time", (Object)new Integer(15));
                personalizeSettings.put("personalise_time", (Object)SyMUtil.getDefaultTimeFormat());
                personalizeSettings.put("personalise_time_zone", (Object)SyMUtil.getDefaultTimeZoneID());
            }
            personalizeSettings.put("is_language_pack_enable", LicenseProvider.getInstance().isLanguagePackEnabled());
            final String userLocale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale().toString();
            personalizeSettings.put("personalise_language", (Object)userLocale);
            return personalizeSettings;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting personalisation settings details...", ex);
            throw new APIHTTPException("PS0001", new Object[0]);
        }
    }
    
    public JSONObject updatePersonalisationSettings(final JSONObject jsonObject) {
        try {
            final JSONObject responseJSON = new JSONObject();
            MDMUtil.getUserTransaction().begin();
            Long userID = JSONUtil.optLongForUVH(jsonObject, "user_id", Long.valueOf(-1L));
            if (userID == -1L) {
                userID = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            }
            Row userSettingsRow = null;
            userSettingsRow = SyMUtil.getUserAccountSettings(userID);
            if (userSettingsRow == null) {
                userSettingsRow = new Row("UserSettings");
                userSettingsRow.set("USER_ACCOUNT_ID", (Object)userID);
                userSettingsRow.set("REFRESH_TIME", (Object)60);
            }
            final int time = jsonObject.getInt("session_expiry_time");
            final int timeout = time * 60;
            String modifiedParams = "";
            final Integer expiryTime = (Integer)userSettingsRow.get("SESSION_EXPIRY_TIME");
            if (expiryTime == null || expiryTime != timeout) {
                userSettingsRow.set("SESSION_EXPIRY_TIME", (Object)timeout);
                responseJSON.put("session_expiry_time", timeout);
                if (timeout == -60) {
                    responseJSON.put("session_expiry_message", (Object)I18N.getMsg("dc.admin.personalize.updated_session_to_never_expire", new Object[0]));
                }
                else {
                    responseJSON.put("session_expiry_message", (Object)I18N.getMsg("dc.admin.personalize.update_to_expire_in_n_mins", new Object[] { time }));
                }
                final boolean update = true;
                modifiedParams = modifiedParams + I18N.getMsg("mdm.user.session_expiry_time", new Object[0]) + ", ";
            }
            final Boolean showHelpCard = (Boolean)userSettingsRow.get("SHOW_HELP_CARD");
            final Boolean isShowHelpCard = jsonObject.optBoolean("show_help_card");
            if (showHelpCard == null || showHelpCard != isShowHelpCard) {
                userSettingsRow.set("SHOW_HELP_CARD", (Object)isShowHelpCard);
            }
            responseJSON.put("show_help_card", (Object)isShowHelpCard);
            SyMUtil.updateUserParameter(userID, "INACTIVE_USERS_HELPCARD", "true");
            SyMUtil.updateUserParameter(userID, "SOM_LIST_HELPCARD", "true");
            SyMUtil.updateUserParameter(userID, "SOM_HELPCARD", "true");
            SyMUtil.updateUserParameter(userID, "VIEW_CONFIGURATION_HELPCARD", "true");
            SyMUtil.updateUserParameter(userID, "RDS_ACTIVEX_HELPCARD", "true");
            boolean update = true;
            final String selectedLanguage = jsonObject.optString("personalise_language");
            final String selectedTimeZone = jsonObject.optString("personalise_time_zone");
            final String defaultLanguage = "en_US";
            String[] langCountry = selectedLanguage.split("_");
            if (!LicenseProvider.getInstance().isLanguagePackEnabled()) {
                langCountry = defaultLanguage.split("_");
            }
            if (selectedLanguage != null && !selectedLanguage.equalsIgnoreCase("default")) {
                modifiedParams = modifiedParams + I18N.getMsg("dc.common.LANGUAGE", new Object[0]) + ", ";
                modifiedParams = modifiedParams + I18N.getMsg("mdm.user.time_zone", new Object[0]) + ", ";
                this.logger.log(Level.INFO, "User selected language is : {0} and its time zone value is : {1}", new Object[] { selectedLanguage, selectedTimeZone });
                if (DMOnPremiseUserUtil.isLanguageChanged(userID, langCountry[0])) {
                    responseJSON.put("language_changed", (Object)"true");
                }
                DMOnPremiseUserUtil.changeAAAProfile(userID, langCountry[0], langCountry[1], selectedTimeZone);
            }
            if (selectedLanguage.equalsIgnoreCase("default") && selectedTimeZone != null) {
                this.logger.log(Level.INFO, "Timezone value of the selected user is : {0}", selectedTimeZone);
                DMOnPremiseUserUtil.changeAAAProfile(userID, "en", "US", selectedTimeZone);
            }
            String seletedTimeFormat = jsonObject.optString("personalise_time");
            this.logger.log(Level.INFO, "seletedTimeFormat value : {0}", seletedTimeFormat);
            if (seletedTimeFormat != null) {
                if (seletedTimeFormat.equalsIgnoreCase("default")) {
                    seletedTimeFormat = SyMUtil.getDefaultTimeFormat();
                }
                final String seletedDateFormat = DMUserHandler.getUsersDateFormat(seletedTimeFormat);
                userSettingsRow.set("TIMEFORMAT", (Object)seletedTimeFormat);
                userSettingsRow.set("DATEFORMAT", (Object)seletedDateFormat);
                update = true;
                modifiedParams = modifiedParams + I18N.getMsg("mdm.user.time_format", new Object[0]) + ", ";
            }
            if (update) {
                SyMUtil.addOrUpdateUserAccountSettings(userSettingsRow);
                final String timeKey = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID() + "_" + "TIMEFORMAT";
                final String dateKey = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID() + "_" + "DATEFORMAT";
                final String localeKey = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID() + "_" + "USERLOCALE";
                final String timeZoneKey = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID() + "_" + "USERTIMEZONEID";
                this.logger.log(Level.INFO, "Dropping timezone related cache due to Personalizing with keys {0}, {1},  {2} and {3}", new Object[] { localeKey, timeZoneKey, timeKey, dateKey });
                ApiFactoryProvider.getCacheAccessAPI().removeCache(localeKey, 3);
                ApiFactoryProvider.getCacheAccessAPI().removeCache(timeZoneKey, 3);
                ApiFactoryProvider.getCacheAccessAPI().removeCache(timeKey, 3);
                ApiFactoryProvider.getCacheAccessAPI().removeCache(dateKey, 3);
            }
            final char[] chars = modifiedParams.toCharArray();
            chars[chars.length - 1] = ' ';
            modifiedParams = new String(chars);
            final int lastIndex = modifiedParams.lastIndexOf(",");
            chars[lastIndex] = '&';
            modifiedParams = new String(chars);
            final JSONObject personaliseJSON = this.getPersonalizeSettings();
            responseJSON.put("personalise_language", (Object)String.valueOf(personaliseJSON.get("personalise_language")));
            responseJSON.put("personalise_time", (Object)String.valueOf(personaliseJSON.get("personalise_time")));
            responseJSON.put("personalise_time_zone", (Object)String.valueOf(personaliseJSON.get("personalise_time_zone")));
            responseJSON.put("is_language_pack_enable", (Object)String.valueOf(personaliseJSON.get("is_language_pack_enable")));
            final String currentUserName = MDMUtil.getInstance().getCurrentlyLoggedOnUserName();
            DCEventLogUtil.getInstance().addEvent(8002, currentUserName, (HashMap)null, I18N.getMsg("mdm.user.personalization_success", new Object[] { currentUserName, modifiedParams, DMUserHandler.getUserNameFromUserID(userID) }), (Object)null, true);
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- updatePersonalisationSettings() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- updatePersonalisationSettings() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
