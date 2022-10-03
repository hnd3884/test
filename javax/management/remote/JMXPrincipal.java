package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Principal;

public class JMXPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -4184480100214577411L;
    private String name;
    
    public JMXPrincipal(final String name) {
        validate(name);
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return "JMXPrincipal:  " + this.name;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof JMXPrincipal && this.getName().equals(((JMXPrincipal)o).getName())));
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final String name = (String)objectInputStream.readFields().get("name", null);
        try {
            validate(name);
            this.name = name;
        }
        catch (final NullPointerException ex) {
            throw new InvalidObjectException(ex.getMessage());
        }
    }
    
    private static void validate(final String s) throws NullPointerException {
        if (s == null) {
            throw new NullPointerException("illegal null input");
        }
    }
}
