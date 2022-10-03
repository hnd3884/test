package com.adventnet.tools.update.installer.log;

import java.util.Properties;

public interface UpdateManagerLogInterface
{
    void init(final Properties p0);
    
    void log(final String p0);
    
    void log(final String p0, final int p1);
    
    void fail(final String p0);
    
    void fail(final String p0, final Throwable p1);
    
    String getString(final String p0);
    
    void close();
}
