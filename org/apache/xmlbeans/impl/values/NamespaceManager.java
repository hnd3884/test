package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.impl.common.PrefixResolver;

public interface NamespaceManager extends PrefixResolver
{
    String find_prefix_for_nsuri(final String p0, final String p1);
}
