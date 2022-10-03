package com.zoho.mickey.ha;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultErrorHandler implements HAErrorHandler
{
    private static final Logger OUT;
    
    @Override
    public void handleError(final HAException exp) {
        DefaultErrorHandler.OUT.log(Level.SEVERE, "ERROR CODE: [ {0} ] DESC [ {1} ]", new Object[] { exp.getErrCode(), exp.getErrDesc() });
        exp.printStackTrace();
        if (exp.getErrCode() > 1000) {
            throw new RuntimeException(exp);
        }
    }
    
    @Override
    public void handleReplicationError(final int exitCode, final String dirName, final String message, final boolean onTakeOver) {
        DefaultErrorHandler.OUT.log(Level.SEVERE, "Replication Exit code:[ {0} ] dir name:[ {1} ] message:[ {2} ]", new Object[] { exitCode, dirName, message });
        if (exitCode == 16 && onTakeOver) {
            throw new RuntimeException("Error in replication directory" + dirName);
        }
    }
    
    static {
        OUT = Logger.getLogger(DefaultErrorHandler.class.getName());
    }
}
