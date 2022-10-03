package com.me.devicemanagement.framework.server.message;

import java.util.logging.Level;
import com.adventnet.persistence.OperationInfo;
import java.util.logging.Logger;
import com.adventnet.mfw.message.MessageFilter;

public class MickeyMessageFilter implements MessageFilter
{
    Logger logger;
    
    public MickeyMessageFilter() {
        this.logger = Logger.getLogger("ProbeSyncLogger");
    }
    
    public boolean matches(final Object obj) {
        final OperationInfo oi = (OperationInfo)obj;
        if (oi.getOperation() == 1) {
            this.logger.log(Level.FINE, "operation notified: add");
            return true;
        }
        if (oi.getOperation() == 2) {
            this.logger.log(Level.FINE, "operation notified: update");
            return true;
        }
        if (oi.getOperation() == 3) {
            this.logger.log(Level.FINE, "operation notified: delete");
            return true;
        }
        this.logger.log(Level.FINE, "operation notified: others");
        return false;
    }
}
