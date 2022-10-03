package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class LDAPConstraints implements Serializable
{
    private static final long serialVersionUID = 6843729471197926148L;
    private boolean followReferrals;
    private int hopLimit;
    private int timeLimit;
    private LDAPBind bindProc;
    private LDAPControl[] clientControls;
    private LDAPControl[] serverControls;
    private LDAPRebind rebindProc;
    
    public LDAPConstraints() {
        this.bindProc = null;
        this.clientControls = new LDAPControl[0];
        this.followReferrals = false;
        this.hopLimit = 5;
        this.rebindProc = null;
        this.serverControls = new LDAPControl[0];
        this.timeLimit = 0;
    }
    
    public LDAPConstraints(final int msLimit, final boolean doReferrals, final LDAPBind bindProc, final int hopLimit) {
        this();
        this.timeLimit = msLimit;
        this.followReferrals = doReferrals;
        this.bindProc = bindProc;
        this.hopLimit = hopLimit;
    }
    
    public LDAPConstraints(final int msLimit, final boolean doReferrals, final LDAPRebind rebindProc, final int hopLimit) {
        this();
        this.timeLimit = msLimit;
        this.followReferrals = doReferrals;
        this.rebindProc = rebindProc;
        this.hopLimit = hopLimit;
    }
    
    public int getTimeLimit() {
        return this.timeLimit;
    }
    
    public void setTimeLimit(final int timeLimit) {
        if (timeLimit < 0) {
            this.timeLimit = 0;
        }
        else {
            this.timeLimit = timeLimit;
        }
    }
    
    public boolean getReferrals() {
        return this.followReferrals;
    }
    
    public void setReferrals(final boolean doReferrals) {
        this.followReferrals = doReferrals;
    }
    
    public LDAPBind getBindProc() {
        return this.bindProc;
    }
    
    public void setBindProc(final LDAPBind bindProc) {
        this.bindProc = bindProc;
    }
    
    public LDAPRebind getRebindProc() {
        return this.rebindProc;
    }
    
    public void setRebindProc(final LDAPRebind rebindProc) {
        this.rebindProc = rebindProc;
    }
    
    public int getHopLimit() {
        return this.hopLimit;
    }
    
    public void setHopLimit(final int hopLimit) {
        if (hopLimit < 0) {
            this.hopLimit = 0;
        }
        else {
            this.hopLimit = hopLimit;
        }
    }
    
    public LDAPControl[] getClientControls() {
        return this.clientControls;
    }
    
    public void setClientControls(final LDAPControl control) {
        this.clientControls = new LDAPControl[] { control };
    }
    
    public void setClientControls(final LDAPControl[] controls) {
        if (controls == null) {
            this.clientControls = new LDAPControl[0];
        }
        else {
            this.clientControls = controls;
        }
    }
    
    public LDAPControl[] getServerControls() {
        return this.serverControls;
    }
    
    public void setServerControls(final LDAPControl control) {
        this.serverControls = new LDAPControl[] { control };
    }
    
    public void setServerControls(final LDAPControl[] controls) {
        if (controls == null) {
            this.serverControls = new LDAPControl[0];
        }
        else {
            this.serverControls = controls;
        }
    }
    
    public LDAPConstraints duplicate() {
        final LDAPConstraints c = new LDAPConstraints();
        c.bindProc = this.bindProc;
        c.clientControls = this.clientControls;
        c.followReferrals = this.followReferrals;
        c.hopLimit = this.hopLimit;
        c.rebindProc = this.rebindProc;
        c.serverControls = this.serverControls;
        c.timeLimit = this.timeLimit;
        return c;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("LDAPConstraints(followReferrals=");
        buffer.append(this.followReferrals);
        buffer.append(", bindProc=");
        buffer.append(String.valueOf(this.bindProc));
        buffer.append(", rebindProc=");
        buffer.append(String.valueOf(this.rebindProc));
        buffer.append(", hopLimit=");
        buffer.append(this.hopLimit);
        buffer.append(", timeLimit=");
        buffer.append(this.timeLimit);
        buffer.append(", clientControls={");
        for (int i = 0; i < this.clientControls.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(this.clientControls[i].toString());
        }
        buffer.append("}, serverControls={");
        for (int i = 0; i < this.serverControls.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(this.serverControls[i].toString());
        }
        buffer.append("})");
        return buffer.toString();
    }
}
