package javax.servlet;

import java.util.Set;
import java.util.Map;

public interface Registration
{
    String getName();
    
    String getClassName();
    
    boolean setInitParameter(final String p0, final String p1);
    
    String getInitParameter(final String p0);
    
    Set<String> setInitParameters(final Map<String, String> p0);
    
    Map<String, String> getInitParameters();
    
    public interface Dynamic extends Registration
    {
        void setAsyncSupported(final boolean p0);
    }
}
