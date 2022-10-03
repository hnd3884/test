package javax.security.auth.message;

import java.util.Map;

public interface MessageInfo
{
    Object getRequestMessage();
    
    Object getResponseMessage();
    
    void setRequestMessage(final Object p0);
    
    void setResponseMessage(final Object p0);
    
    Map getMap();
}
