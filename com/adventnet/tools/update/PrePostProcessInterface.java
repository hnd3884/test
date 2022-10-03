package com.adventnet.tools.update;

import java.util.Properties;

public interface PrePostProcessInterface
{
    int install(final Properties p0);
    
    int revert(final Properties p0);
    
    String getErrorMsg();
    
    Object[] getFilesToModify();
    
    boolean isFilesToBeBackedUp();
}
