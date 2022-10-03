package org.apache.catalina.ssi;

import java.io.IOException;
import java.util.Date;
import java.util.Collection;

public interface SSIExternalResolver
{
    void addVariableNames(final Collection<String> p0);
    
    String getVariableValue(final String p0);
    
    void setVariableValue(final String p0, final String p1);
    
    Date getCurrentDate();
    
    long getFileSize(final String p0, final boolean p1) throws IOException;
    
    long getFileLastModified(final String p0, final boolean p1) throws IOException;
    
    String getFileText(final String p0, final boolean p1) throws IOException;
    
    void log(final String p0, final Throwable p1);
}
