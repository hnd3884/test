package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import org.json.JSONObject;
import java.util.Iterator;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeManagedGuestSessionPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeManagedGuestSessionPayload implements DO2ChromePayload
{
    @Override
    public ChromeManagedGuestSessionPayload createPayload(final DataObject dataObject) {
        ChromeManagedGuestSessionPayload chromeManagedGuestSessionPayload = null;
        try {
            chromeManagedGuestSessionPayload = new ChromeManagedGuestSessionPayload("1.0", "ManagedGuestSession", "ManagedGuestSession");
            final Row managedGuestSessionRow = dataObject.getRow("ManagedGuestSession");
            final Boolean managedGuestSession = (Boolean)managedGuestSessionRow.get("MANAGED_GUEST_SESSION");
            final String sessionName = (String)managedGuestSessionRow.get("SESSION_NAME");
            chromeManagedGuestSessionPayload.setManagedGuestSession(managedGuestSession);
            chromeManagedGuestSessionPayload.setSessionName(sessionName);
            if (managedGuestSessionRow.get("SESSION_LENGTH") != null) {
                final int sessionLength = (int)managedGuestSessionRow.get("SESSION_LENGTH");
                chromeManagedGuestSessionPayload.setSessionLength(sessionLength);
            }
            if (dataObject.containsTable("ChromeUserRestrictions")) {
                this.handleRestrictionsPayload(dataObject, chromeManagedGuestSessionPayload);
            }
            if (dataObject.containsTable("BrowserConfiguration")) {
                this.handleBrowserRestrictionsPayload(dataObject, chromeManagedGuestSessionPayload);
            }
            if (dataObject.containsTable("IOSWebContentPolicy")) {
                this.handleWebContentPayload(dataObject, chromeManagedGuestSessionPayload);
            }
            if (dataObject.containsTable("ManagedBookmarksPolicy")) {
                this.handleBookmarksPayload(dataObject, chromeManagedGuestSessionPayload);
            }
        }
        catch (final Exception ex) {
            DO2ChromeManagedGuestSessionPayload.LOGGER.log(Level.SEVERE, "Exception in ChromeManagedGuestSession createPayload", ex);
        }
        return chromeManagedGuestSessionPayload;
    }
    
    private void handleRestrictionsPayload(final DataObject dataObject, final ChromeManagedGuestSessionPayload chromeManagedGuestSessionPayload) throws DataAccessException {
        final Row restrictionRow = dataObject.getRow("ChromeUserRestrictions");
        if (restrictionRow != null) {
            chromeManagedGuestSessionPayload.initializeRestrictionPayload();
            chromeManagedGuestSessionPayload.getChromeUserRestrictionPayload().setShowHomeButton((int)restrictionRow.get("SHOW_HOME_BUTTON"));
            chromeManagedGuestSessionPayload.getChromeUserRestrictionPayload().setExternalStorageAccess((int)restrictionRow.get("EXTERNAL_STORAGE_ACCESSIBILITY"));
            chromeManagedGuestSessionPayload.setRestrictionJSON();
        }
    }
    
    private void handleBrowserRestrictionsPayload(final DataObject dataObject, final ChromeManagedGuestSessionPayload chromeManagedGuestSessionPayload) throws DataAccessException {
        final Row browserRestrictionRow = dataObject.getRow("BrowserConfiguration");
        if (browserRestrictionRow != null) {
            chromeManagedGuestSessionPayload.initializeBrowserRestrictionPayload();
            final int homePageSettings = (int)browserRestrictionRow.get("HOME_PAGE_SETTINGS");
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().initiatePopupSettings();
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().initiateHomePageSettings();
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setPopupSettings((int)browserRestrictionRow.get("POPUP_SETTINGS"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setPopupAllowURL((String)browserRestrictionRow.get("POPUP_ALLOW_URL"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setPopupBlockURL((String)browserRestrictionRow.get("POPUP_BLOCK_URL"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setSafeBrowsing((int)browserRestrictionRow.get("SAFE_BROWSING"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setMaliciousSites((boolean)browserRestrictionRow.get("PREVENT_MALICIOUS_SITES"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setHomePageSettings(homePageSettings);
            if (homePageSettings == 2) {
                chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setHomePageURL((String)browserRestrictionRow.get("HOME_PAGE_URL"));
            }
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setStartupURLs((String)browserRestrictionRow.get("STARTUP_URLS"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setIncognitoMode((int)browserRestrictionRow.get("INCOGNITO_MODE"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setSavingHistoryAllowed((boolean)browserRestrictionRow.get("SAVING_HISTORY"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setDeletingHistoryAllowed((boolean)browserRestrictionRow.get("DELETING_HISTORY"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setBookmarksBarEnabled((int)browserRestrictionRow.get("BOOKMARKS_ENABLED"));
            chromeManagedGuestSessionPayload.getChromeBrowserSettingsPayload().setIsEditBookmarksAllowed((boolean)browserRestrictionRow.get("BOOKMARKS_EDITABLE"));
            chromeManagedGuestSessionPayload.setBrowserRestrictionsJSON();
        }
    }
    
    private void handleWebContentPayload(final DataObject dataObject, final ChromeManagedGuestSessionPayload chromeManagedGuestSessionPayload) throws DataAccessException {
        final Row webContentRow = dataObject.getRow("IOSWebContentPolicy");
        if (webContentRow != null) {
            chromeManagedGuestSessionPayload.initializeWebContentPayload();
            final Iterator urlIterator = dataObject.getRows("ManagedGuestSessionWebContentUrlDetails");
            if (webContentRow.get("URL_FILTER_TYPE")) {
                final JSONArray whitelistUrlArray = new JSONArray();
                final Boolean enableBookmarks = (Boolean)webContentRow.get("CREATE_BOOKMARKS");
                chromeManagedGuestSessionPayload.getChromeWebContentFilterPayload().setEnableBookmarks(enableBookmarks);
                while (urlIterator.hasNext()) {
                    final Row urlDetailsRow = urlIterator.next();
                    final String url = (String)urlDetailsRow.get("URL");
                    String title = "";
                    if (enableBookmarks) {
                        title = (String)urlDetailsRow.get("BOOKMARK_TITILE");
                    }
                    final JSONObject whitelistUrlDetail = chromeManagedGuestSessionPayload.getChromeWebContentFilterPayload().getUrlJSON(url, title);
                    whitelistUrlArray.put((Object)whitelistUrlDetail);
                }
                chromeManagedGuestSessionPayload.getChromeWebContentFilterPayload().setWhitelistURLs(whitelistUrlArray);
            }
            else {
                final JSONArray blacklistUrlArray = new JSONArray();
                while (urlIterator.hasNext()) {
                    final Row urlDetailsRow2 = urlIterator.next();
                    final String url2 = (String)urlDetailsRow2.get("URL");
                    blacklistUrlArray.put((Object)url2);
                }
                chromeManagedGuestSessionPayload.getChromeWebContentFilterPayload().setBlacklistURLs(blacklistUrlArray);
            }
            chromeManagedGuestSessionPayload.setWebContentFilterJSON();
        }
    }
    
    private void handleBookmarksPayload(final DataObject dataObject, final ChromeManagedGuestSessionPayload chromeManagedGuestSessionPayload) throws DataAccessException {
        final Row bookmarksRow = dataObject.getRow("ManagedBookmarksPolicy");
        if (bookmarksRow != null) {
            chromeManagedGuestSessionPayload.initializeBookmarksPayload();
            chromeManagedGuestSessionPayload.getChromeBookmarksPayload().setEditable((boolean)bookmarksRow.get("IS_BOOKMARK_EDITABLE"));
            chromeManagedGuestSessionPayload.getChromeBookmarksPayload().setBookmarkBar((int)bookmarksRow.get("BOOKMARKS_BAR"));
            final JSONObject folder = chromeManagedGuestSessionPayload.getChromeBookmarksPayload().getFolderNameElem((String)bookmarksRow.get("FOLDER_NAME"));
            final JSONArray bookmarkArray = new JSONArray();
            if (folder != null) {
                bookmarkArray.put((Object)folder);
            }
            final Iterator urlIterator = dataObject.getRows("ManagedGuestSessionBookmarksUrlDetails");
            while (urlIterator.hasNext()) {
                final Row urlDetailsRow = urlIterator.next();
                final String url = (String)urlDetailsRow.get("URL");
                final String title = (String)urlDetailsRow.get("BOOKMARK_TITILE");
                final JSONObject urlDetail = chromeManagedGuestSessionPayload.getChromeBookmarksPayload().getUrlJSON(url, title);
                bookmarkArray.put((Object)urlDetail);
            }
            chromeManagedGuestSessionPayload.getChromeBookmarksPayload().setURLs(bookmarkArray);
            chromeManagedGuestSessionPayload.setBookmarksJSON();
        }
    }
}
