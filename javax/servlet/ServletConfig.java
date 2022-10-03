package javax.servlet;

import java.util.Enumeration;

public interface ServletConfig
{
    String getServletName();
    
    ServletContext getServletContext();
    
    String getInitParameter(final String p0);
    
    Enumeration<String> getInitParameterNames();
}
