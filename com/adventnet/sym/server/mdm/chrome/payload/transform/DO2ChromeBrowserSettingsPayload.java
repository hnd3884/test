package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeBrowserSettingsPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeBrowserSettingsPayload implements DO2ChromePayload
{
    @Override
    public ChromeBrowserSettingsPayload createPayload(final DataObject dataObject) {
        ChromeBrowserSettingsPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("BrowserConfiguration");
            while (iterator.hasNext()) {
                payload = new ChromeBrowserSettingsPayload("1.0", "ChromeBrowserRestriction", "ChromeBrowserRestriction");
                final Row row = iterator.next();
                final int popUpSettings = (int)row.get("POPUP_SETTINGS");
                final String popupAllowURL = (String)row.get("POPUP_ALLOW_URL");
                final String popupBlockURL = (String)row.get("POPUP_BLOCK_URL");
                final int safeBrowsing = (int)row.get("SAFE_BROWSING");
                final boolean maliciousSites = (boolean)row.get("PREVENT_MALICIOUS_SITES");
                final int homePageSettings = (int)row.get("HOME_PAGE_SETTINGS");
                final String startupURLs = (String)row.get("STARTUP_URLS");
                final String homePageURL = (String)row.get("HOME_PAGE_URL");
                final int incongnitoMode = (int)row.get("INCOGNITO_MODE");
                final boolean isSavingHistoryAllowed = (boolean)row.get("SAVING_HISTORY");
                final boolean isDeletingHistoryAllowed = (boolean)row.get("DELETING_HISTORY");
                final int bookmarksEnabled = (int)row.get("BOOKMARKS_ENABLED");
                final boolean isBookMarkEditable = (boolean)row.get("BOOKMARKS_EDITABLE");
                payload.initiatePopupSettings();
                payload.initiateHomePageSettings();
                payload.setPopupSettings(popUpSettings);
                payload.setPopupAllowURL(popupAllowURL);
                payload.setPopupBlockURL(popupBlockURL);
                payload.setSafeBrowsing(safeBrowsing);
                payload.setMaliciousSites(maliciousSites);
                payload.setHomePageSettings(homePageSettings);
                if (homePageSettings == 2) {
                    payload.setHomePageURL(homePageURL);
                }
                payload.setStartupURLs(startupURLs);
                payload.setIncognitoMode(incongnitoMode);
                payload.setSavingHistoryAllowed(isSavingHistoryAllowed);
                payload.setDeletingHistoryAllowed(isDeletingHistoryAllowed);
                payload.setBookmarksBarEnabled(bookmarksEnabled);
                payload.setIsEditBookmarksAllowed(isBookMarkEditable);
            }
        }
        catch (final Exception ex) {
            DO2ChromeBrowserSettingsPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
}
