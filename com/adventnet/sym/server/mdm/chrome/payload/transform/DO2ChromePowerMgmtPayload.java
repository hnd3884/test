package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromePowerMgmtPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromePowerMgmtPayload implements DO2ChromePayload
{
    @Override
    public ChromePowerMgmtPayload createPayload(final DataObject dataObject) {
        ChromePowerMgmtPayload powerMgmtPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("PowerManagementSettings");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                powerMgmtPayload = new ChromePowerMgmtPayload("1.0", "PowerIdleManagement", "PowerIdleManagement");
                powerMgmtPayload.initPayloadData();
                powerMgmtPayload.setWarningTimeout((int)row.get("IDLE_WARNING_TIMEOUT_AC"), 1);
                powerMgmtPayload.setScreenOffTimeout((int)row.get("SCREENOFF_TIMEOUT_AC"), 1);
                powerMgmtPayload.setIdleTimeout((int)row.get("IDLE_TIMEOUT_AC"), 1);
                powerMgmtPayload.setScreenDimTimeout((int)row.get("SCREEN_DIM_TIMEOUT_AC"), 1);
                powerMgmtPayload.setIdleAction((int)row.get("IDLE_ACTION_AC"), 1);
                powerMgmtPayload.setWarningTimeout((int)row.get("IDLE_WARNING_TIMEOUT_DC"), 2);
                powerMgmtPayload.setScreenOffTimeout((int)row.get("SCREENOFF_TIMEOUT_DC"), 2);
                powerMgmtPayload.setIdleTimeout((int)row.get("IDLE_TIMEOUT_DC"), 2);
                powerMgmtPayload.setScreenDimTimeout((int)row.get("SCREEN_DIM_TIMEOUT_DC"), 2);
                powerMgmtPayload.setIdleAction((int)row.get("IDLE_ACTION_DC"), 2);
            }
        }
        catch (final Exception ex) {
            DO2ChromePowerMgmtPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return powerMgmtPayload;
    }
}
