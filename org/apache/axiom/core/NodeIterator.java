package org.apache.axiom.core;

import java.util.Iterator;

public interface NodeIterator<T> extends Iterator<T>
{
    void replace(final CoreChildNode p0) throws CoreModelException;
}
