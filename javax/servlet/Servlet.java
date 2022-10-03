package javax.servlet;

import java.io.IOException;

public interface Servlet
{
    void init(final ServletConfig p0) throws ServletException;
    
    ServletConfig getServletConfig();
    
    void service(final ServletRequest p0, final ServletResponse p1) throws ServletException, IOException;
    
    String getServletInfo();
    
    void destroy();
}
