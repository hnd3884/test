package com.unboundid.ldap.sdk;

import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.Validator;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public abstract class LDAPRequest implements ReadOnlyLDAPRequest
{
    static final Control[] NO_CONTROLS;
    private static final long serialVersionUID = -2040756188243320117L;
    private Boolean followReferrals;
    private Control[] controls;
    private IntermediateResponseListener intermediateResponseListener;
    private long responseTimeout;
    private ReferralConnector referralConnector;
    
    protected LDAPRequest(final Control[] controls) {
        if (controls == null) {
            this.controls = LDAPRequest.NO_CONTROLS;
        }
        else {
            this.controls = controls;
        }
        this.followReferrals = null;
        this.responseTimeout = -1L;
        this.intermediateResponseListener = null;
        this.referralConnector = null;
    }
    
    public final Control[] getControls() {
        return this.controls;
    }
    
    @Override
    public final List<Control> getControlList() {
        return Collections.unmodifiableList((List<? extends Control>)Arrays.asList((T[])this.controls));
    }
    
    @Override
    public final boolean hasControl() {
        return this.controls.length > 0;
    }
    
    @Override
    public final boolean hasControl(final String oid) {
        Validator.ensureNotNull(oid);
        for (final Control c : this.controls) {
            if (c.getOID().equals(oid)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final Control getControl(final String oid) {
        Validator.ensureNotNull(oid);
        for (final Control c : this.controls) {
            if (c.getOID().equals(oid)) {
                return c;
            }
        }
        return null;
    }
    
    final void setControlsInternal(final Control[] controls) {
        this.controls = controls;
    }
    
    @Override
    public final long getResponseTimeoutMillis(final LDAPConnection connection) {
        if (this.responseTimeout >= 0L || connection == null) {
            return this.responseTimeout;
        }
        if (this instanceof ExtendedRequest) {
            final ExtendedRequest extendedRequest = (ExtendedRequest)this;
            return connection.getConnectionOptions().getExtendedOperationResponseTimeoutMillis(extendedRequest.getOID());
        }
        return connection.getConnectionOptions().getResponseTimeoutMillis(this.getOperationType());
    }
    
    public final void setResponseTimeoutMillis(final long responseTimeout) {
        if (responseTimeout < 0L) {
            this.responseTimeout = -1L;
        }
        else {
            this.responseTimeout = responseTimeout;
        }
    }
    
    @Override
    public final boolean followReferrals(final LDAPConnection connection) {
        if (this.followReferrals == null) {
            return connection.getConnectionOptions().followReferrals();
        }
        return this.followReferrals;
    }
    
    final Boolean followReferralsInternal() {
        return this.followReferrals;
    }
    
    public final void setFollowReferrals(final Boolean followReferrals) {
        this.followReferrals = followReferrals;
    }
    
    @Override
    public final ReferralConnector getReferralConnector(final LDAPConnection connection) {
        if (this.referralConnector == null) {
            return connection.getReferralConnector();
        }
        return this.referralConnector;
    }
    
    final ReferralConnector getReferralConnectorInternal() {
        return this.referralConnector;
    }
    
    public final void setReferralConnector(final ReferralConnector referralConnector) {
        this.referralConnector = referralConnector;
    }
    
    public final IntermediateResponseListener getIntermediateResponseListener() {
        return this.intermediateResponseListener;
    }
    
    public final void setIntermediateResponseListener(final IntermediateResponseListener listener) {
        this.intermediateResponseListener = listener;
    }
    
    @InternalUseOnly
    protected abstract LDAPResult process(final LDAPConnection p0, final int p1) throws LDAPException;
    
    public abstract int getLastMessageID();
    
    public abstract OperationType getOperationType();
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public abstract void toString(final StringBuilder p0);
    
    static {
        NO_CONTROLS = new Control[0];
    }
}
