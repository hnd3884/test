package com.me.devicemanagement.onpremise.webclient.admin.certificate.manual;

import java.util.Date;
import java.util.logging.LogRecord;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.io.File;
import java.util.logging.Level;
import java.io.Writer;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.io.StringWriter;

public class BatchLogHandler
{
    StringWriter sw;
    PrintWriter pw;
    private Logger logger;
    private String homeLocation;
    
    public BatchLogHandler(final Logger logger, final String homeLocation) {
        this.homeLocation = null;
        this.sw = new StringWriter();
        this.pw = new PrintWriter(this.sw);
        (this.logger = logger).setUseParentHandlers(false);
        this.homeLocation = homeLocation;
        this.setUpLogger();
    }
    
    public Logger getLogger() {
        return this.logger;
    }
    
    public void logExceptionTrace(final Exception ex) {
        ex.printStackTrace(this.pw);
        this.logger.log(Level.SEVERE, this.sw.toString());
    }
    
    public Logger setUpLogger() {
        return this.getFileLogger(this.homeLocation + File.separator + "logs" + File.separator + "importcertificate.log", this.getCustomFormatter());
    }
    
    public Logger getFileLogger(final String fileName, final Formatter formatter) {
        try {
            final boolean append = true;
            final int limit = 204800;
            final String pattern = fileName;
            final int numLogFiles = 50;
            final FileHandler handler = new FileHandler(pattern, limit, numLogFiles, append);
            if (formatter != null) {
                handler.setFormatter(formatter);
            }
            this.logger.addHandler(handler);
        }
        catch (final IOException e) {
            this.logger.info("Log file can't be created.. No privilege granted..");
            this.logExceptionTrace(e);
        }
        return this.logger;
    }
    
    public Formatter getCustomFormatter() {
        return new Formatter() {
            @Override
            public String format(final LogRecord record) {
                final String recordStr = "" + new Date() + " " + record.getSourceMethodName() + " " + record.getLevel() + " " + record.getSourceClassName() + " " + record.getMessage() + "\n";
                return recordStr;
            }
        };
    }
}
