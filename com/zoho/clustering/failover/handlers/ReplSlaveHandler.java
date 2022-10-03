package com.zoho.clustering.failover.handlers;

import com.zoho.clustering.failover.ErrorCode;
import com.zoho.clustering.filerepl.slave.ReplSlaveModule;
import com.zoho.clustering.util.MyProperties;
import com.zoho.clustering.failover.FOS;
import com.zoho.clustering.filerepl.slave.ReplSlave;
import java.util.logging.Logger;
import com.zoho.clustering.failover.FOSHandler;

public class ReplSlaveHandler implements FOSHandler
{
    private static Logger logger;
    private String confFilePath;
    private String masterURL;
    private ReplSlave replSlave;
    
    public ReplSlaveHandler(final String confFilePath, final String masterURL) {
        this.confFilePath = confFilePath;
        this.masterURL = masterURL;
    }
    
    @Override
    public void onStart(final FOS.Mode mode) {
        ReplSlaveHandler.logger.fine("FileReplSlaveHandler.onStart() invoked");
        if (mode == FOS.Mode.SLAVE) {
            ReplSlaveModule.initialize(new MyProperties(this.confFilePath), this.masterURL);
            (this.replSlave = ReplSlaveModule.getInst()).start();
        }
    }
    
    @Override
    public void onStop(final FOS.Mode mode, final ErrorCode errorCode) {
        FOS.Console.out("ReplSlaveHandler.onStop() invoked");
        if (mode == FOS.Mode.SLAVE && this.replSlave != null) {
            this.replSlave.stop();
        }
    }
    
    @Override
    public void onSlaveTakeover() {
        ReplSlaveHandler.logger.fine("FileReplSlaveHandler.onSlaveTakeover() invoked");
        if (this.replSlave != null) {
            this.replSlave.stop();
        }
    }
    
    static {
        ReplSlaveHandler.logger = Logger.getLogger(FOSHandler.class.getName());
    }
}
