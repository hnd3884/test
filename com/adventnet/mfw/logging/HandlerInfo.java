package com.adventnet.mfw.logging;

public class HandlerInfo
{
    private String archivedir;
    private String handlerName;
    private String handlerPreFix;
    private String handlerSuffix;
    private String logDir;
    private int noOfDays;
    private boolean isArchiveEnabled;
    private int maxFilesPerDay;
    
    public HandlerInfo() {
    }
    
    public HandlerInfo(final String name, final String prefix, final String suffix, final String logdir, final String archDir, final int days, final boolean enable, final int maxFilesPerDay) {
        this.handlerName = name;
        this.archivedir = archDir;
        this.handlerPreFix = prefix;
        this.handlerSuffix = suffix;
        this.logDir = logdir;
        this.noOfDays = days;
        this.isArchiveEnabled = enable;
        this.maxFilesPerDay = maxFilesPerDay;
    }
    
    public int getMaxFilesPerDay() {
        return this.maxFilesPerDay;
    }
    
    public String getPrefix() {
        return this.handlerPreFix;
    }
    
    public String getSuffix() {
        return this.handlerSuffix;
    }
    
    public String getArchiveDir() {
        return this.archivedir;
    }
    
    public String getLogDir() {
        return this.logDir;
    }
    
    public String getHandlerName() {
        return this.handlerName;
    }
    
    public int getArchiveInterval() {
        return this.noOfDays;
    }
    
    public boolean isArchiveEnabled() {
        return this.isArchiveEnabled;
    }
    
    public void setPrefix(final String prefix) {
        this.handlerPreFix = prefix;
    }
    
    public void setSuffix(final String suffix) {
        this.handlerSuffix = suffix;
    }
    
    public void setArchiveDir(final String archDir) {
        this.archivedir = archDir;
    }
    
    public void setLogDir(final String dirName) {
        this.logDir = dirName;
    }
    
    public void setHandlerName(final String name) {
        this.handlerName = name;
    }
    
    public void setArchiveInterval(final int days) {
        this.noOfDays = days;
    }
    
    public void setEnableArchive(final boolean enable) {
        this.isArchiveEnabled = enable;
    }
}
