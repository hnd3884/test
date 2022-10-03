package com.zoho.clustering.failover.handlers;

import java.io.Closeable;
import com.zoho.clustering.util.FileUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.zoho.clustering.failover.ErrorCode;
import java.util.logging.Level;
import java.io.File;
import com.zoho.clustering.failover.FOS;
import java.util.Properties;
import java.util.logging.Logger;
import com.zoho.clustering.failover.FOSHandler;

public class TakeoverFileHandler implements FOSHandler
{
    private static Logger logger;
    private String filePath;
    private Properties properties;
    
    public TakeoverFileHandler(final String filePath, final Properties properties) {
        this.filePath = filePath;
        this.properties = properties;
    }
    
    @Override
    public void onStart(final FOS.Mode mode) {
        TakeoverFileHandler.logger.fine("TakeoverFileHandler.onStart() invoked");
        this.cleanupTakeoverFile();
    }
    
    private void cleanupTakeoverFile() {
        final File file = new File(this.filePath);
        if (file.exists()) {
            if (file.delete()) {
                TakeoverFileHandler.logger.log(Level.INFO, "Deleted the takeover file [" + this.filePath + "]");
            }
            else {
                TakeoverFileHandler.logger.log(Level.WARNING, "Not able to delete 'takeover' file [" + this.filePath + "]");
                FOS.Console.out("TakeoverFile deletion failed !!! Please remove the file [" + file.getAbsolutePath() + "]");
            }
        }
    }
    
    @Override
    public void onStop(final FOS.Mode mode, final ErrorCode errorCode) {
        FOS.Console.out("TakeoverFileHandler.onStop() invoked");
        this.cleanupTakeoverFile();
    }
    
    @Override
    public void onSlaveTakeover() {
        TakeoverFileHandler.logger.fine("TakeoverFileHandler.onSlaveTakeover() invoked");
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(this.filePath);
            this.properties.store(fout, "Genarated when this node took over as 'Master'");
        }
        catch (final IOException exp) {
            throw new RuntimeException("Problem while creating 'takeover' file [" + this.filePath + "]", exp);
        }
        finally {
            FileUtil.Close(fout);
        }
        TakeoverFileHandler.logger.log(Level.INFO, "Created the takeover file [" + this.filePath + "]");
    }
    
    static {
        TakeoverFileHandler.logger = Logger.getLogger(FOSHandler.class.getName());
    }
}
