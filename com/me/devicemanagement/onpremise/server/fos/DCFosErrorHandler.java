package com.me.devicemanagement.onpremise.server.fos;

import java.util.logging.Level;
import com.adventnet.persistence.fos.FOSException;
import java.util.logging.Logger;
import com.adventnet.persistence.fos.FOSErrorHandler;
import com.adventnet.persistence.fos.DefaultErrorHandler;

public class DCFosErrorHandler extends DefaultErrorHandler implements FOSErrorHandler
{
    private static final Logger OUT;
    
    public void handleError(final FOSException exp) {
        DCFosErrorHandler.OUT.log(Level.SEVERE, "HandleError called with error code " + exp.getErrCode());
        if (exp.getErrCode() == 12 || exp.getErrCode() == 1008) {
            DCFosErrorHandler.OUT.log(Level.SEVERE, "Complete Pending Replication has failed, Updated FosParams table with flag ");
            FosUtil.updateFosParam("true");
        }
        super.handleError(exp);
    }
    
    public void handleReplicationError(final int exitCode, final String dirName, final String message, final boolean onTakeover) {
        if (onTakeover && exitCode == 16) {
            DCFosErrorHandler.OUT.log(Level.SEVERE, "Complete Pending Replication is set to true");
            FosUtil.updateFosParam("true");
        }
        FosUtil.incrementReplicationErrorFrequency(exitCode);
        DCFosErrorHandler.OUT.log(Level.SEVERE, "Replication Exit code:[ {0} ] dir name:[ {1} ] message:[ {2} ]", new Object[] { exitCode, dirName, message });
    }
    
    static {
        OUT = Logger.getLogger(DefaultErrorHandler.class.getName());
    }
}
