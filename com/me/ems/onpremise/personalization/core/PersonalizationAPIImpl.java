package com.me.ems.onpremise.personalization.core;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Locale;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import java.util.Collections;
import com.adventnet.authentication.PAM;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.adventnet.authentication.util.AuthUtil;
import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.ArrayList;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;
import com.me.ems.framework.personalization.core.AbstractPersonalizationImpl;

public class PersonalizationAPIImpl extends AbstractPersonalizationImpl
{
    Logger logger;
    
    public PersonalizationAPIImpl() {
        this.logger = Logger.getLogger(PersonalizationAPIImpl.class.getName());
    }
    
    public Map<String, Object> getPersonalizeSettings(final User user) throws Exception {
        final Map<String, Object> returnMap = new HashMap<String, Object>();
        final Row row = SyMUtil.getUserAccountSettings(user.getUserID());
        if (row != null) {
            final Integer timeout = (Integer)row.get("SESSION_EXPIRY_TIME");
            final int time = timeout / 60;
            returnMap.put("sessionExpiryTime", time);
            returnMap.put("showHelpCard", row.get("SHOW_HELP_CARD"));
            returnMap.put("personaliseTime", user.getTimeFormat());
            returnMap.put("personaliseTimeZone", user.getUserTimeZone());
        }
        else {
            returnMap.put("sessionExpiryTime", 15);
            returnMap.put("showHelpCard", Boolean.TRUE);
            returnMap.put("personaliseTime", SyMUtil.getDefaultTimeFormat());
            returnMap.put("personaliseTimeZone", SyMUtil.getDefaultTimeZoneID());
        }
        returnMap.put("isLanguagePackEnabled", LicenseProvider.getInstance().isLanguagePackEnabled());
        returnMap.put("personaliseLanguage", I18NUtil.getUserLocaleFromDB(user.getLoginID()) + "");
        return returnMap;
    }
    
    public Map<String, Object> updatePersonalizeSettings(final Map<String, Object> detailsMap, final User user, final HttpServletRequest request) throws Exception {
        final Map<String, Object> returnMap = new HashMap<String, Object>();
        this.validateUserInputs(detailsMap);
        Boolean update = Boolean.FALSE;
        final Long userID = user.getUserID();
        Row userSettingsRow = SyMUtil.getUserAccountSettings(userID);
        if (userSettingsRow == null) {
            userSettingsRow = new Row("UserSettings");
            userSettingsRow.set("USER_ACCOUNT_ID", (Object)userID);
            userSettingsRow.set("REFRESH_TIME", (Object)60);
        }
        final Integer time = detailsMap.get("sessionExpiryTime");
        final int timeout = time * 60;
        final Integer expiryTime = (Integer)userSettingsRow.get("SESSION_EXPIRY_TIME");
        if (time != null && time >= 10 && time <= 480 && (expiryTime == null || expiryTime != timeout)) {
            request.getSession().setMaxInactiveInterval(timeout);
            userSettingsRow.set("SESSION_EXPIRY_TIME", (Object)timeout);
            update = Boolean.TRUE;
        }
        final Boolean showHelpCard = (Boolean)userSettingsRow.get("SHOW_HELP_CARD");
        final Boolean showHelpCardFromRequest = detailsMap.get("showHelpCard");
        if (showHelpCard == null || showHelpCard != showHelpCardFromRequest) {
            userSettingsRow.set("SHOW_HELP_CARD", detailsMap.get("showHelpCard"));
            update = Boolean.TRUE;
        }
        SyMUtil.updateUserParameter(userID, "INACTIVE_USERS_HELPCARD", "true");
        SyMUtil.updateUserParameter(userID, "SOM_LIST_HELPCARD", "true");
        SyMUtil.updateUserParameter(userID, "SOM_HELPCARD", "true");
        SyMUtil.updateUserParameter(userID, "VIEW_CONFIGURATION_HELPCARD", "true");
        SyMUtil.updateUserParameter(userID, "RDS_ACTIVEX_HELPCARD", "true");
        final String theme = SyMUtil.getInstance().getTheme();
        final String previousTheme = (String)userSettingsRow.get("THEME");
        if (theme != null && !previousTheme.equals(theme)) {
            userSettingsRow.set("THEME", (Object)theme);
            returnMap.put("themeMessage", I18N.getMsg("dc.admin.personalize.update_theme", new Object[] { theme }));
            update = Boolean.TRUE;
        }
        returnMap.put("selectedSkin", SyMUtil.getInstance().getTheme());
        final Map resultMap = this.updateTimeZoneAndLanguage(detailsMap, user);
        returnMap.putAll(resultMap);
        final String selectedTimeFormat = detailsMap.get("personaliseTime");
        final String userTimeFormatFromDB = (String)userSettingsRow.get("TIMEFORMAT");
        this.logger.log(Level.INFO, "seletedTimeFormat value : " + selectedTimeFormat);
        if (!selectedTimeFormat.equals(userTimeFormatFromDB)) {
            final String selectedDateFormat = DMUserHandler.getUsersDateFormat(selectedTimeFormat);
            userSettingsRow.set("TIMEFORMAT", (Object)selectedTimeFormat);
            userSettingsRow.set("DATEFORMAT", (Object)selectedDateFormat);
            returnMap.put("personalisedTimeMsg", "Successfully Time is updated");
            final String timeKey = userID + "_" + "TIMEFORMAT";
            final String dateKey = userID + "_" + "DATEFORMAT";
            ApiFactoryProvider.getCacheAccessAPI().removeCache(timeKey, 3);
            ApiFactoryProvider.getCacheAccessAPI().removeCache(dateKey, 3);
            update = Boolean.TRUE;
        }
        if (update) {
            SyMUtil.addOrUpdateUserAccountSettings(userSettingsRow);
        }
        returnMap.put("generalSettingsMessage", I18N.getMsg("dc.admin.generalsettings.updated_general_settings", new Object[0]));
        DCEventLogUtil.getInstance().addEvent(4001, user.getName(), (HashMap)null, "dm.event.personalize.change", (Object)null, false, CustomerInfoUtil.getInstance().getCustomerId());
        return returnMap;
    }
    
    public Map<String, Object> getUserImage(final User user) throws Exception {
        final Map<String, Object> userImagePath = new HashMap<String, Object>(4);
        final Long loginID = user.getLoginID();
        final File croppedImageFile = new File(DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + "user_profile" + File.separator + loginID + ".png");
        final File fullImageFile = new File(DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + "user_full_profile" + File.separator + loginID + ".png");
        String userImage;
        if (croppedImageFile.exists() && fullImageFile.exists()) {
            userImage = "/images/user_profile/" + loginID + ".png";
            final String fullImage = "/images/user_full_profile/" + loginID + ".png";
            userImagePath.put("fullImagePath", fullImage);
            userImagePath.put("isProfilePictureChanged", true);
        }
        else {
            userImage = "/images/user-thumbnail.png";
            userImagePath.put("isProfilePictureChanged", false);
        }
        userImagePath.put("userImagePath", userImage);
        return userImagePath;
    }
    
    private String getRobotoFontPath() {
        final String robotoFont = "Roboto-Regular.ttf";
        final String robotoFontPath = System.getProperty("server.home") + File.separator + "lib" + File.separator + "resources" + File.separator + "fonts" + File.separator + "roboto" + File.separator + robotoFont;
        return robotoFontPath;
    }
    
    private void validateUserInputs(final Map<String, Object> detailsMap) throws APIException {
        final Integer time = detailsMap.get("sessionExpiryTime");
        final String selectedTimeFormat = detailsMap.get("personaliseTime");
        final List<String> nullList = new ArrayList<String>(4);
        if (time == null) {
            nullList.add("sessionExpiryTime");
        }
        if (selectedTimeFormat == null || selectedTimeFormat.isEmpty()) {
            nullList.add("personaliseTime");
        }
        this.validateTimeZoneAndLanguage(detailsMap);
        if (!nullList.isEmpty()) {
            throw new APIException("GENERIC0009", (String)null, (String[])nullList.toArray(new String[0]));
        }
    }
    
    public boolean deleteActiveSession(final Long sessionID, final HttpServletRequest request, final User user) throws Exception {
        if (sessionID == null) {
            AuthUtil.closeAllOtherSessions(request);
            this.addOrUpdateSessionInvalidatorUsedCount("Kill All Session");
            DCEventLogUtil.getInstance().addEvent(717, user.getName(), (HashMap)null, "dc.admin.activesession.signout", (Object)request.getRemoteHost(), true);
        }
        else {
            final List<Long> sessionList = DMOnPremiseUserUtil.getActiveSessionForLoginID(user.getLoginID());
            if (!sessionList.contains(sessionID)) {
                this.logger.log(Level.WARNING, "Active session can't be deleted as session id doesn't belong to the current user");
                return Boolean.FALSE;
            }
            PAM.logout(sessionID);
            this.addOrUpdateSessionInvalidatorUsedCount("Kill Specified Session");
        }
        return Boolean.TRUE;
    }
    
    public void closeAllSessions(final User user) throws Exception {
        AuthUtil.closeAllSessions(user.getUserID());
        this.logger.log(Level.INFO, "All sessions have been closed for the user : " + user.getName());
    }
    
    public Map<String, Object> getActiveSessions(final User user) throws Exception {
        return (Map<String, Object>)Collections.singletonMap("count", DMOnPremiseUserUtil.getActiveSessionForLoginID(user.getLoginID()).size());
    }
    
    private void addOrUpdateSessionInvalidatorUsedCount(final String paramName) {
        try {
            final int usedCount = Integer.parseInt(UserMgmtUtil.getUserMgmtParameter(paramName));
            UserMgmtUtil.updateUserMgmtParameter(paramName, String.valueOf(usedCount + 1));
        }
        catch (final NumberFormatException ex) {
            UserMgmtUtil.updateUserMgmtParameter(paramName, String.valueOf(1));
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception while addOrUpdate SessionInvalidator Used Count:", ex2);
        }
    }
    
    public Map<String, Object> updateTimeZoneAndLanguage(final Map<String, Object> detailsMap, final User user) throws Exception {
        final Map<String, Object> returnMap = new HashMap<String, Object>();
        this.validateTimeZoneAndLanguage(detailsMap);
        final Long userID = user.getUserID();
        final String selectedLanguage = detailsMap.get("personaliseLanguage");
        final String selectedTimeZone = detailsMap.get("personaliseTimeZone");
        final String[] langCountry = selectedLanguage.split("_");
        final Locale userLocaleFromRequest = new Locale(langCountry[0], langCountry[1]);
        final Locale userLocaleFromDB = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocaleFromDB(user.getLoginID());
        final String existingUserTimeZone = DMUserHandler.getUserTimeZoneIDFromDB(userID);
        Boolean isUpdate = false;
        if (!userLocaleFromRequest.equals(userLocaleFromDB)) {
            if (!LicenseProvider.getInstance().isLanguagePackEnabled()) {
                throw new APIException("PERS0001");
            }
            returnMap.put("isLanguageChanged", "true");
            DMOnPremiseUserUtil.changeAAAProfile(userID, langCountry[0], langCountry[1], selectedTimeZone);
            returnMap.put("langCountryMsg", I18N.getMsg("dc.admin.personalize.updated_language", new Object[0]));
            if (langCountry[0].equals("ru")) {
                final Properties fontProps = new Properties();
                fontProps.load(new FileInputStream(System.getProperty("server.home") + File.separator + "conf" + File.separator + "fonts.properties"));
                final String fontPathFromProps = fontProps.getProperty("ru.url");
                final String fontpath = this.getRobotoFontPath();
                if (!fontPathFromProps.equalsIgnoreCase(fontpath)) {
                    final Boolean isFileExists = new File(fontpath).exists();
                    if (!isFileExists) {
                        this.logger.severe("Font file not installed in the computer");
                    }
                    else {
                        final Date currDate = new Date();
                        fontProps.setProperty("ru.url", fontpath);
                        fontProps.store(new FileOutputStream(System.getProperty("server.home") + "/conf/fonts.properties"), "Updated the external font path for Russian language at " + currDate.toString());
                    }
                }
            }
            final String localeKey = userID + "_" + "USERLOCALE";
            ApiFactoryProvider.getCacheAccessAPI().removeCache(localeKey, 3);
            isUpdate = true;
        }
        if (!selectedTimeZone.equals(existingUserTimeZone)) {
            this.logger.log(Level.INFO, "Timezone value of the selected user is : " + selectedTimeZone);
            final String timeZoneKey = userID + "_" + "USERTIMEZONEID";
            ApiFactoryProvider.getCacheAccessAPI().removeCache(timeZoneKey, 3);
            isUpdate = true;
        }
        if (isUpdate) {
            DMOnPremiseUserUtil.changeAAAProfile(userID, langCountry[0], langCountry[1], selectedTimeZone);
        }
        return returnMap;
    }
    
    public void validateTimeZoneAndLanguage(final Map<String, Object> detailsMap) throws APIException {
        final String selectedLanguage = detailsMap.get("personaliseLanguage");
        final String selectedTimeZone = detailsMap.get("personaliseTimeZone");
        final List<String> nullList = new ArrayList<String>(4);
        if (selectedLanguage == null || selectedLanguage.isEmpty()) {
            nullList.add("personaliseLanguage");
        }
        if (selectedTimeZone == null || selectedTimeZone.isEmpty()) {
            nullList.add("personaliseTimeZone");
        }
        if (!nullList.isEmpty()) {
            throw new APIException("GENERIC0009", (String)null, (String[])nullList.toArray(new String[0]));
        }
    }
}
