package com.me.mdm.chrome.agent;

import com.google.chromedevicemanagement.v1.model.ManagedBookmarks;
import com.google.chromedevicemanagement.v1.model.UrlAllowlist;
import com.google.chromedevicemanagement.v1.model.UrlBlocklist;
import com.google.chromedevicemanagement.v1.model.EditBookmarksDisabled;
import com.google.chromedevicemanagement.v1.model.BookmarksBarEnabled;
import com.google.chromedevicemanagement.v1.model.DeletingBrowserHistoryDisabled;
import com.google.chromedevicemanagement.v1.model.SavingBrowserHistoryDisabled;
import com.google.chromedevicemanagement.v1.model.HomepageSettings;
import com.google.chromedevicemanagement.v1.model.RestoreOnStartupUrls;
import com.google.chromedevicemanagement.v1.model.DisableSafeBrowsingProceedAnyway;
import com.google.chromedevicemanagement.v1.model.SafeBrowsingEnabled;
import com.google.chromedevicemanagement.v1.model.PopupsBlockedForUrls;
import com.google.chromedevicemanagement.v1.model.PopupsAllowedForUrls;
import java.util.List;
import com.google.chromedevicemanagement.v1.model.PopupsDefaultSettings;
import com.google.chromedevicemanagement.v1.model.SessionLengthLimit;
import com.google.chromedevicemanagement.v1.model.DisplayName;
import com.google.chromedevicemanagement.v1.model.PublicSession;
import com.google.chromedevicemanagement.v1.model.ScreenLockDisabled;
import com.google.chromedevicemanagement.v1.model.ExternalStorageAccessibility;
import com.google.chromedevicemanagement.v1.model.ShowHomeButton;
import com.google.chromedevicemanagement.v1.model.PrintingDisabled;
import com.google.chromedevicemanagement.v1.model.TaskManagerEndProcessDisabled;
import com.google.chromedevicemanagement.v1.model.IncognitoModeAvailability;
import com.google.chromedevicemanagement.v1.model.Operation;
import com.google.chromedevicemanagement.v1.model.IssueDeviceCommandRequest;
import com.google.chromedevicemanagement.v1.model.DeviceCommand;
import com.google.chromedevicemanagement.v1.model.DeviceDisabledMessage;
import com.google.api.services.directory.model.ChromeOsDeviceAction;
import org.json.JSONObject;
import java.io.IOException;
import java.util.logging.Level;
import com.google.chromedevicemanagement.v1.model.Device;
import java.util.Iterator;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.UserPolicy;
import com.google.chromedevicemanagement.v1.model.DevicePolicy;
import java.util.logging.Logger;

public class GoogleChromeAPIWrapper
{
    public static Logger logger;
    private static DevicePolicy devicePolicy;
    private static UserPolicy userPolicy;
    
    public static void initiateDevicePolicy() {
        GoogleChromeAPIWrapper.devicePolicy = new DevicePolicy();
    }
    
    public static void initiateUserPolicy() {
        GoogleChromeAPIWrapper.userPolicy = new UserPolicy();
    }
    
    public static String getUpdateMask(final Set<String> keySet) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : keySet) {
            builder.append(s + ",");
        }
        return builder.toString();
    }
    
    public static Device getEnterpriseDeviceDetails(final Context context) throws IOException {
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Chrome API execution. HTTP Request: GET https://chromedevicemanagement.googleapis.com/v1/enterprises/{0}/devices/{1} ", new Object[] { context.getEnterpriseId(), context.getUdid() });
        final Device deviceDetails = (Device)context.getCMPAService().enterprises().devices().get(context.getCMPAEnterpriseAndUDID()).execute();
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Response : {0}", deviceDetails.toString());
        return deviceDetails;
    }
    
    public static JSONObject getDeviceActionRequestBody(final String deviceAction) {
        final JSONObject deviceActionRequestBody = new JSONObject();
        deviceActionRequestBody.put("action", (Object)deviceAction);
        return deviceActionRequestBody;
    }
    
    public static void setDeviceAction(final String deviceAction, final Context context) throws IOException {
        final JSONObject deviceActionRequestBody = getDeviceActionRequestBody(deviceAction);
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Chrome API execution. HTTP Request: POST https://www.googleapis.com/admin/directory/v1/customer/{0}/devices/chromeos/{1}/action ; RequestBody : {2}", new Object[] { context.getEnterpriseId(), context.getUdid(), deviceActionRequestBody.toString() });
        context.getDirectoryService().chromeosdevices().action(context.getEnterpriseId(), context.getUdid(), new ChromeOsDeviceAction().setAction(deviceAction)).execute();
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Device action has been set successfully");
    }
    
    public static void setDeviceDisabledMessage(final String lostModeMessage, final Context context) throws IOException {
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Going to set lost mode message");
        final DevicePolicy devicePolicy = new DevicePolicy();
        devicePolicy.setDeviceDisabledMessage(new DeviceDisabledMessage().setDeviceDisabledMessage(lostModeMessage));
        updateDevicePolicy(devicePolicy, context);
    }
    
    public static void revertDeviceDisabledMessage(final Context context) throws IOException {
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Going to remove lost mode message");
        final DevicePolicy devicePolicy = getDevicePolicy(context);
        devicePolicy.setDeviceDisabledMessage(new DeviceDisabledMessage());
        updateDevicePolicy(devicePolicy, context);
    }
    
    public static DevicePolicy getDevicePolicy(final Context context) throws IOException {
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Chrome API execution. HTTP Request : GET https://chromedevicemanagement.googleapis.com/v1/enterprises/{0}/devices/{1}/devicePolicy ", new Object[] { context.getEnterpriseId(), context.getUdid() });
        GoogleChromeAPIWrapper.devicePolicy = (DevicePolicy)context.getCMPAService().enterprises().devices().getDevicePolicy(context.getCMPAEnterpriseAndUDID()).execute();
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Response : {0}", GoogleChromeAPIWrapper.devicePolicy.toString());
        return GoogleChromeAPIWrapper.devicePolicy;
    }
    
    public static UserPolicy getUserPolicy(final Context context) throws IOException {
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Chrome API execution. HTTP Request : GET https://chromedevicemanagement.googleapis.com/v1/enterprises/{0}/devices/{1}/devicePolicy ", new Object[] { context.getEnterpriseId(), context.getUdid() });
        GoogleChromeAPIWrapper.userPolicy = (UserPolicy)context.getCMPAService().enterprises().users().getUserPolicy(context.getCMPAEnterpriseAndUDID()).execute();
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Response : {0}", GoogleChromeAPIWrapper.userPolicy.toString());
        return GoogleChromeAPIWrapper.userPolicy;
    }
    
    @Deprecated
    public static void updateDevicePolicy(final DevicePolicy devicePolicy, final Context context) throws IOException {
        final String updateMask = getUpdateMask(devicePolicy.keySet());
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Chrome API execution. HTTP Request: PATCH https://chromedevicemanagement.googleapis.com/v1/enterprises/{0}/devices/{1}/devicePolicy?updateMask={2} ", new Object[] { context.getEnterpriseId(), context.getUdid(), updateMask });
        final DevicePolicy responseDevicePolicy = (DevicePolicy)context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask(updateMask).execute();
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Device policy updated successfully");
    }
    
    @Deprecated
    public static void updateUserPolicy(final UserPolicy userPolicy, final Context context) throws IOException {
        final String updateMask = getUpdateMask(userPolicy.keySet());
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Chrome API execution. HTTP Request: PATCH https://chromedevicemanagement.googleapis.com/v1/enterprises/{0}/users/{1}/userPolicy?updateMask={2} ", new Object[] { context.getEnterpriseId(), context.getUdid(), updateMask });
        final UserPolicy responseUserPolicy = (UserPolicy)context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), userPolicy).setUpdateMask(updateMask).execute();
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "User policy updated successfully");
    }
    
    public static void updateDevicePolicy(final Context context) throws IOException {
        final String updateMask = getUpdateMask(GoogleChromeAPIWrapper.devicePolicy.keySet());
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Chrome API execution. HTTP Request: PATCH https://chromedevicemanagement.googleapis.com/v1/enterprises/{0}/devices/{1}/devicePolicy?updateMask={2} ", new Object[] { context.getEnterpriseId(), context.getUdid(), updateMask });
        final DevicePolicy responseDevicePolicy = (DevicePolicy)context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), GoogleChromeAPIWrapper.devicePolicy).setUpdateMask(updateMask).execute();
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Device policy updated successfully : {0}", responseDevicePolicy.toString());
    }
    
    public static void updateUserPolicy(final Context context) throws IOException {
        final String updateMask = getUpdateMask(GoogleChromeAPIWrapper.userPolicy.keySet());
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Chrome API execution. HTTP Request: PATCH https://chromedevicemanagement.googleapis.com/v1/enterprises/{0}/users/{1}/userPolicy?updateMask={2} ", new Object[] { context.getEnterpriseId(), context.getUdid(), updateMask });
        final UserPolicy responseUserPolicy = (UserPolicy)context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), GoogleChromeAPIWrapper.userPolicy).setUpdateMask(updateMask).execute();
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "User policy updated successfully : {0}", responseUserPolicy.toString());
    }
    
    public static JSONObject getDeviceCommandRequestBody(final String commandType, final String commandExpiryTime) {
        final JSONObject deviceCommandRequestBody = new JSONObject();
        deviceCommandRequestBody.put("type", (Object)commandType);
        deviceCommandRequestBody.put("validDuration", (Object)commandExpiryTime);
        return deviceCommandRequestBody;
    }
    
    public static void issueDeviceCommand(final String commandType, final String commandExpiryTime, final Context context) throws IOException {
        final JSONObject deviceCommandRequestBody = getDeviceCommandRequestBody(commandType, commandExpiryTime);
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Chrome API execution. HTTP Request: POST https://chromedevicemanagement.googleapis.com/v1/enterprises/{0}/devices/{1}:issueCommand ; RequestBody : {2}", new Object[] { context.getEnterpriseId(), context.getUdid(), deviceCommandRequestBody.toString() });
        final DeviceCommand devicecommand = new DeviceCommand();
        devicecommand.setType(commandType);
        devicecommand.setValidDuration(commandExpiryTime);
        final IssueDeviceCommandRequest issueDeviceCommandRequest = new IssueDeviceCommandRequest();
        issueDeviceCommandRequest.setDeviceCommand(devicecommand);
        final Operation deviceOperation = (Operation)context.getCMPAService().enterprises().devices().issueCommand(context.getCMPAEnterpriseAndUDID(), issueDeviceCommandRequest).execute();
        GoogleChromeAPIWrapper.logger.log(Level.INFO, "Response : {0}", deviceOperation.toString());
    }
    
    public static void setIncognitoRestriction(final String incognitoAllowed) {
        final IncognitoModeAvailability incognitoModeAvailability = new IncognitoModeAvailability();
        incognitoModeAvailability.setIncognitoModeAvailability(incognitoAllowed);
        GoogleChromeAPIWrapper.userPolicy.setIncognitoModeAvailability(incognitoModeAvailability);
    }
    
    public static void revertIncognitoRestriction() {
        GoogleChromeAPIWrapper.userPolicy.setIncognitoModeAvailability(new IncognitoModeAvailability());
    }
    
    public static void setTaskEndProcessPolicy(final boolean isEndProcessAllowed) {
        final TaskManagerEndProcessDisabled taskManagerEndProcessDisabled = new TaskManagerEndProcessDisabled();
        taskManagerEndProcessDisabled.setTaskManagerEndProcessDisabled(Boolean.valueOf(!isEndProcessAllowed));
        GoogleChromeAPIWrapper.userPolicy.setTaskManagerEndProcessDisabled(taskManagerEndProcessDisabled);
    }
    
    public static void revertTaskEndProcessPolicy() {
        GoogleChromeAPIWrapper.userPolicy.setTaskManagerEndProcessDisabled(new TaskManagerEndProcessDisabled());
    }
    
    public static void setPrintingRestriction(final boolean isPrintingDisabled) {
        final PrintingDisabled printingDisabled = new PrintingDisabled();
        printingDisabled.setPrintingDisabled(Boolean.valueOf(isPrintingDisabled));
        GoogleChromeAPIWrapper.userPolicy.setPrintingDisabled(printingDisabled);
    }
    
    public static void revertPrintingRestriction() {
        GoogleChromeAPIWrapper.userPolicy.setPrintingDisabled(new PrintingDisabled());
    }
    
    public static void setHomeButtonPolicy(final String showHomeButtonMode) {
        final ShowHomeButton showHomeButton = new ShowHomeButton();
        showHomeButton.setShowHomeButtonMode(showHomeButtonMode);
        GoogleChromeAPIWrapper.userPolicy.setShowHomeButton(showHomeButton);
    }
    
    public static void revertHomeButtonPolicy() {
        GoogleChromeAPIWrapper.userPolicy.setShowHomeButton(new ShowHomeButton());
    }
    
    public static void setExternalStorageAccessibilityPolicy(final String mode) {
        final ExternalStorageAccessibility externalStorageAccessibility = new ExternalStorageAccessibility();
        externalStorageAccessibility.setAccessMode(mode);
        GoogleChromeAPIWrapper.userPolicy.setExternalStorageAccessibility(externalStorageAccessibility);
    }
    
    public static void revertExternalStorageAccessibilityPolicy() {
        GoogleChromeAPIWrapper.userPolicy.setExternalStorageAccessibility(new ExternalStorageAccessibility());
    }
    
    public static void setScreenLockRestriction(final boolean isScreenLockDisabled) {
        final ScreenLockDisabled screenLockDisabled = new ScreenLockDisabled();
        screenLockDisabled.setScreenLockDisabled(Boolean.valueOf(isScreenLockDisabled));
        GoogleChromeAPIWrapper.userPolicy.setScreenLockDisabled(screenLockDisabled);
    }
    
    public static void revertScreenLockRestriction() throws IOException {
        GoogleChromeAPIWrapper.userPolicy.setScreenLockDisabled(new ScreenLockDisabled());
    }
    
    public static void setPublicSession() {
        final PublicSession publicSession = new PublicSession();
        publicSession.setPublicSessionEnabled(Boolean.valueOf(true));
        publicSession.setUserPolicy(GoogleChromeAPIWrapper.userPolicy);
        GoogleChromeAPIWrapper.devicePolicy.setPublicSession(publicSession);
    }
    
    public static void revertPublicSession() {
        final PublicSession publicSession = new PublicSession();
        publicSession.setPublicSessionEnabled(Boolean.valueOf(false));
        GoogleChromeAPIWrapper.devicePolicy.setPublicSession(publicSession);
    }
    
    public static void setSessionName(final String sessionName) {
        final DisplayName displayName = new DisplayName();
        displayName.setDisplayName(sessionName);
        GoogleChromeAPIWrapper.userPolicy.setDisplayName(displayName);
    }
    
    public static void setSessionLength(final String duration) {
        final SessionLengthLimit sessionLengthLimit = new SessionLengthLimit();
        sessionLengthLimit.setSessionLengthLimit(duration);
        GoogleChromeAPIWrapper.userPolicy.setSessionLengthLimit(sessionLengthLimit);
    }
    
    public static void setPopUpsDefaultSettings(final String allowPopUps) {
        final PopupsDefaultSettings popupsDefaultSettings = new PopupsDefaultSettings();
        popupsDefaultSettings.setPopupsDefaultMode(allowPopUps);
        GoogleChromeAPIWrapper.userPolicy.setPopupsDefaultSettings(popupsDefaultSettings);
    }
    
    public static void revertPopUpsDefaultSettings() {
        GoogleChromeAPIWrapper.userPolicy.setPopupsDefaultSettings(new PopupsDefaultSettings());
    }
    
    public static void setPopupsAllowedUrls(final List<String> allowedURLs) {
        final PopupsAllowedForUrls popupsAllowedForUrls = new PopupsAllowedForUrls();
        popupsAllowedForUrls.setUrls((List)allowedURLs);
        GoogleChromeAPIWrapper.userPolicy.setPopupsAllowedForUrls(popupsAllowedForUrls);
    }
    
    public static void revertPopupsAllowedUrls() {
        GoogleChromeAPIWrapper.userPolicy.setPopupsAllowedForUrls(new PopupsAllowedForUrls());
    }
    
    public static void setPopupsBlockedUrls(final List<String> blockedUrls) {
        final PopupsBlockedForUrls popupsBlockedForUrls = new PopupsBlockedForUrls();
        popupsBlockedForUrls.setUrls((List)blockedUrls);
        GoogleChromeAPIWrapper.userPolicy.setPopupsBlockedForUrls(popupsBlockedForUrls);
    }
    
    public static void revertPopupsBlockedUrls() {
        GoogleChromeAPIWrapper.userPolicy.setPopupsBlockedForUrls(new PopupsBlockedForUrls());
    }
    
    public static void setSafeBrowsingPolicy(final String mode) {
        final SafeBrowsingEnabled safeBrowsingEnabled = new SafeBrowsingEnabled();
        safeBrowsingEnabled.setSafeBrowsingEnabledMode(mode);
        GoogleChromeAPIWrapper.userPolicy.setSafeBrowsingEnabled(safeBrowsingEnabled);
    }
    
    public static void revertSafeBrowsingPolicy() {
        GoogleChromeAPIWrapper.userPolicy.setSafeBrowsingEnabled(new SafeBrowsingEnabled());
    }
    
    public static void setDisableSafeModeandProceedAnyway(final Boolean disableSafeModeandProceedAnyway) {
        final DisableSafeBrowsingProceedAnyway disableSafeBrowsingProceedAnyway = new DisableSafeBrowsingProceedAnyway();
        disableSafeBrowsingProceedAnyway.setDisableSafeBrowsingProceedAnyway(disableSafeModeandProceedAnyway);
        GoogleChromeAPIWrapper.userPolicy.setDisableSafeBrowsingProceedAnyway(disableSafeBrowsingProceedAnyway);
    }
    
    public static void revertDisableSafeModeAndProceedAnyway() {
        GoogleChromeAPIWrapper.userPolicy.setDisableSafeBrowsingProceedAnyway(new DisableSafeBrowsingProceedAnyway());
    }
    
    public static void setRestoreOnStartUpUrls(final List<String> restoreURLS) {
        final RestoreOnStartupUrls restoreOnStartupUrls = new RestoreOnStartupUrls();
        restoreOnStartupUrls.setUrls((List)restoreURLS);
        GoogleChromeAPIWrapper.userPolicy.setRestoreOnStartupUrls(restoreOnStartupUrls);
    }
    
    public static void revertRestoreOnStartUpUrls() {
        GoogleChromeAPIWrapper.userPolicy.setRestoreOnStartupUrls(new RestoreOnStartupUrls());
    }
    
    public static void setHomePageSettings(final String mode, final String url) {
        final HomepageSettings homepageSettings = new HomepageSettings();
        homepageSettings.setHomepageMode(mode);
        if (url != null) {
            homepageSettings.setUrl(url);
        }
        GoogleChromeAPIWrapper.userPolicy.setHomepageSettings(homepageSettings);
    }
    
    public static void revertHomePageSettings() {
        GoogleChromeAPIWrapper.userPolicy.setHomepageSettings(new HomepageSettings());
    }
    
    public static void setSaveBrowserHistoryPolicy(final Boolean isSavingBrowserHistoryAllowed) {
        final SavingBrowserHistoryDisabled savingBrowserHistoryDisabled = new SavingBrowserHistoryDisabled();
        savingBrowserHistoryDisabled.setSavingBrowserHistoryDisabled(Boolean.valueOf(!isSavingBrowserHistoryAllowed));
        GoogleChromeAPIWrapper.userPolicy.setSavingBrowserHistoryDisabled(savingBrowserHistoryDisabled);
    }
    
    public static void revertSaveBrowserHistoryPolicy() {
        GoogleChromeAPIWrapper.userPolicy.setSavingBrowserHistoryDisabled(new SavingBrowserHistoryDisabled());
    }
    
    public static void setDeleteBrowserHistoryPolicy(final Boolean deleteBrowserHistory) {
        final DeletingBrowserHistoryDisabled deletingBrowserHistoryDisabled = new DeletingBrowserHistoryDisabled();
        deletingBrowserHistoryDisabled.setDeletingBrowserHistoryDisabled(Boolean.valueOf(!deleteBrowserHistory));
        GoogleChromeAPIWrapper.userPolicy.setDeletingBrowserHistoryDisabled(deletingBrowserHistoryDisabled);
    }
    
    public static void revertDeleteBrowserHistoryPolicy() {
        GoogleChromeAPIWrapper.userPolicy.setDeletingBrowserHistoryDisabled(new DeletingBrowserHistoryDisabled());
    }
    
    public static void setBookMarksBarEnabledPolicy(final String mode) {
        final BookmarksBarEnabled bookmarksBarEnabled = new BookmarksBarEnabled();
        bookmarksBarEnabled.setBookmarksBarEnabledMode(mode);
        GoogleChromeAPIWrapper.userPolicy.setBookmarksBarEnabled(bookmarksBarEnabled);
    }
    
    public static void revertBookMarksBarEnabledPolicy() {
        GoogleChromeAPIWrapper.userPolicy.setBookmarksBarEnabled(new BookmarksBarEnabled());
    }
    
    public static void setEditBookmarkPolicy(final Boolean isEditingAllowed) {
        final EditBookmarksDisabled editBookmarksDisabled = new EditBookmarksDisabled();
        editBookmarksDisabled.setEditBookmarksDisabled(Boolean.valueOf(!isEditingAllowed));
        GoogleChromeAPIWrapper.userPolicy.setEditBookmarksDisabled(editBookmarksDisabled);
    }
    
    public static void revertEditBookmarkPolicy() {
        GoogleChromeAPIWrapper.userPolicy.setEditBookmarksDisabled(new EditBookmarksDisabled());
    }
    
    public static void setBlacklistURLs(final List<String> urls) {
        final UrlBlocklist urlBlockList = new UrlBlocklist();
        urlBlockList.setUrls((List)urls);
        GoogleChromeAPIWrapper.userPolicy.setUrlBlocklist(urlBlockList);
    }
    
    public static void revertBlacklistURLs() {
        GoogleChromeAPIWrapper.userPolicy.setUrlBlocklist(new UrlBlocklist());
    }
    
    public static void setWhiteListURLs(final List<String> urls) {
        final UrlAllowlist urlAllowlist = new UrlAllowlist();
        urlAllowlist.setUrls((List)urls);
        GoogleChromeAPIWrapper.userPolicy.setUrlAllowlist(urlAllowlist);
    }
    
    public static void revertWhiteListURLs() {
        GoogleChromeAPIWrapper.userPolicy.setUrlAllowlist(new UrlAllowlist());
    }
    
    public static void setManagedBookmarks(final String jsonArray) {
        final ManagedBookmarks managedBookmarks = new ManagedBookmarks();
        managedBookmarks.setManagedBookmarks(jsonArray);
        GoogleChromeAPIWrapper.userPolicy.setManagedBookmarks(managedBookmarks);
    }
    
    static {
        GoogleChromeAPIWrapper.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
}
