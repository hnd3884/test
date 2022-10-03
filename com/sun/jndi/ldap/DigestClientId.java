package com.sun.jndi.ldap;

import java.util.Arrays;
import java.util.Hashtable;
import java.io.OutputStream;
import javax.naming.ldap.Control;

class DigestClientId extends SimpleClientId
{
    private static final String[] SASL_PROPS;
    private final String[] propvals;
    private final int myHash;
    
    DigestClientId(final int n, final String s, final int n2, final String s2, final Control[] array, final OutputStream outputStream, final String s3, final String s4, final Object o, final Hashtable<?, ?> hashtable) {
        super(n, s, n2, s2, array, outputStream, s3, s4, o);
        if (hashtable == null) {
            this.propvals = null;
        }
        else {
            this.propvals = new String[DigestClientId.SASL_PROPS.length];
            for (int i = 0; i < DigestClientId.SASL_PROPS.length; ++i) {
                this.propvals[i] = (String)hashtable.get(DigestClientId.SASL_PROPS[i]);
            }
        }
        this.myHash = (super.hashCode() ^ Arrays.hashCode(this.propvals));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DigestClientId)) {
            return false;
        }
        final DigestClientId digestClientId = (DigestClientId)o;
        return this.myHash == digestClientId.myHash && super.equals(o) && Arrays.equals(this.propvals, digestClientId.propvals);
    }
    
    @Override
    public int hashCode() {
        return this.myHash;
    }
    
    @Override
    public String toString() {
        if (this.propvals != null) {
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < this.propvals.length; ++i) {
                sb.append(':');
                if (this.propvals[i] != null) {
                    sb.append(this.propvals[i]);
                }
            }
            return super.toString() + sb.toString();
        }
        return super.toString();
    }
    
    static {
        SASL_PROPS = new String[] { "java.naming.security.sasl.authorizationId", "java.naming.security.sasl.realm", "javax.security.sasl.qop", "javax.security.sasl.strength", "javax.security.sasl.reuse", "javax.security.sasl.server.authentication", "javax.security.sasl.maxbuffer", "javax.security.sasl.policy.noplaintext", "javax.security.sasl.policy.noactive", "javax.security.sasl.policy.nodictionary", "javax.security.sasl.policy.noanonymous", "javax.security.sasl.policy.forward", "javax.security.sasl.policy.credentials" };
    }
}
