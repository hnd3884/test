package com.me.devicemanagement.framework.server.tinyurl;

import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class TinyURLUtil
{
    public static final Logger LOGGER;
    
    public long addOrUpdateQRImageURL(final Long tinyUrlId, final String qrImageUrl) throws Exception {
        try {
            final Criteria tinyURLCriteria = new Criteria(Column.getColumn("TinyUrl", "TINY_URL_ID"), (Object)tinyUrlId, 0);
            final DataObject tinyUrlObject = SyMUtil.getPersistence().get("TinyUrl", tinyURLCriteria);
            if (tinyUrlObject.isEmpty()) {
                TinyURLUtil.LOGGER.log(Level.INFO, "TinyURLUtil: Tiny URL not found for Tiny URL Id : " + tinyUrlId);
                return -1L;
            }
            final Row tinyUrlRow = tinyUrlObject.getRow("TinyUrl");
            tinyUrlRow.set("QR_URL", (Object)qrImageUrl);
            tinyUrlObject.updateRow(tinyUrlRow);
            SyMUtil.getPersistence().update(tinyUrlObject);
            TinyURLUtil.LOGGER.log(Level.INFO, "TinyURLUtil: QR image url successfully associated with Tiny URL Id : " + tinyUrlId);
        }
        catch (final Exception e) {
            throw new Exception("Error in persisting QR image URL");
        }
        return tinyUrlId;
    }
    
    public long addOrUpdateTinyURL(final String tinyUrl) throws Exception {
        long tinyURLId = -1L;
        try {
            final DataObject tinyUrlObject = (DataObject)new WritableDataObject();
            Row tinyUrlRow = new Row("TinyUrl");
            tinyUrlRow.set("TINY_URL", (Object)tinyUrl);
            tinyUrlObject.addRow(tinyUrlRow);
            SyMUtil.getPersistence().add(tinyUrlObject);
            final DataObject tinyUrlDataObject = SyMUtil.getPersistence().get("TinyUrl", tinyUrlRow);
            tinyUrlRow = tinyUrlDataObject.getFirstRow("TinyUrl");
            tinyURLId = (long)tinyUrlRow.get("TINY_URL_ID");
            TinyURLUtil.LOGGER.log(Level.INFO, "TinyURLUtil: TinyUrl successfully added : " + tinyURLId);
        }
        catch (final Exception e) {
            throw new Exception("Error in persisting Tiny URL");
        }
        return tinyURLId;
    }
    
    static {
        LOGGER = Logger.getLogger("TinyURLUtil");
    }
}
