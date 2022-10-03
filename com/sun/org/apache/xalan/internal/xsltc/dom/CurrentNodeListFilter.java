package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;

public interface CurrentNodeListFilter
{
    boolean test(final int p0, final int p1, final int p2, final int p3, final AbstractTranslet p4, final DTMAxisIterator p5);
}
