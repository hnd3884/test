package com.unboundid.ldap.listener;

import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.SearchResultReferenceProtocolOp;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface SearchReferenceTransformer
{
    ObjectPair<SearchResultReferenceProtocolOp, Control[]> transformReference(final int p0, final SearchResultReferenceProtocolOp p1, final Control[] p2);
}
