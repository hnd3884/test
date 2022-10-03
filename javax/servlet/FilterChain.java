package javax.servlet;

import java.io.IOException;

public interface FilterChain
{
    void doFilter(final ServletRequest p0, final ServletResponse p1) throws IOException, ServletException;
}
