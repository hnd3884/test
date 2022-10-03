package org.apache.catalina;

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import java.io.File;

public interface Host extends Container
{
    public static final String ADD_ALIAS_EVENT = "addAlias";
    public static final String REMOVE_ALIAS_EVENT = "removeAlias";
    
    String getXmlBase();
    
    void setXmlBase(final String p0);
    
    File getConfigBaseFile();
    
    String getAppBase();
    
    File getAppBaseFile();
    
    void setAppBase(final String p0);
    
    boolean getAutoDeploy();
    
    void setAutoDeploy(final boolean p0);
    
    String getConfigClass();
    
    void setConfigClass(final String p0);
    
    boolean getDeployOnStartup();
    
    void setDeployOnStartup(final boolean p0);
    
    String getDeployIgnore();
    
    Pattern getDeployIgnorePattern();
    
    void setDeployIgnore(final String p0);
    
    ExecutorService getStartStopExecutor();
    
    boolean getCreateDirs();
    
    void setCreateDirs(final boolean p0);
    
    boolean getUndeployOldVersions();
    
    void setUndeployOldVersions(final boolean p0);
    
    void addAlias(final String p0);
    
    String[] findAliases();
    
    void removeAlias(final String p0);
}
