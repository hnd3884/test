package org.apache.xerces.xni.grammars;

import org.apache.xerces.xs.XSModel;

public interface XSGrammar extends Grammar
{
    XSModel toXSModel();
    
    XSModel toXSModel(final XSGrammar[] p0);
}
