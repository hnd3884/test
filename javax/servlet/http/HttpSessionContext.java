package javax.servlet.http;

import java.util.Enumeration;

public interface HttpSessionContext
{
    @Deprecated
    HttpSession getSession(final String p0);
    
    @Deprecated
    Enumeration<String> getIds();
}
