package com.adventnet.iam.security;

import java.io.File;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_CONSUMPTION_ANOMALY;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class TempFileUploadDirMonitoring implements Runnable
{
    private static final Logger LOGGER;
    private ScheduledFuture<?> handle;
    private long thresholdSize;
    private long scheduleTime;
    private boolean enable;
    private static final Pattern SINGLE_SIZE_PATTERN;
    
    public TempFileUploadDirMonitoring(final boolean enable, final long thresholdSize, final long scheduleTime) {
        this.enable = enable;
        this.thresholdSize = thresholdSize;
        this.scheduleTime = scheduleTime;
    }
    
    public static long getSizeInBytes(final String sizeAsString) {
        SIZE_UNIT unit = SIZE_UNIT.b;
        try {
            final Matcher matcher = TempFileUploadDirMonitoring.SINGLE_SIZE_PATTERN.matcher(sizeAsString);
            if (matcher.matches()) {
                final long size = Long.parseLong(matcher.group(1));
                final String sizeUnit = matcher.group(2);
                if (sizeUnit != null) {
                    unit = SIZE_UNIT.valueOf(sizeUnit.toLowerCase());
                }
                return size * unit.value();
            }
            TempFileUploadDirMonitoring.LOGGER.log(Level.SEVERE, " The given size unit  : {0} is invalid", new Object[] { sizeAsString });
            throw new RuntimeException(String.format(" The given size unit : %s is invalid", sizeAsString));
        }
        catch (final IllegalArgumentException e) {
            TempFileUploadDirMonitoring.LOGGER.log(Level.SEVERE, " The given size unit  : {0} is invalid , exception {1} ", new Object[] { sizeAsString, e.getMessage() });
            throw new RuntimeException(String.format("The given size unit  : %s is invalid", sizeAsString), e);
        }
    }
    
    public void setThresholdSize(final long thresholdSize) {
        this.thresholdSize = thresholdSize;
    }
    
    public void setScheduleTime(final long time) {
        this.scheduleTime = time;
    }
    
    public void setEnable(final boolean enable) {
        this.enable = enable;
    }
    
    public long getThresholdSize() {
        return this.thresholdSize;
    }
    
    public long getScheduleTime() {
        return this.scheduleTime;
    }
    
    public boolean isEnable() {
        return this.enable;
    }
    
    void init() {
        if (this.isEnable()) {
            this.handle = SecurityUtil.getWafScheduler().scheduleWithFixedDelay(this, 0L, this.getScheduleTime(), TimeUnit.MILLISECONDS);
        }
    }
    
    void reinit() {
        this.handle.cancel(false);
        this.init();
    }
    
    @Override
    public void run() {
        try {
            final File tempFileUploadDir = SecurityUtil.getTempFileUploadDir();
            final long consumedSize = SecurityUtil.sizeOfDirectory(tempFileUploadDir);
            if (consumedSize >= this.thresholdSize) {
                final long totalDiskSize = tempFileUploadDir.getTotalSpace();
                final long availableDiskSize = tempFileUploadDir.getFreeSpace();
                final long usedDiskSize = totalDiskSize - availableDiskSize;
                ZSEC_CONSUMPTION_ANOMALY.pushFileUploadDiskConsumptionAlert(tempFileUploadDir.getAbsolutePath(), totalDiskSize, availableDiskSize, usedDiskSize, consumedSize, this.thresholdSize, (ExecutionTimer)null);
            }
        }
        catch (final Exception ex) {
            TempFileUploadDirMonitoring.LOGGER.log(Level.SEVERE, "Exception occured during monitoring of the temporary fileupload directory", ex);
        }
        catch (final Throwable t) {
            TempFileUploadDirMonitoring.LOGGER.log(Level.SEVERE, "Throwable occured during monitoring of the temporary fileupload directory", t);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(TempFileUploadDirMonitoring.class.getName());
        SINGLE_SIZE_PATTERN = Pattern.compile("([0-9]+)(kb|mb|gb|tb)?");
    }
    
    private enum SIZE_UNIT
    {
        b(1L), 
        kb(1024L), 
        mb(1048576L), 
        gb(1073741824L);
        
        private long value;
        
        private SIZE_UNIT(final long converstionValue) {
            this.value = converstionValue;
        }
        
        public long value() {
            return this.value;
        }
    }
}
