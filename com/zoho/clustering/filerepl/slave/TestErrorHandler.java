package com.zoho.clustering.filerepl.slave;

import java.util.logging.Level;
import com.zoho.clustering.filerepl.ErrorHandler;

public class TestErrorHandler implements ErrorHandler
{
    @Override
    public void handleError(final Exception exp) {
        ReplSlave.logger().log(Level.SEVERE, "ReplSlaveModule.ErrorHandler.onError() invoked ...", exp);
        ReplSlave.logger().log(Level.SEVERE, "Calling System.exit(1)");
        System.exit(1);
    }
}
