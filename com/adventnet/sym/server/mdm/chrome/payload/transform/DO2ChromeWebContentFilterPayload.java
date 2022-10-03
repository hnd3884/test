package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeWebContentFilterPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeWebContentFilterPayload implements DO2ChromePayload
{
    @Override
    public ChromeWebContentFilterPayload createPayload(final DataObject dataObject) {
        ChromeWebContentFilterPayload webContentPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("IOSWebContentPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                webContentPayload = new ChromeWebContentFilterPayload("1.0", "WebContent", "WebContent");
                final long configDataItemId = (long)row.get("CONFIG_DATA_ITEM_ID");
                final Criteria CfgDataItemCriteria = new Criteria(new Column("CfgDataItemToUrl", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                final Join urlDetailsJoin = new Join("CfgDataItemToUrl", "URLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 2);
                final Iterator urlIterator = dataObject.getRows("URLDetails", CfgDataItemCriteria, urlDetailsJoin);
                if (row.get("URL_FILTER_TYPE")) {
                    final JSONArray whitelistUrlArray = new JSONArray();
                    final Boolean enableBookmarks = (Boolean)row.get("CREATE_BOOKMARKS");
                    webContentPayload.setEnableBookmarks(enableBookmarks);
                    while (urlIterator.hasNext()) {
                        final Row urlDetailsRow = urlIterator.next();
                        final String url = (String)urlDetailsRow.get("URL");
                        String title = "";
                        if (enableBookmarks) {
                            title = (String)urlDetailsRow.get("BOOKMARK_TITILE");
                        }
                        final JSONObject whitelistUrlDetail = webContentPayload.getUrlJSON(url, title);
                        whitelistUrlArray.put((Object)whitelistUrlDetail);
                    }
                    webContentPayload.setWhitelistURLs(whitelistUrlArray);
                }
                else {
                    final JSONArray blacklistUrlArray = new JSONArray();
                    while (urlIterator.hasNext()) {
                        final Row urlDetailsRow2 = urlIterator.next();
                        final String url2 = (String)urlDetailsRow2.get("URL");
                        blacklistUrlArray.put((Object)url2);
                    }
                    webContentPayload.setBlacklistURLs(blacklistUrlArray);
                }
            }
        }
        catch (final Exception ex) {
            DO2ChromeWebContentFilterPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return webContentPayload;
    }
}
