package sun.security.action;

import java.security.PrivilegedAction;

public class GetLongAction implements PrivilegedAction<Long>
{
    private String theProp;
    private long defaultVal;
    private boolean defaultSet;
    
    public GetLongAction(final String theProp) {
        this.defaultSet = false;
        this.theProp = theProp;
    }
    
    public GetLongAction(final String theProp, final long defaultVal) {
        this.defaultSet = false;
        this.theProp = theProp;
        this.defaultVal = defaultVal;
        this.defaultSet = true;
    }
    
    @Override
    public Long run() {
        final Long long1 = Long.getLong(this.theProp);
        if (long1 == null && this.defaultSet) {
            return new Long(this.defaultVal);
        }
        return long1;
    }
}
