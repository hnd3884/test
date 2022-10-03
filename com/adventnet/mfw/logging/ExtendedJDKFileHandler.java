package com.adventnet.mfw.logging;

import java.io.File;
import com.zoho.conf.Configuration;
import java.io.IOException;
import java.util.logging.FileHandler;

public class ExtendedJDKFileHandler extends FileHandler
{
    public ExtendedJDKFileHandler() throws IOException, SecurityException {
    }
    
    public ExtendedJDKFileHandler(final String pattern) throws IOException, SecurityException {
        super(pattern);
    }
    
    public ExtendedJDKFileHandler(final String pattern, final boolean append) throws IOException, SecurityException {
        super(pattern, append);
    }
    
    public ExtendedJDKFileHandler(final String pattern, final int limit, final int count) throws IOException, SecurityException {
        super(pattern, limit, count);
    }
    
    public ExtendedJDKFileHandler(final String pattern, final int limit, final int count, final boolean append) throws IOException, SecurityException {
        super(pattern, limit, count, append);
    }
    
    static {
        final String serverHome = (Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home");
        final String logDir = serverHome + File.separator + "logs";
        final File file = new File(logDir);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}
