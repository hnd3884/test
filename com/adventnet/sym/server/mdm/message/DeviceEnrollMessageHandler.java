package com.adventnet.sym.server.mdm.message;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class DeviceEnrollMessageHandler implements MessageListener
{
    private static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        Boolean isClose = Boolean.FALSE;
        try {
            final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final int count = ManagedDeviceHandler.getInstance().getManagedDeviceCount(customerCri);
            if (count > 0) {
                isClose = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            DeviceEnrollMessageHandler.logger.log(Level.SEVERE, "Exception while getting enrolled device count", ex);
        }
        return isClose;
    }
    
    static {
        DeviceEnrollMessageHandler.logger = Logger.getLogger("MDMLogger");
    }
}
