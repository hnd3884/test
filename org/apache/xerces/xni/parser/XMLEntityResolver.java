package org.apache.xerces.xni.parser;

import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.XMLResourceIdentifier;

public interface XMLEntityResolver
{
    XMLInputSource resolveEntity(final XMLResourceIdentifier p0) throws XNIException, IOException;
}
