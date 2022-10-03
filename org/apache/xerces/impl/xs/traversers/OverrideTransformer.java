package org.apache.xerces.impl.xs.traversers;

import org.w3c.dom.Element;

public abstract class OverrideTransformer
{
    protected static final int OVERRIDE_SIMPLE_TYPE = 1;
    protected static final int OVERRIDE_COMPLEX_TYPE = 2;
    protected static final int OVERRIDE_ATTRIBUTE_GROUP = 3;
    protected static final int OVERRIDE_GROUP = 4;
    protected static final int OVERRIDE_ELEMENT = 5;
    protected static final int OVERRIDE_NOTATION = 6;
    protected static final int OVERRIDE_ATTRIBUTE = 7;
    
    protected OverrideTransformer() {
    }
    
    public abstract Element transform(final Element p0, final Element p1) throws OverrideTransformException;
}
