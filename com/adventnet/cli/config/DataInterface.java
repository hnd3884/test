package com.adventnet.cli.config;

import java.util.Properties;

public interface DataInterface
{
    TaskData[] getConfigData(final ConfigObject p0) throws DataException;
    
    Properties getConfigCmdData(final String p0, final String p1) throws DataException;
    
    String[] getConfigScriptData(final String p0, final String p1) throws DataException;
}
