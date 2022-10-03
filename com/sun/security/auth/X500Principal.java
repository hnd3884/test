package com.sun.security.auth;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.NotActiveException;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.security.x509.X500Name;
import java.util.ResourceBundle;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported(false)
@Deprecated
public class X500Principal implements Principal, Serializable
{
    private static final long serialVersionUID = -8222422609431628648L;
    private static final ResourceBundle rb;
    private String name;
    private transient X500Name thisX500Name;
    
    public X500Principal(final String name) {
        if (name == null) {
            throw new NullPointerException(X500Principal.rb.getString("provided.null.name"));
        }
        try {
            this.thisX500Name = new X500Name(name);
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException(ex.toString());
        }
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.thisX500Name.getName();
    }
    
    @Override
    public String toString() {
        return this.thisX500Name.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof X500Principal) {
            final X500Principal x500Principal = (X500Principal)o;
            try {
                return this.thisX500Name.equals(new X500Name(x500Principal.getName()));
            }
            catch (final Exception ex) {
                return false;
            }
        }
        return o instanceof Principal && o.equals(this.thisX500Name);
    }
    
    @Override
    public int hashCode() {
        return this.thisX500Name.hashCode();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, NotActiveException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.thisX500Name = new X500Name(this.name);
    }
    
    static {
        rb = AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
            @Override
            public ResourceBundle run() {
                return ResourceBundle.getBundle("sun.security.util.AuthResources");
            }
        });
    }
}
