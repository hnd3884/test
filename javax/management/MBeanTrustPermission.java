package javax.management;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.BasicPermission;

public class MBeanTrustPermission extends BasicPermission
{
    private static final long serialVersionUID = -2952178077029018140L;
    
    public MBeanTrustPermission(final String s) {
        this(s, null);
    }
    
    public MBeanTrustPermission(final String s, final String s2) {
        super(s, s2);
        validate(s, s2);
    }
    
    private static void validate(final String s, final String s2) {
        if (s2 != null && s2.length() > 0) {
            throw new IllegalArgumentException("MBeanTrustPermission actions must be null: " + s2);
        }
        if (!s.equals("register") && !s.equals("*")) {
            throw new IllegalArgumentException("MBeanTrustPermission: Unknown target name [" + s + "]");
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            validate(super.getName(), super.getActions());
        }
        catch (final IllegalArgumentException ex) {
            throw new InvalidObjectException(ex.getMessage());
        }
    }
}
