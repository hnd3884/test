package com.unboundid.ldap.listener;

import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.SearchResultEntryProtocolOp;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface SearchEntryTransformer
{
    ObjectPair<SearchResultEntryProtocolOp, Control[]> transformEntry(final int p0, final SearchResultEntryProtocolOp p1, final Control[] p2);
}
