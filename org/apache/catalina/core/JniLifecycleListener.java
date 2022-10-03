package org.apache.catalina.core;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.LifecycleEvent;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.LifecycleListener;

public class JniLifecycleListener implements LifecycleListener
{
    private static final Log log;
    protected static final StringManager sm;
    private String libraryName;
    private String libraryPath;
    
    public JniLifecycleListener() {
        this.libraryName = "";
        this.libraryPath = "";
    }
    
    @Override
    public void lifecycleEvent(final LifecycleEvent event) {
        if ("before_start".equals(event.getType())) {
            if (!this.libraryName.isEmpty()) {
                System.loadLibrary(this.libraryName);
                JniLifecycleListener.log.info((Object)JniLifecycleListener.sm.getString("jniLifecycleListener.load.name", new Object[] { this.libraryName }));
            }
            else {
                if (this.libraryPath.isEmpty()) {
                    throw new IllegalArgumentException(JniLifecycleListener.sm.getString("jniLifecycleListener.missingPathOrName"));
                }
                System.load(this.libraryPath);
                JniLifecycleListener.log.info((Object)JniLifecycleListener.sm.getString("jniLifecycleListener.load.path", new Object[] { this.libraryPath }));
            }
        }
    }
    
    public void setLibraryName(final String libraryName) {
        if (!this.libraryPath.isEmpty()) {
            throw new IllegalArgumentException(JniLifecycleListener.sm.getString("jniLifecycleListener.bothPathAndName"));
        }
        this.libraryName = libraryName;
    }
    
    public String getLibraryName() {
        return this.libraryName;
    }
    
    public void setLibraryPath(final String libraryPath) {
        if (!this.libraryName.isEmpty()) {
            throw new IllegalArgumentException(JniLifecycleListener.sm.getString("jniLifecycleListener.bothPathAndName"));
        }
        this.libraryPath = libraryPath;
    }
    
    public String getLibraryPath() {
        return this.libraryPath;
    }
    
    static {
        log = LogFactory.getLog((Class)JniLifecycleListener.class);
        sm = StringManager.getManager((Class)JniLifecycleListener.class);
    }
}
