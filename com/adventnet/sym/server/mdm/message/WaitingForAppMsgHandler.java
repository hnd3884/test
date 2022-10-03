package com.adventnet.sym.server.mdm.message;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class WaitingForAppMsgHandler implements MessageListener
{
    private static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        Boolean isClose = Boolean.FALSE;
        try {
            final Criteria cWaiting = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)3, 0);
            final int count = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", cWaiting);
            if (count == 0) {
                isClose = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            WaitingForAppMsgHandler.logger.log(Level.SEVERE, "Exception while getting yet to enroll devices", ex);
        }
        return isClose;
    }
    
    static {
        WaitingForAppMsgHandler.logger = Logger.getLogger("MDMLogger");
    }
}
