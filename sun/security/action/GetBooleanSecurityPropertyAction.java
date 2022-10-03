package sun.security.action;

import java.security.Security;
import java.security.PrivilegedAction;

public class GetBooleanSecurityPropertyAction implements PrivilegedAction<Boolean>
{
    private String theProp;
    
    public GetBooleanSecurityPropertyAction(final String theProp) {
        this.theProp = theProp;
    }
    
    @Override
    public Boolean run() {
        boolean b = false;
        try {
            final String property = Security.getProperty(this.theProp);
            b = (property != null && property.equalsIgnoreCase("true"));
        }
        catch (final NullPointerException ex) {}
        return b;
    }
}
