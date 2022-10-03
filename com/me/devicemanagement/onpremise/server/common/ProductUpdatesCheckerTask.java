package com.me.devicemanagement.onpremise.server.common;

import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.io.File;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ProductUpdatesCheckerTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public ProductUpdatesCheckerTask() {
        (this.logger = Logger.getLogger(ProductUpdatesCheckerTask.class.getName())).log(Level.INFO, "ProductUpdatesCheckerTask() instance created.");
    }
    
    public void executeTask(final Properties taskProps) {
        final Long currTime = new Long(System.currentTimeMillis());
        this.logger.log(Level.INFO, "ProductUpdatesCheckerTask Task is invoked at " + SyMUtil.getDate((long)currTime));
        try {
            final String outFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "updates.json";
            final String updChkURL = ProductUrlLoader.getInstance().getValue("updates_check_url");
            if (!this.isFileDownloadedSuccessfully(outFileName, updChkURL)) {
                return;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception while executing ProductUpdatesCheckerTask", ex);
        }
    }
    
    public boolean isFileDownloadedSuccessfully(final String outFileName, final String updChkURL) {
        final File uFile = new File(outFileName);
        if (!uFile.exists()) {
            this.logger.log(Level.WARNING, "ProductUpdatesCheckerTask: Copying " + updChkURL + " to local file system is failed.");
            SyMUtil.updateSyMParameter("UPDATES_CHECK_STATUS", "failed");
            return false;
        }
        this.logger.log(Level.WARNING, "ProductUpdatesCheckerTask: Copying " + updChkURL + " to local file system is success.");
        SyMUtil.updateSyMParameter("UPDATES_CHECK_STATUS", "success");
        return true;
    }
    
    public void updateProductSpecificDetails(final JSONObject otherProps) {
        this.logger.log(Level.WARNING, "ProductUpdatesCheckerTask:updateProductSpecificDetails()  ");
    }
    
    @Override
    public String toString() {
        return "ProductUpdatesCheckerTask";
    }
}
