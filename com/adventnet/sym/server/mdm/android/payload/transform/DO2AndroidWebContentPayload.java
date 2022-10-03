package com.adventnet.sym.server.mdm.android.payload.transform;

import org.json.JSONObject;
import java.util.Iterator;
import org.json.JSONArray;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.android.payload.AndroidWebContentPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public class DO2AndroidWebContentPayload implements DO2AndroidPayload
{
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidWebContentPayload andWebContentPayload = null;
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("IOSWebContentPolicy");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    andWebContentPayload = new AndroidWebContentPayload("1.0", "com.mdm.mobiledevice.webcontent", "Web Content Filter Policy");
                    final long configDataItemId = (long)row.get("CONFIG_DATA_ITEM_ID");
                    final Criteria CfgDataItemCriteria = new Criteria(new Column("URLRestrictionDetails", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                    final Join urlDetailsJoin = new Join("URLRestrictionDetails", "URLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 2);
                    final Iterator urlIterator = dataObject.getRows("URLDetails", CfgDataItemCriteria, urlDetailsJoin);
                    final boolean maliciousContentFilter = (boolean)row.get("MALICIOUS_CONTENT_FILTER");
                    andWebContentPayload.setMaliciousContentFilter(maliciousContentFilter);
                    if (row.get("URL_FILTER_TYPE")) {
                        final JSONArray whitelistUrlArray = new JSONArray();
                        while (urlIterator.hasNext()) {
                            final Row urlDetailsRow = urlIterator.next();
                            final String url = (String)urlDetailsRow.get("URL");
                            final String title = (String)urlDetailsRow.get("BOOKMARK_TITILE");
                            final String path = (String)urlDetailsRow.get("BOOKMARK_PATH");
                            final JSONObject whitelistUrlDetail = andWebContentPayload.getUrlJSON(url, title, path);
                            whitelistUrlArray.put((Object)whitelistUrlDetail);
                        }
                        andWebContentPayload.setWhitelistURLs(whitelistUrlArray);
                    }
                    else {
                        final JSONArray blacklistUrlArray = new JSONArray();
                        while (urlIterator.hasNext()) {
                            final Row urlDetailsRow = urlIterator.next();
                            final String url = (String)urlDetailsRow.get("URL");
                            blacklistUrlArray.put((Object)url);
                        }
                        andWebContentPayload.setBlacklistURLs(blacklistUrlArray);
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return andWebContentPayload;
    }
}
