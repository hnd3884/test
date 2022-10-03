package com.sun.org.apache.xerces.internal.xni.parser;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public interface XMLEntityResolver
{
    XMLInputSource resolveEntity(final XMLResourceIdentifier p0) throws XNIException, IOException;
}
