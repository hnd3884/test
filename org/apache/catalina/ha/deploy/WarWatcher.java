package org.apache.catalina.ha.deploy;

import org.apache.juli.logging.LogFactory;
import java.util.Iterator;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class WarWatcher
{
    private static final Log log;
    private static final StringManager sm;
    protected final File watchDir;
    protected final FileChangeListener listener;
    protected final Map<String, WarInfo> currentStatus;
    
    public WarWatcher(final FileChangeListener listener, final File watchDir) {
        this.currentStatus = new HashMap<String, WarInfo>();
        this.listener = listener;
        this.watchDir = watchDir;
    }
    
    public void check() {
        if (WarWatcher.log.isDebugEnabled()) {
            WarWatcher.log.debug((Object)WarWatcher.sm.getString("warWatcher.checkingWars", new Object[] { this.watchDir }));
        }
        File[] list = this.watchDir.listFiles(new WarFilter());
        if (list == null) {
            WarWatcher.log.warn((Object)WarWatcher.sm.getString("warWatcher.cantListWatchDir", new Object[] { this.watchDir }));
            list = new File[0];
        }
        for (final File file : list) {
            if (!file.exists()) {
                WarWatcher.log.warn((Object)WarWatcher.sm.getString("warWatcher.listedFileDoesNotExist", new Object[] { file, this.watchDir }));
            }
            this.addWarInfo(file);
        }
        final Iterator<Map.Entry<String, WarInfo>> i = this.currentStatus.entrySet().iterator();
        while (i.hasNext()) {
            final Map.Entry<String, WarInfo> entry = i.next();
            final WarInfo info = entry.getValue();
            if (WarWatcher.log.isTraceEnabled()) {
                WarWatcher.log.trace((Object)WarWatcher.sm.getString("warWatcher.checkingWar", new Object[] { info.getWar() }));
            }
            final int check = info.check();
            if (check == 1) {
                this.listener.fileModified(info.getWar());
            }
            else if (check == -1) {
                this.listener.fileRemoved(info.getWar());
                i.remove();
            }
            if (WarWatcher.log.isTraceEnabled()) {
                WarWatcher.log.trace((Object)WarWatcher.sm.getString("warWatcher.checkWarResult", new Object[] { check, info.getWar() }));
            }
        }
    }
    
    protected void addWarInfo(final File warfile) {
        WarInfo info = this.currentStatus.get(warfile.getAbsolutePath());
        if (info == null) {
            info = new WarInfo(warfile);
            info.setLastState(-1);
            this.currentStatus.put(warfile.getAbsolutePath(), info);
        }
    }
    
    public void clear() {
        this.currentStatus.clear();
    }
    
    static {
        log = LogFactory.getLog((Class)WarWatcher.class);
        sm = StringManager.getManager((Class)WarWatcher.class);
    }
    
    protected static class WarFilter implements FilenameFilter
    {
        @Override
        public boolean accept(final File path, final String name) {
            return name != null && name.endsWith(".war");
        }
    }
    
    protected static class WarInfo
    {
        protected final File war;
        protected long lastChecked;
        protected long lastState;
        
        public WarInfo(final File war) {
            this.lastChecked = 0L;
            this.lastState = 0L;
            this.war = war;
            this.lastChecked = war.lastModified();
            if (!war.exists()) {
                this.lastState = -1L;
            }
        }
        
        public boolean modified() {
            return this.war.exists() && this.war.lastModified() > this.lastChecked;
        }
        
        public boolean exists() {
            return this.war.exists();
        }
        
        public int check() {
            int result = 0;
            if (this.modified()) {
                result = 1;
                this.lastState = result;
            }
            else if (!this.exists() && this.lastState != -1L) {
                result = -1;
                this.lastState = result;
            }
            else if (this.lastState == -1L && this.exists()) {
                result = 1;
                this.lastState = result;
            }
            this.lastChecked = System.currentTimeMillis();
            return result;
        }
        
        public File getWar() {
            return this.war;
        }
        
        @Override
        public int hashCode() {
            return this.war.getAbsolutePath().hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            if (other instanceof WarInfo) {
                final WarInfo wo = (WarInfo)other;
                return wo.getWar().equals(this.getWar());
            }
            return false;
        }
        
        protected void setLastState(final int lastState) {
            this.lastState = lastState;
        }
    }
}
