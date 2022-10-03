package javax.servlet;

import java.io.IOException;

public interface Filter
{
    void init(final FilterConfig p0) throws ServletException;
    
    void doFilter(final ServletRequest p0, final ServletResponse p1, final FilterChain p2) throws IOException, ServletException;
    
    void destroy();
}
