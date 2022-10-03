package org.tanukisoftware.wrapper.jmx;

public interface WrapperManagerMBean
{
    String getVersion();
    
    String getBuildTime();
    
    int getJVMId();
    
    boolean isProfessionalEdition();
    
    boolean isStandardEdition();
    
    void setConsoleTitle(final String p0);
    
    int getWrapperPID();
    
    int getJavaPID();
    
    void requestThreadDump();
    
    boolean isControlledByNativeWrapper();
    
    boolean isLaunchedAsService();
    
    boolean isDebugEnabled();
    
    void restart();
    
    void stop(final int p0);
    
    boolean getHasShutdownHookBeenTriggered();
}
