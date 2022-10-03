package sun.security.action;

import java.security.PrivilegedAction;

public class GetIntegerAction implements PrivilegedAction<Integer>
{
    private String theProp;
    private int defaultVal;
    private boolean defaultSet;
    
    public GetIntegerAction(final String theProp) {
        this.defaultSet = false;
        this.theProp = theProp;
    }
    
    public GetIntegerAction(final String theProp, final int defaultVal) {
        this.defaultSet = false;
        this.theProp = theProp;
        this.defaultVal = defaultVal;
        this.defaultSet = true;
    }
    
    @Override
    public Integer run() {
        final Integer integer = Integer.getInteger(this.theProp);
        if (integer == null && this.defaultSet) {
            return new Integer(this.defaultVal);
        }
        return integer;
    }
}
