package com.adventnet.sym.server.mdm.message;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class AETMessageHandler implements MessageListener
{
    public static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        Boolean isClose = Boolean.FALSE;
        final Criteria wpAppSettingsCri = new Criteria(new Column("WpAppSettings", "CUSTOMER_ID"), (Object)customerId, 0);
        try {
            final int aetCount = DBUtil.getRecordCount("WpAppSettings", "CUSTOMER_ID", wpAppSettingsCri);
            if (aetCount > 0) {
                isClose = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            AETMessageHandler.logger.log(Level.SEVERE, "Exception while getting App Settings message status", ex);
        }
        return isClose;
    }
    
    static {
        AETMessageHandler.logger = Logger.getLogger("MDMLogger");
    }
}
