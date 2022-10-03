package com.sun.jndi.ldap;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.Control;
import java.util.Vector;

public final class LdapResult
{
    int msgId;
    public int status;
    String matchedDN;
    String errorMessage;
    Vector<Vector<String>> referrals;
    LdapReferralException refEx;
    Vector<LdapEntry> entries;
    Vector<Control> resControls;
    public byte[] serverCreds;
    String extensionId;
    byte[] extensionValue;
    
    public LdapResult() {
        this.referrals = null;
        this.refEx = null;
        this.entries = null;
        this.resControls = null;
        this.serverCreds = null;
        this.extensionId = null;
        this.extensionValue = null;
    }
    
    boolean compareToSearchResult(final String s) {
        boolean b = false;
        switch (this.status) {
            case 6: {
                this.status = 0;
                (this.entries = new Vector<LdapEntry>(1, 1)).addElement(new LdapEntry(s, new BasicAttributes(true)));
                b = true;
                break;
            }
            case 5: {
                this.status = 0;
                this.entries = new Vector<LdapEntry>(0);
                b = true;
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
}
