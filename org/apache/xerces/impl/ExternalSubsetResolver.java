package org.apache.xerces.impl;

import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.grammars.XMLDTDDescription;
import org.apache.xerces.xni.parser.XMLEntityResolver;

public interface ExternalSubsetResolver extends XMLEntityResolver
{
    XMLInputSource getExternalSubset(final XMLDTDDescription p0) throws XNIException, IOException;
}
