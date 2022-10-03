package org.apache.axiom.attachments;

import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Timer;
import java.util.HashMap;
import org.apache.commons.logging.Log;

public final class AttachmentCacheMonitor
{
    static Log log;
    private int attachmentTimeoutSeconds;
    private int refreshSeconds;
    public static final String ATTACHMENT_TIMEOUT_PROPERTY = "org.apache.axiom.attachments.tempfile.expiration";
    private HashMap files;
    private Long priorDeleteMillis;
    private Timer timer;
    private static AttachmentCacheMonitor _singleton;
    
    public static synchronized AttachmentCacheMonitor getAttachmentCacheMonitor() {
        if (AttachmentCacheMonitor._singleton == null) {
            AttachmentCacheMonitor._singleton = new AttachmentCacheMonitor();
        }
        return AttachmentCacheMonitor._singleton;
    }
    
    private AttachmentCacheMonitor() {
        this.attachmentTimeoutSeconds = 0;
        this.refreshSeconds = 0;
        this.files = new HashMap();
        this.priorDeleteMillis = this.getTime();
        this.timer = null;
        String value = "";
        try {
            value = System.getProperty("org.apache.axiom.attachments.tempfile.expiration", "0");
            this.attachmentTimeoutSeconds = Integer.valueOf(value);
        }
        catch (final Throwable t) {
            if (AttachmentCacheMonitor.log.isDebugEnabled()) {
                AttachmentCacheMonitor.log.debug((Object)("The value of " + value + " was not valid. The default " + this.attachmentTimeoutSeconds + " will be used instead."));
            }
        }
        this.refreshSeconds = this.attachmentTimeoutSeconds / 2;
        if (AttachmentCacheMonitor.log.isDebugEnabled()) {
            AttachmentCacheMonitor.log.debug((Object)"Custom Property Key =  org.apache.axiom.attachments.tempfile.expiration");
            AttachmentCacheMonitor.log.debug((Object)("              Value = " + this.attachmentTimeoutSeconds));
        }
        if (this.refreshSeconds > 0) {
            (this.timer = new Timer(true)).schedule(new CleanupFilesTask(), this.refreshSeconds * 1000, this.refreshSeconds * 1000);
        }
    }
    
    public synchronized int getTimeout() {
        return this.attachmentTimeoutSeconds;
    }
    
    public synchronized void setTimeout(final int timeout) {
        if (timeout == this.attachmentTimeoutSeconds) {
            return;
        }
        this.attachmentTimeoutSeconds = timeout;
        this.refreshSeconds = this.attachmentTimeoutSeconds / 2;
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        if (this.refreshSeconds > 0) {
            (this.timer = new Timer(true)).schedule(new CleanupFilesTask(), this.refreshSeconds * 1000, this.refreshSeconds * 1000);
        }
        if (AttachmentCacheMonitor.log.isDebugEnabled()) {
            AttachmentCacheMonitor.log.debug((Object)("New timeout = " + this.attachmentTimeoutSeconds));
            AttachmentCacheMonitor.log.debug((Object)("New refresh = " + this.refreshSeconds));
        }
    }
    
    public void register(final String fileName) {
        if (this.attachmentTimeoutSeconds > 0) {
            this._register(fileName);
            this._checkForAgedFiles();
        }
    }
    
    public void access(final String fileName) {
        if (this.attachmentTimeoutSeconds > 0) {
            this._access(fileName);
            this._checkForAgedFiles();
        }
    }
    
    public void checkForAgedFiles() {
        if (this.attachmentTimeoutSeconds > 0) {
            this._checkForAgedFiles();
        }
    }
    
    private synchronized void _register(final String fileName) {
        final Long currentTime = this.getTime();
        if (AttachmentCacheMonitor.log.isDebugEnabled()) {
            AttachmentCacheMonitor.log.debug((Object)("Register file " + fileName));
            AttachmentCacheMonitor.log.debug((Object)("Time = " + currentTime));
        }
        this.files.put(fileName, currentTime);
    }
    
    private synchronized void _access(final String fileName) {
        final Long currentTime = this.getTime();
        final Long priorTime = this.files.get(fileName);
        if (priorTime != null) {
            this.files.put(fileName, currentTime);
            if (AttachmentCacheMonitor.log.isDebugEnabled()) {
                AttachmentCacheMonitor.log.debug((Object)("Access file " + fileName));
                AttachmentCacheMonitor.log.debug((Object)("Old Time = " + priorTime));
                AttachmentCacheMonitor.log.debug((Object)("New Time = " + currentTime));
            }
        }
        else if (AttachmentCacheMonitor.log.isDebugEnabled()) {
            AttachmentCacheMonitor.log.debug((Object)("The following file was already deleted and is no longer available: " + fileName));
            AttachmentCacheMonitor.log.debug((Object)("The value of org.apache.axiom.attachments.tempfile.expiration is " + this.attachmentTimeoutSeconds));
        }
    }
    
    private synchronized void _checkForAgedFiles() {
        final Long currentTime = this.getTime();
        if (this.isExpired(this.priorDeleteMillis, currentTime, this.refreshSeconds)) {
            final Iterator it = this.files.keySet().iterator();
            while (it.hasNext()) {
                final String fileName = it.next();
                final Long lastAccess = this.files.get(fileName);
                if (this.isExpired(lastAccess, currentTime, this.attachmentTimeoutSeconds)) {
                    if (AttachmentCacheMonitor.log.isDebugEnabled()) {
                        AttachmentCacheMonitor.log.debug((Object)("Expired file " + fileName));
                        AttachmentCacheMonitor.log.debug((Object)("Old Time = " + lastAccess));
                        AttachmentCacheMonitor.log.debug((Object)("New Time = " + currentTime));
                        AttachmentCacheMonitor.log.debug((Object)("Elapsed Time (ms) = " + (currentTime - lastAccess)));
                    }
                    this.deleteFile(fileName);
                    it.remove();
                }
            }
            this.priorDeleteMillis = currentTime;
        }
    }
    
    private boolean deleteFile(final String fileName) {
        final Boolean privRet = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return AttachmentCacheMonitor.this._deleteFile(fileName);
            }
        });
        return privRet;
    }
    
    private Boolean _deleteFile(final String fileName) {
        boolean ret = false;
        final File file = new File(fileName);
        if (file.exists()) {
            ret = file.delete();
            if (AttachmentCacheMonitor.log.isDebugEnabled()) {
                AttachmentCacheMonitor.log.debug((Object)("Deletion Successful ? " + ret));
            }
        }
        else if (AttachmentCacheMonitor.log.isDebugEnabled()) {
            AttachmentCacheMonitor.log.debug((Object)("This file no longer exists = " + fileName));
        }
        return new Boolean(ret);
    }
    
    private Long getTime() {
        return new Long(System.currentTimeMillis());
    }
    
    private boolean isExpired(final Long oldTimeMillis, final Long newTimeMillis, final int thresholdSecs) {
        final long elapse = newTimeMillis - oldTimeMillis;
        return elapse > thresholdSecs * 1000;
    }
    
    static {
        AttachmentCacheMonitor.log = LogFactory.getLog(AttachmentCacheMonitor.class.getName());
        AttachmentCacheMonitor._singleton = null;
    }
    
    private class CleanupFilesTask extends TimerTask
    {
        @Override
        public void run() {
            AttachmentCacheMonitor.this.checkForAgedFiles();
        }
    }
}
