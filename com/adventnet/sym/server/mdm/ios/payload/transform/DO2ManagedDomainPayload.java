package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.ios.payload.ManagedDomainPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2ManagedDomainPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        Iterator iterator = null;
        ManagedDomainPayload payload = null;
        final IOSPayload[] payloadArray = new IOSPayload[2];
        try {
            iterator = dataObject.getRows("ManagedWebDomainPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new ManagedDomainPayload(1, "MDM", "com.mdm.mobiledevice.manageddomain", "ManagedDomain");
                final Long policyId = (Long)row.get("CONFIG_DATA_ITEM_ID");
                final Criteria cpolicy = new Criteria(new Column("ManagedWebDomainPolicy", "CONFIG_DATA_ITEM_ID"), (Object)policyId, 0);
                final Iterator urliterator = dataObject.getRows("ManagedWebDomainURLDetails", cpolicy);
                final List<String> url = new ArrayList<String>();
                while (urliterator.hasNext()) {
                    final Row urldetails = urliterator.next();
                    final String urlstring = (String)urldetails.get("URL");
                    url.add(urlstring);
                }
                payload.setManagedDomainURL(url);
            }
            payloadArray[0] = payload;
            final DO2RestrictionsPolicyPayload autoRestrict = new DO2RestrictionsPolicyPayload();
            payloadArray[1] = autoRestrict.autoCreateRestrictionPayload(517, dataObject);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.INFO, "Error in creating Do2manageddomain", e);
        }
        return payloadArray;
    }
}
