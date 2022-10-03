package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class InMemorySASLBindHandler
{
    public abstract String getSASLMechanismName();
    
    public abstract BindResult processSASLBind(final InMemoryRequestHandler p0, final int p1, final DN p2, final ASN1OctetString p3, final List<Control> p4);
    
    @Override
    public String toString() {
        return "InMemorySASLBindHandler(mechanismName='" + this.getSASLMechanismName() + ')';
    }
}
