package org.apache.xmlbeans.impl.common;

import java.util.Collections;
import java.util.Iterator;
import org.apache.xmlbeans.XmlError;
import java.net.URI;
import java.util.AbstractCollection;

public class XmlErrorPrinter extends AbstractCollection
{
    private boolean _noisy;
    private URI _baseURI;
    
    public XmlErrorPrinter(final boolean noisy, final URI baseURI) {
        this._noisy = noisy;
        this._baseURI = baseURI;
    }
    
    @Override
    public boolean add(final Object o) {
        if (o instanceof XmlError) {
            final XmlError err = (XmlError)o;
            if (err.getSeverity() == 0 || err.getSeverity() == 1) {
                System.err.println(err.toString(this._baseURI));
            }
            else if (this._noisy) {
                System.out.println(err.toString(this._baseURI));
            }
        }
        return false;
    }
    
    @Override
    public Iterator iterator() {
        return Collections.EMPTY_LIST.iterator();
    }
    
    @Override
    public int size() {
        return 0;
    }
}
