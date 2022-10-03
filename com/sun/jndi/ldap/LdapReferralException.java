package com.sun.jndi.ldap;

import javax.naming.ReferralException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import java.util.Vector;
import javax.naming.ldap.Control;
import java.util.Hashtable;

public final class LdapReferralException extends javax.naming.ldap.LdapReferralException
{
    private static final long serialVersionUID = 627059076356906399L;
    private int handleReferrals;
    private Hashtable<?, ?> envprops;
    private String nextName;
    private Control[] reqCtls;
    private Vector<?> referrals;
    private int referralIndex;
    private int referralCount;
    private boolean foundEntry;
    private boolean skipThisReferral;
    private int hopCount;
    private NamingException errorEx;
    private String newRdn;
    private boolean debug;
    LdapReferralException nextReferralEx;
    
    LdapReferralException(final Name resolvedName, final Object resolvedObj, final Name remainingName, final String s, final Hashtable<?, ?> envprops, final String nextName, final int handleReferrals, final Control[] array) {
        super(s);
        this.referrals = null;
        this.referralIndex = 0;
        this.referralCount = 0;
        this.foundEntry = false;
        this.skipThisReferral = false;
        this.hopCount = 1;
        this.errorEx = null;
        this.newRdn = null;
        this.debug = false;
        this.nextReferralEx = null;
        if (this.debug) {
            System.out.println("LdapReferralException constructor");
        }
        this.setResolvedName(resolvedName);
        this.setResolvedObj(resolvedObj);
        this.setRemainingName(remainingName);
        this.envprops = envprops;
        this.nextName = nextName;
        this.handleReferrals = handleReferrals;
        this.reqCtls = (Control[])((handleReferrals == 1 || handleReferrals == 4) ? array : null);
    }
    
    @Override
    public Context getReferralContext() throws NamingException {
        return this.getReferralContext(this.envprops, null);
    }
    
    @Override
    public Context getReferralContext(final Hashtable<?, ?> hashtable) throws NamingException {
        return this.getReferralContext(hashtable, null);
    }
    
    @Override
    public Context getReferralContext(final Hashtable<?, ?> hashtable, final Control[] array) throws NamingException {
        if (this.debug) {
            System.out.println("LdapReferralException.getReferralContext");
        }
        final LdapReferralContext ldapReferralContext = new LdapReferralContext(this, hashtable, array, this.reqCtls, this.nextName, this.skipThisReferral, this.handleReferrals);
        ldapReferralContext.setHopCount(this.hopCount + 1);
        if (this.skipThisReferral) {
            this.skipThisReferral = false;
        }
        return ldapReferralContext;
    }
    
    @Override
    public Object getReferralInfo() {
        if (this.debug) {
            System.out.println("LdapReferralException.getReferralInfo");
            System.out.println("  referralIndex=" + this.referralIndex);
        }
        if (this.hasMoreReferrals()) {
            return this.referrals.elementAt(this.referralIndex);
        }
        return null;
    }
    
    @Override
    public void retryReferral() {
        if (this.debug) {
            System.out.println("LdapReferralException.retryReferral");
        }
        if (this.referralIndex > 0) {
            --this.referralIndex;
        }
    }
    
    @Override
    public boolean skipReferral() {
        if (this.debug) {
            System.out.println("LdapReferralException.skipReferral");
        }
        this.skipThisReferral = true;
        try {
            this.getNextReferral();
        }
        catch (final ReferralException ex) {}
        return this.hasMoreReferrals() || this.hasMoreReferralExceptions();
    }
    
    void setReferralInfo(final Vector<?> referrals, final boolean b) {
        if (this.debug) {
            System.out.println("LdapReferralException.setReferralInfo");
        }
        this.referrals = referrals;
        this.referralCount = ((referrals == null) ? 0 : referrals.size());
        if (this.debug) {
            if (referrals != null) {
                for (int i = 0; i < this.referralCount; ++i) {
                    System.out.println("  [" + i + "] " + referrals.elementAt(i));
                }
            }
            else {
                System.out.println("setReferralInfo : referrals == null");
            }
        }
    }
    
    String getNextReferral() throws ReferralException {
        if (this.debug) {
            System.out.println("LdapReferralException.getNextReferral");
        }
        if (this.hasMoreReferrals()) {
            return (String)this.referrals.elementAt(this.referralIndex++);
        }
        if (this.hasMoreReferralExceptions()) {
            throw this.nextReferralEx;
        }
        return null;
    }
    
    LdapReferralException appendUnprocessedReferrals(LdapReferralException nextReferralEx) {
        if (this.debug) {
            System.out.println("LdapReferralException.appendUnprocessedReferrals");
            this.dump();
            if (nextReferralEx != null) {
                nextReferralEx.dump();
            }
        }
        LdapReferralException nextReferralEx2 = this;
        if (!nextReferralEx2.hasMoreReferrals()) {
            nextReferralEx2 = this.nextReferralEx;
            if (this.errorEx != null && nextReferralEx2 != null) {
                nextReferralEx2.setNamingException(this.errorEx);
            }
        }
        if (this == nextReferralEx) {
            return nextReferralEx2;
        }
        if (nextReferralEx != null && !nextReferralEx.hasMoreReferrals()) {
            nextReferralEx = nextReferralEx.nextReferralEx;
        }
        if (nextReferralEx == null) {
            return nextReferralEx2;
        }
        LdapReferralException nextReferralEx3;
        for (nextReferralEx3 = nextReferralEx2; nextReferralEx3.nextReferralEx != null; nextReferralEx3 = nextReferralEx3.nextReferralEx) {}
        nextReferralEx3.nextReferralEx = nextReferralEx;
        return nextReferralEx2;
    }
    
    boolean hasMoreReferrals() {
        if (this.debug) {
            System.out.println("LdapReferralException.hasMoreReferrals");
        }
        return !this.foundEntry && this.referralIndex < this.referralCount;
    }
    
    boolean hasMoreReferralExceptions() {
        if (this.debug) {
            System.out.println("LdapReferralException.hasMoreReferralExceptions");
        }
        return this.nextReferralEx != null;
    }
    
    void setHopCount(final int hopCount) {
        if (this.debug) {
            System.out.println("LdapReferralException.setHopCount");
        }
        this.hopCount = hopCount;
    }
    
    void setNameResolved(final boolean foundEntry) {
        if (this.debug) {
            System.out.println("LdapReferralException.setNameResolved");
        }
        this.foundEntry = foundEntry;
    }
    
    void setNamingException(final NamingException errorEx) {
        if (this.debug) {
            System.out.println("LdapReferralException.setNamingException");
        }
        if (this.errorEx == null) {
            errorEx.setRootCause(this);
            this.errorEx = errorEx;
        }
    }
    
    String getNewRdn() {
        if (this.debug) {
            System.out.println("LdapReferralException.getNewRdn");
        }
        return this.newRdn;
    }
    
    void setNewRdn(final String newRdn) {
        if (this.debug) {
            System.out.println("LdapReferralException.setNewRdn");
        }
        this.newRdn = newRdn;
    }
    
    NamingException getNamingException() {
        if (this.debug) {
            System.out.println("LdapReferralException.getNamingException");
        }
        return this.errorEx;
    }
    
    void dump() {
        System.out.println();
        System.out.println("LdapReferralException.dump");
        for (LdapReferralException nextReferralEx = this; nextReferralEx != null; nextReferralEx = nextReferralEx.nextReferralEx) {
            nextReferralEx.dumpState();
        }
    }
    
    private void dumpState() {
        System.out.println("LdapReferralException.dumpState");
        System.out.println("  hashCode=" + this.hashCode());
        System.out.println("  foundEntry=" + this.foundEntry);
        System.out.println("  skipThisReferral=" + this.skipThisReferral);
        System.out.println("  referralIndex=" + this.referralIndex);
        if (this.referrals != null) {
            System.out.println("  referrals:");
            for (int i = 0; i < this.referralCount; ++i) {
                System.out.println("    [" + i + "] " + this.referrals.elementAt(i));
            }
        }
        else {
            System.out.println("  referrals=null");
        }
        System.out.println("  errorEx=" + this.errorEx);
        if (this.nextReferralEx == null) {
            System.out.println("  nextRefEx=null");
        }
        else {
            System.out.println("  nextRefEx=" + this.nextReferralEx.hashCode());
        }
        System.out.println();
    }
}
