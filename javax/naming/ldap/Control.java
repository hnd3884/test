package javax.naming.ldap;

import java.io.Serializable;

public interface Control extends Serializable
{
    public static final boolean CRITICAL = true;
    public static final boolean NONCRITICAL = false;
    
    String getID();
    
    boolean isCritical();
    
    byte[] getEncodedValue();
}
