package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeApplicationPolicyPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeApplicationPolicyPayload implements DO2ChromePayload
{
    @Override
    public ChromeApplicationPolicyPayload createPayload(final DataObject dataObject) {
        ChromeApplicationPolicyPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("ApplicationPolicyConfig");
            while (iterator.hasNext()) {
                iterator.next();
                payload = new ChromeApplicationPolicyPayload("1.0", "ExtensionInstallSources", "ExtensionInstallSources");
                final JSONArray urlArray = new JSONArray();
                final Iterator urlItr = dataObject.getRows("CfgDataItemToUrl");
                while (urlItr.hasNext()) {
                    final Row urlRow = urlItr.next();
                    final Long urlDetailsID = (Long)urlRow.get("URL_DETAILS_ID");
                    final Criteria urlDetailsCriteria = new Criteria(new Column("URLDetails", "URL_DETAILS_ID"), (Object)urlDetailsID, 0);
                    final Iterator urlIterator = dataObject.getRows("URLDetails", urlDetailsCriteria);
                    while (urlIterator.hasNext()) {
                        final Row urlDetailsRow = urlIterator.next();
                        final String url = (String)urlDetailsRow.get("URL");
                        urlArray.put((Object)url);
                    }
                }
                payload.setAllowedURLs(urlArray);
            }
        }
        catch (final Exception ex) {
            DO2ChromeApplicationPolicyPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
}
