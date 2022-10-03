package org.apache.xerces.dom;

import java.util.StringTokenizer;
import java.util.ArrayList;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationSource;

public class DOMImplementationSourceImpl implements DOMImplementationSource
{
    public DOMImplementation getDOMImplementation(final String s) {
        final DOMImplementation domImplementation = CoreDOMImplementationImpl.getDOMImplementation();
        if (this.testImpl(domImplementation, s)) {
            return domImplementation;
        }
        final DOMImplementation domImplementation2 = DOMImplementationImpl.getDOMImplementation();
        if (this.testImpl(domImplementation2, s)) {
            return domImplementation2;
        }
        return null;
    }
    
    public DOMImplementationList getDOMImplementationList(final String s) {
        final DOMImplementation domImplementation = CoreDOMImplementationImpl.getDOMImplementation();
        final ArrayList list = new ArrayList();
        if (this.testImpl(domImplementation, s)) {
            list.add(domImplementation);
        }
        final DOMImplementation domImplementation2 = DOMImplementationImpl.getDOMImplementation();
        if (this.testImpl(domImplementation2, s)) {
            list.add(domImplementation2);
        }
        return new DOMImplementationListImpl(list);
    }
    
    boolean testImpl(final DOMImplementation domImplementation, final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        String s2 = null;
        if (stringTokenizer.hasMoreTokens()) {
            s2 = stringTokenizer.nextToken();
        }
        while (s2 != null) {
            boolean b = false;
            String nextToken;
            if (stringTokenizer.hasMoreTokens()) {
                nextToken = stringTokenizer.nextToken();
                switch (nextToken.charAt(0)) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9': {
                        b = true;
                        break;
                    }
                }
            }
            else {
                nextToken = null;
            }
            if (b) {
                if (!domImplementation.hasFeature(s2, nextToken)) {
                    return false;
                }
                if (stringTokenizer.hasMoreTokens()) {
                    s2 = stringTokenizer.nextToken();
                }
                else {
                    s2 = null;
                }
            }
            else {
                if (!domImplementation.hasFeature(s2, null)) {
                    return false;
                }
                s2 = nextToken;
            }
        }
        return true;
    }
}
