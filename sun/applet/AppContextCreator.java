package sun.applet;

import sun.awt.SunToolkit;
import sun.awt.AppContext;

class AppContextCreator extends Thread
{
    Object syncObject;
    AppContext appContext;
    volatile boolean created;
    
    AppContextCreator(final ThreadGroup threadGroup) {
        super(threadGroup, "AppContextCreator");
        this.syncObject = new Object();
        this.appContext = null;
        this.created = false;
    }
    
    @Override
    public void run() {
        this.appContext = SunToolkit.createNewAppContext();
        this.created = true;
        synchronized (this.syncObject) {
            this.syncObject.notifyAll();
        }
    }
}
