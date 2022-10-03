package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;
import java.io.Serializable;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface DecodeableControl extends Serializable
{
    Control decodeControl(final String p0, final boolean p1, final ASN1OctetString p2) throws LDAPException;
}
