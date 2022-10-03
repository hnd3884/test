package javax.security.auth.spi;

import javax.security.auth.login.LoginException;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;

public interface LoginModule
{
    void initialize(final Subject p0, final CallbackHandler p1, final Map<String, ?> p2, final Map<String, ?> p3);
    
    boolean login() throws LoginException;
    
    boolean commit() throws LoginException;
    
    boolean abort() throws LoginException;
    
    boolean logout() throws LoginException;
}
