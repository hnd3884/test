package com.adventnet.sym.server.mdm.message;

import java.util.List;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.logging.Logger;

public class GroupMessageHandler implements MessageListener
{
    private static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        Boolean isClose = Boolean.FALSE;
        final List groupList = MDMGroupHandler.getMDMGroupType();
        groupList.add(7);
        final Criteria cType = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)groupList.toArray(), 8);
        try {
            final int profileCount = DBUtil.getRecordCount("CustomGroup", "RESOURCE_ID", cType);
            if (profileCount > 0) {
                isClose = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            GroupMessageHandler.logger.log(Level.SEVERE, "Exception while getting profile message status", ex);
        }
        return isClose;
    }
    
    static {
        GroupMessageHandler.logger = Logger.getLogger("MDMLogger");
    }
}
