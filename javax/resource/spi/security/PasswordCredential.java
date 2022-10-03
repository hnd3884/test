package javax.resource.spi.security;

import java.util.Arrays;
import javax.resource.spi.ManagedConnectionFactory;
import java.io.Serializable;

public class PasswordCredential implements Serializable
{
    private String userName;
    private char[] password;
    private ManagedConnectionFactory mcf;
    
    public PasswordCredential(final String userName, final char[] password) {
        this.mcf = null;
        this.userName = userName;
        this.password = password;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public char[] getPassword() {
        return this.password;
    }
    
    public ManagedConnectionFactory getManagedConnectionFactory() {
        return this.mcf;
    }
    
    public void setManagedConnectionFactory(final ManagedConnectionFactory mcf) {
        this.mcf = mcf;
    }
    
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        final PasswordCredential otherCredential = (PasswordCredential)other;
        return this.userName.equals(otherCredential.userName) && Arrays.equals(this.password, otherCredential.password);
    }
    
    public int hashCode() {
        return this.userName.hashCode();
    }
}
