package com.me.mdm.server.windows.profile.payload.transform;

import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import org.apache.commons.codec.binary.Base64;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayInputStream;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.WindowsCustomPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2WindowsCustomPayload extends DO2WindowsPayload
{
    public Logger logger;
    
    public DO2WindowsCustomPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        final WindowsCustomPayload windowsCustomPayload = new WindowsCustomPayload();
        try {
            final Iterator rowsIterator = dataObject.getRows("WindowsCustomProfilesData");
            while (rowsIterator.hasNext()) {
                final Row profileDataRow = rowsIterator.next();
                String locURI = (String)profileDataRow.get("LOC_URI");
                final int actionType = (int)profileDataRow.get("ACTION_TYPE");
                final int dataType = (int)profileDataRow.get("DATA_TYPE");
                String data = (String)profileDataRow.get("DATA");
                final Row extnRow = dataObject.getRow("WindowsCustomProfilesDataExtn", new Criteria(Column.getColumn("WindowsCustomProfilesDataExtn", "CUSTOM_PROFILE_DATA_ID"), profileDataRow.get("CUSTOM_PROFILE_DATA_ID"), 0));
                if (extnRow != null) {
                    final InputStream inputStream = (ByteArrayInputStream)extnRow.get("DATA_BLOB");
                    final String dataBlob = IOUtils.toString(inputStream);
                    if (!MDMStringUtils.isEmpty(dataBlob)) {
                        data = dataBlob;
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
                if (!MDMStringUtils.isEmpty(data)) {
                    data = new String(Base64.decodeBase64(data));
                }
                locURI = locURI.replaceAll(" ", "%20");
                windowsCustomPayload.addCommand(locURI, actionType, dataType, data);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Windows custom profile creation failed ", ex);
        }
        return windowsCustomPayload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        final WindowsCustomPayload windowsCustomPayload = new WindowsCustomPayload();
        try {
            final Iterator rowsIterator = dataObject.getRows("WindowsCustomProfilesData");
            while (rowsIterator.hasNext()) {
                final Row profileDataRow = rowsIterator.next();
                final String locURI = (String)profileDataRow.get("LOC_URI");
                final int actionType = (int)profileDataRow.get("ACTION_TYPE");
                if (actionType != 2) {
                    windowsCustomPayload.addNonAtomicDelete(locURI);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Windows custom profile creation failed ", ex);
        }
        return windowsCustomPayload;
    }
}
