package org.apache.axiom.attachments.lifecycle.impl;

import org.apache.commons.logging.LogFactory;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.io.IOException;
import org.apache.axiom.util.UIDGenerator;
import java.io.File;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;

public class LifecycleManagerImpl implements LifecycleManager
{
    private static final Log log;
    private static Hashtable table;
    private VMShutdownHook hook;
    
    public LifecycleManagerImpl() {
        this.hook = null;
    }
    
    public FileAccessor create(final String attachmentDir) throws IOException {
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"Start Create()");
        }
        File file = null;
        File dir = null;
        if (attachmentDir != null) {
            dir = new File(attachmentDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Given Attachment File Cache Location " + dir + " should be a directory.");
        }
        final String id = UIDGenerator.generateUID();
        final String fileString = "axiom" + id + ".att";
        file = new File(dir, fileString);
        final FileAccessor fa = new FileAccessor(this, file);
        LifecycleManagerImpl.table.put(fileString, fa);
        this.deleteOnExit(file);
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"End Create()");
        }
        return fa;
    }
    
    public void delete(final File file) throws IOException {
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"Start delete()");
        }
        if (file != null && file.exists()) {
            LifecycleManagerImpl.table.remove(file);
            if (LifecycleManagerImpl.log.isDebugEnabled()) {
                LifecycleManagerImpl.log.debug((Object)"invoking file.delete()");
            }
            if (file.delete()) {
                if (LifecycleManagerImpl.log.isDebugEnabled()) {
                    LifecycleManagerImpl.log.debug((Object)"delete() successful");
                }
                final VMShutdownHook hook = VMShutdownHook.hook();
                if (hook.isRegistered()) {
                    hook.remove(file);
                }
                if (LifecycleManagerImpl.log.isDebugEnabled()) {
                    LifecycleManagerImpl.log.debug((Object)"File Purged and removed from Shutdown Hook Collection");
                }
            }
            else {
                if (LifecycleManagerImpl.log.isDebugEnabled()) {
                    LifecycleManagerImpl.log.debug((Object)"Cannot delete file, set to delete on VM shutdown");
                }
                this.deleteOnExit(file);
            }
        }
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"End delete()");
        }
    }
    
    public void deleteOnExit(final File file) throws IOException {
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"Start deleteOnExit()");
        }
        if (this.hook == null) {
            this.hook = this.RegisterVMShutdownHook();
        }
        if (file != null) {
            if (LifecycleManagerImpl.log.isDebugEnabled()) {
                LifecycleManagerImpl.log.debug((Object)("Invoking deleteOnExit() for file = " + file.getAbsolutePath()));
            }
            this.hook.add(file);
            LifecycleManagerImpl.table.remove(file);
        }
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"End deleteOnExit()");
        }
    }
    
    public void deleteOnTimeInterval(final int interval, final File file) throws IOException {
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"Start deleteOnTimeInterval()");
        }
        final Thread t = new Thread(new FileDeletor(interval, file));
        t.setDaemon(true);
        t.start();
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"End deleteOnTimeInterval()");
        }
    }
    
    private VMShutdownHook RegisterVMShutdownHook() throws RuntimeException {
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"Start RegisterVMShutdownHook()");
        }
        try {
            this.hook = AccessController.doPrivileged((PrivilegedExceptionAction<VMShutdownHook>)new PrivilegedExceptionAction() {
                public Object run() throws SecurityException, IllegalStateException, IllegalArgumentException {
                    final VMShutdownHook hook = VMShutdownHook.hook();
                    if (!hook.isRegistered()) {
                        Runtime.getRuntime().addShutdownHook(hook);
                        hook.setRegistered(true);
                    }
                    return hook;
                }
            });
        }
        catch (final PrivilegedActionException e) {
            if (LifecycleManagerImpl.log.isDebugEnabled()) {
                LifecycleManagerImpl.log.debug((Object)("Exception thrown from AccessController: " + e));
                LifecycleManagerImpl.log.debug((Object)"VM Shutdown Hook not registered.");
            }
            throw new RuntimeException(e);
        }
        if (LifecycleManagerImpl.log.isDebugEnabled()) {
            LifecycleManagerImpl.log.debug((Object)"Exit RegisterVMShutdownHook()");
        }
        return this.hook;
    }
    
    public FileAccessor getFileAccessor(final String fileName) throws IOException {
        return LifecycleManagerImpl.table.get(fileName);
    }
    
    static {
        log = LogFactory.getLog((Class)LifecycleManagerImpl.class);
        LifecycleManagerImpl.table = new Hashtable();
    }
    
    public class FileDeletor implements Runnable
    {
        int interval;
        File _file;
        
        public FileDeletor(final int interval, final File file) {
            this.interval = interval;
            this._file = file;
        }
        
        public void run() {
            try {
                Thread.sleep(this.interval * 1000);
                if (this._file.exists()) {
                    LifecycleManagerImpl.table.remove(this._file);
                    this._file.delete();
                }
            }
            catch (final InterruptedException e) {
                if (LifecycleManagerImpl.log.isDebugEnabled()) {
                    LifecycleManagerImpl.log.warn((Object)("InterruptedException occured " + e.getMessage()));
                }
            }
        }
    }
}
