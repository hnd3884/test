package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.ldap.Control;
import javax.naming.NamingException;
import javax.naming.ldap.UnsolicitedNotification;

final class UnsolicitedResponseImpl implements UnsolicitedNotification
{
    private String oid;
    private String[] referrals;
    private byte[] extensionValue;
    private NamingException exception;
    private Control[] controls;
    private static final long serialVersionUID = 5913778898401784775L;
    
    UnsolicitedResponseImpl(final String oid, final byte[] extensionValue, final Vector<Vector<String>> vector, final int n, final String s, final String s2, final Control[] controls) {
        this.oid = oid;
        this.extensionValue = extensionValue;
        if (vector != null && vector.size() > 0) {
            final int size = vector.size();
            this.referrals = new String[size];
            for (int i = 0; i < size; ++i) {
                this.referrals[i] = ((Vector<String>)vector.elementAt(i)).elementAt(0);
            }
        }
        this.exception = LdapCtx.mapErrorCode(n, s);
        this.controls = controls;
    }
    
    @Override
    public String getID() {
        return this.oid;
    }
    
    @Override
    public byte[] getEncodedValue() {
        return this.extensionValue;
    }
    
    @Override
    public String[] getReferrals() {
        return this.referrals;
    }
    
    @Override
    public NamingException getException() {
        return this.exception;
    }
    
    @Override
    public Control[] getControls() throws NamingException {
        return this.controls;
    }
}
