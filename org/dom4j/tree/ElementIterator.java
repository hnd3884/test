package org.dom4j.tree;

import org.dom4j.Element;
import java.util.Iterator;

public class ElementIterator extends FilterIterator
{
    public ElementIterator(final Iterator proxy) {
        super(proxy);
    }
    
    protected boolean matches(final Object element) {
        return element instanceof Element;
    }
}
