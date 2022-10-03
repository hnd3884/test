package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1Integer;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public abstract class BindRequest extends LDAPRequest
{
    protected static final ASN1Integer VERSION_ELEMENT;
    private static final long serialVersionUID = -1509925217235385907L;
    
    protected BindRequest(final Control[] controls) {
        super(controls);
    }
    
    @Override
    protected abstract BindResult process(final LDAPConnection p0, final int p1) throws LDAPException;
    
    @Override
    public final OperationType getOperationType() {
        return OperationType.BIND;
    }
    
    public abstract String getBindType();
    
    @Override
    public abstract BindRequest duplicate();
    
    @Override
    public abstract BindRequest duplicate(final Control[] p0);
    
    public BindRequest getRebindRequest(final String host, final int port) {
        return null;
    }
    
    static {
        VERSION_ELEMENT = new ASN1Integer(3);
    }
}
