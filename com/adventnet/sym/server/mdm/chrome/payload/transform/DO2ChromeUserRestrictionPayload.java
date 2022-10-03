package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeUserRestrictionPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeUserRestrictionPayload implements DO2ChromePayload
{
    @Override
    public ChromeUserRestrictionPayload createPayload(final DataObject dataObject) {
        ChromeUserRestrictionPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("ChromeUserRestrictions");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new ChromeUserRestrictionPayload("1.0", "UserRestriction", "UserRestriction");
                payload.setAllowIncognito((boolean)row.get("ALLOW_INCOGNITO"));
                payload.setShowHomeButton((int)row.get("SHOW_HOME_BUTTON"));
                payload.setAllowPrinting((boolean)row.get("ALLOW_PRINTING"));
                payload.setEndProcess((boolean)row.get("ALLOW_END_PROCESS"));
                payload.setExternalStorageAccess((int)row.get("EXTERNAL_STORAGE_ACCESSIBILITY"));
                payload.setDisableScreenLock((boolean)row.get("DISABLE_SCREENLOCK"));
            }
        }
        catch (final Exception ex) {
            DO2ChromeUserRestrictionPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
}
