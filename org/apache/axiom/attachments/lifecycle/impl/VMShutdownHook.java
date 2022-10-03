package org.apache.axiom.attachments.lifecycle.impl;

import java.util.Collections;
import java.util.HashSet;
import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.io.File;
import java.util.Set;
import org.apache.commons.logging.Log;

public class VMShutdownHook extends Thread
{
    private static final Log log;
    private static VMShutdownHook instance;
    private static Set files;
    private boolean isRegistered;
    
    static VMShutdownHook hook() {
        if (VMShutdownHook.instance == null) {
            if (VMShutdownHook.log.isDebugEnabled()) {
                VMShutdownHook.log.debug((Object)"creating VMShutdownHook");
            }
            VMShutdownHook.instance = new VMShutdownHook();
        }
        if (VMShutdownHook.log.isDebugEnabled()) {
            VMShutdownHook.log.debug((Object)"returning VMShutdownHook instance");
        }
        return VMShutdownHook.instance;
    }
    
    private VMShutdownHook() {
        this.isRegistered = false;
    }
    
    void remove(final File file) {
        if (file == null) {
            return;
        }
        if (VMShutdownHook.log.isDebugEnabled()) {
            VMShutdownHook.log.debug((Object)"Removing File to Shutdown Hook Collection");
        }
        VMShutdownHook.files.remove(file);
    }
    
    void add(final File file) {
        if (file == null) {
            return;
        }
        if (VMShutdownHook.log.isDebugEnabled()) {
            VMShutdownHook.log.debug((Object)"Adding File to Shutdown Hook Collection");
        }
        VMShutdownHook.files.add(file);
    }
    
    @Override
    public void run() {
        if (VMShutdownHook.log.isDebugEnabled()) {
            VMShutdownHook.log.debug((Object)"JVM running VM Shutdown Hook");
        }
        for (final File file : VMShutdownHook.files) {
            if (VMShutdownHook.log.isDebugEnabled()) {
                VMShutdownHook.log.debug((Object)("Deleting File from Shutdown Hook Collection" + file.getAbsolutePath()));
            }
            file.delete();
        }
        if (VMShutdownHook.log.isDebugEnabled()) {
            VMShutdownHook.log.debug((Object)"JVM Done running VM Shutdown Hook");
        }
    }
    
    public boolean isRegistered() {
        if (VMShutdownHook.log.isDebugEnabled()) {
            if (!this.isRegistered) {
                VMShutdownHook.log.debug((Object)"hook isRegistered= false");
            }
            else {
                VMShutdownHook.log.debug((Object)"hook isRegistered= true");
            }
        }
        return this.isRegistered;
    }
    
    public void setRegistered(final boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
    
    static {
        log = LogFactory.getLog((Class)VMShutdownHook.class);
        VMShutdownHook.instance = null;
        VMShutdownHook.files = Collections.synchronizedSet(new HashSet<Object>());
    }
}
