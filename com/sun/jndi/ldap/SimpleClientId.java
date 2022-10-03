package com.sun.jndi.ldap;

import java.util.Arrays;
import java.io.OutputStream;
import javax.naming.ldap.Control;

class SimpleClientId extends ClientId
{
    private final String username;
    private final Object passwd;
    private final int myHash;
    
    SimpleClientId(final int n, final String s, final int n2, final String s2, final Control[] array, final OutputStream outputStream, final String s3, final String username, final Object passwd) {
        super(n, s, n2, s2, array, outputStream, s3);
        this.username = username;
        int n3 = 0;
        if (passwd == null) {
            this.passwd = null;
        }
        else if (passwd instanceof byte[]) {
            this.passwd = ((byte[])passwd).clone();
            n3 = Arrays.hashCode((byte[])passwd);
        }
        else if (passwd instanceof char[]) {
            this.passwd = ((char[])passwd).clone();
            n3 = Arrays.hashCode((char[])passwd);
        }
        else {
            this.passwd = passwd;
            n3 = passwd.hashCode();
        }
        this.myHash = (super.hashCode() ^ ((username != null) ? username.hashCode() : 0) ^ n3);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof SimpleClientId)) {
            return false;
        }
        final SimpleClientId simpleClientId = (SimpleClientId)o;
        return super.equals(o) && (this.username == simpleClientId.username || (this.username != null && this.username.equals(simpleClientId.username))) && (this.passwd == simpleClientId.passwd || (this.passwd != null && simpleClientId.passwd != null && ((this.passwd instanceof String && this.passwd.equals(simpleClientId.passwd)) || (this.passwd instanceof byte[] && simpleClientId.passwd instanceof byte[] && Arrays.equals((byte[])this.passwd, (byte[])simpleClientId.passwd)) || (this.passwd instanceof char[] && simpleClientId.passwd instanceof char[] && Arrays.equals((char[])this.passwd, (char[])simpleClientId.passwd)))));
    }
    
    @Override
    public int hashCode() {
        return this.myHash;
    }
    
    @Override
    public String toString() {
        return super.toString() + ":" + this.username;
    }
}
