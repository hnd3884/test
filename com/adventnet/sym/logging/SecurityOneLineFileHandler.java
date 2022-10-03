package com.adventnet.sym.logging;

import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.io.IOException;
import com.adventnet.mfw.logging.ExtendedJDKFileHandler;

public class SecurityOneLineFileHandler extends ExtendedJDKFileHandler
{
    public SecurityOneLineFileHandler() throws IOException, SecurityException {
    }
    
    public SecurityOneLineFileHandler(final String pattern) throws IOException, SecurityException {
        super(pattern);
    }
    
    public SecurityOneLineFileHandler(final String pattern, final boolean append) throws IOException, SecurityException {
        super(pattern, append);
    }
    
    public SecurityOneLineFileHandler(final String pattern, final int limit, final int count) throws IOException, SecurityException {
        super(pattern, limit, count);
    }
    
    public SecurityOneLineFileHandler(final String pattern, final int limit, final int count, final boolean append) throws IOException, SecurityException {
        super(pattern, limit, count, append);
    }
    
    public synchronized void publish(final LogRecord var1) {
        if (var1.getMessage().equals("SecurityOnelineLogger")) {
            super.publish(var1);
        }
        else {
            Logger.getLogger(SecurityOneLineFileHandler.class.getName()).log(var1);
        }
    }
}
