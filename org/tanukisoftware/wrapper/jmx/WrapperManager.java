package org.tanukisoftware.wrapper.jmx;

public class WrapperManager implements WrapperManagerMBean
{
    public String getVersion() {
        return org.tanukisoftware.wrapper.WrapperManager.getVersion();
    }
    
    public String getBuildTime() {
        return org.tanukisoftware.wrapper.WrapperManager.getBuildTime();
    }
    
    public int getJVMId() {
        return org.tanukisoftware.wrapper.WrapperManager.getJVMId();
    }
    
    public boolean isProfessionalEdition() {
        return org.tanukisoftware.wrapper.WrapperManager.isProfessionalEdition();
    }
    
    public boolean isStandardEdition() {
        return org.tanukisoftware.wrapper.WrapperManager.isStandardEdition();
    }
    
    public void setConsoleTitle(final String title) {
        org.tanukisoftware.wrapper.WrapperManager.setConsoleTitle(title);
    }
    
    public int getWrapperPID() {
        return org.tanukisoftware.wrapper.WrapperManager.getWrapperPID();
    }
    
    public int getJavaPID() {
        return org.tanukisoftware.wrapper.WrapperManager.getJavaPID();
    }
    
    public void requestThreadDump() {
        org.tanukisoftware.wrapper.WrapperManager.requestThreadDump();
    }
    
    public boolean isControlledByNativeWrapper() {
        return org.tanukisoftware.wrapper.WrapperManager.isControlledByNativeWrapper();
    }
    
    public boolean isLaunchedAsService() {
        return org.tanukisoftware.wrapper.WrapperManager.isLaunchedAsService();
    }
    
    public boolean isDebugEnabled() {
        return org.tanukisoftware.wrapper.WrapperManager.isDebugEnabled();
    }
    
    public void restart() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000L);
                }
                catch (final InterruptedException ex) {}
                org.tanukisoftware.wrapper.WrapperManager.restart();
            }
        }.start();
    }
    
    public void stop(final int exitCode) {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000L);
                }
                catch (final InterruptedException ex) {}
                org.tanukisoftware.wrapper.WrapperManager.stop(exitCode);
            }
        }.start();
    }
    
    public boolean getHasShutdownHookBeenTriggered() {
        return org.tanukisoftware.wrapper.WrapperManager.hasShutdownHookBeenTriggered();
    }
}
