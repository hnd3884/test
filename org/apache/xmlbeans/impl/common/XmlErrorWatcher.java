package org.apache.xmlbeans.impl.common;

import java.util.Collections;
import java.util.Iterator;
import org.apache.xmlbeans.XmlError;
import java.util.Collection;
import java.util.AbstractCollection;

public class XmlErrorWatcher extends AbstractCollection
{
    private Collection _underlying;
    private XmlError _firstError;
    
    public XmlErrorWatcher(final Collection underlying) {
        this._underlying = underlying;
    }
    
    @Override
    public boolean add(final Object o) {
        if (this._firstError == null && o instanceof XmlError && ((XmlError)o).getSeverity() == 0) {
            this._firstError = (XmlError)o;
        }
        return this._underlying != null && this._underlying.add(o);
    }
    
    @Override
    public Iterator iterator() {
        if (this._underlying == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return this._underlying.iterator();
    }
    
    @Override
    public int size() {
        if (this._underlying == null) {
            return 0;
        }
        return this._underlying.size();
    }
    
    public boolean hasError() {
        return this._firstError != null;
    }
    
    public XmlError firstError() {
        return this._firstError;
    }
}
