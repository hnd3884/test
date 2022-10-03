package com.unboundid.ldap.protocol;

import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.InternalUseOnly;
import java.io.Serializable;

@InternalUseOnly
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ProtocolOp extends Serializable
{
    byte getProtocolOpType();
    
    ASN1Element encodeProtocolOp();
    
    void writeTo(final ASN1Buffer p0);
    
    void toString(final StringBuilder p0);
}
