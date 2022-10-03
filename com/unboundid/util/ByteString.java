package com.unboundid.util;

import com.unboundid.asn1.ASN1OctetString;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface ByteString extends Serializable
{
    byte[] getValue();
    
    String stringValue();
    
    void appendValueTo(final ByteStringBuffer p0);
    
    ASN1OctetString toASN1OctetString();
}
