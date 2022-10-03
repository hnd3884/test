package com.sun.org.apache.xerces.internal.impl;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;

public interface ExternalSubsetResolver extends XMLEntityResolver
{
    XMLInputSource getExternalSubset(final XMLDTDDescription p0) throws XNIException, IOException;
}
