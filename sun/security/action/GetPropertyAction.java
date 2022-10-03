package sun.security.action;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class GetPropertyAction implements PrivilegedAction<String>
{
    private String theProp;
    private String defaultVal;
    
    public GetPropertyAction(final String theProp) {
        this.theProp = theProp;
    }
    
    public GetPropertyAction(final String theProp, final String defaultVal) {
        this.theProp = theProp;
        this.defaultVal = defaultVal;
    }
    
    @Override
    public String run() {
        final String property = System.getProperty(this.theProp);
        return (property == null) ? this.defaultVal : property;
    }
    
    public static String privilegedGetProperty(final String s) {
        if (System.getSecurityManager() == null) {
            return System.getProperty(s);
        }
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s));
    }
    
    public static String privilegedGetProperty(final String s, final String s2) {
        if (System.getSecurityManager() == null) {
            return System.getProperty(s, s2);
        }
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s, s2));
    }
}
