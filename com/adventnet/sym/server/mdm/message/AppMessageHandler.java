package com.adventnet.sym.server.mdm.message;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class AppMessageHandler implements MessageListener
{
    public static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        Boolean isClose = Boolean.FALSE;
        final Criteria cProfile = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)2, 0);
        try {
            final int profileCount = DBUtil.getRecordCount("Profile", "PROFILE_ID", cProfile);
            if (profileCount > 0) {
                isClose = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            AppMessageHandler.logger.log(Level.SEVERE, "Exception while getting profile message status", ex);
        }
        return isClose;
    }
    
    static {
        AppMessageHandler.logger = Logger.getLogger("MDMLogger");
    }
}
