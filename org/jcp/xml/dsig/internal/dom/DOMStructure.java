package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;
import javax.xml.crypto.XMLStructure;

public abstract class DOMStructure implements XMLStructure
{
    public final boolean isFeatureSupported(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        return false;
    }
    
    public abstract void marshal(final Node p0, final String p1, final DOMCryptoContext p2) throws MarshalException;
}
