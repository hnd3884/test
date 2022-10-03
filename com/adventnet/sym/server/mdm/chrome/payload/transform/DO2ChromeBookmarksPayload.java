package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeBookmarksPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeBookmarksPayload implements DO2ChromePayload
{
    @Override
    public ChromeBookmarksPayload createPayload(final DataObject dataObject) {
        ChromeBookmarksPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("ManagedBookmarksPolicy");
            while (iterator.hasNext()) {
                payload = new ChromeBookmarksPayload("1.0", "ManagedBookMarks", "ManagedBookMarks");
                final Row bookmarksRow = iterator.next();
                final JSONArray bookmarkArray = new JSONArray();
                final String topName = (String)bookmarksRow.get("FOLDER_NAME");
                final boolean editable = (boolean)bookmarksRow.get("IS_BOOKMARK_EDITABLE");
                final int bookmarkBar = (int)bookmarksRow.get("BOOKMARKS_BAR");
                payload.setEditable(editable);
                payload.setBookmarkBar(bookmarkBar);
                final JSONObject folder = payload.getFolderNameElem(topName);
                if (folder != null) {
                    bookmarkArray.put((Object)folder);
                }
                final Iterator bookmarkDetailsItr = dataObject.getRows("CfgDataItemToUrl");
                while (bookmarkDetailsItr.hasNext()) {
                    final Row bookmarkDetailsRow = bookmarkDetailsItr.next();
                    final Long urlDetailsID = (Long)bookmarkDetailsRow.get("URL_DETAILS_ID");
                    final Criteria urlDetailsCriteria = new Criteria(new Column("URLDetails", "URL_DETAILS_ID"), (Object)urlDetailsID, 0);
                    final Iterator urlIterator = dataObject.getRows("URLDetails", urlDetailsCriteria);
                    while (urlIterator.hasNext()) {
                        final Row urlDetailsRow = urlIterator.next();
                        final String url = (String)urlDetailsRow.get("URL");
                        final String title = (String)urlDetailsRow.get("BOOKMARK_TITILE");
                        final JSONObject whitelistUrlDetail = payload.getUrlJSON(url, title);
                        bookmarkArray.put((Object)whitelistUrlDetail);
                    }
                }
                payload.setURLs(bookmarkArray);
            }
        }
        catch (final Exception ex) {
            DO2ChromeBookmarksPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
}
