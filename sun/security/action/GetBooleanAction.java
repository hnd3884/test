package sun.security.action;

import java.security.PrivilegedAction;

public class GetBooleanAction implements PrivilegedAction<Boolean>
{
    private String theProp;
    
    public GetBooleanAction(final String theProp) {
        this.theProp = theProp;
    }
    
    @Override
    public Boolean run() {
        return Boolean.getBoolean(this.theProp);
    }
}
