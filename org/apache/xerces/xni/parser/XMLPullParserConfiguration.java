package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XNIException;
import java.io.IOException;

public interface XMLPullParserConfiguration extends XMLParserConfiguration
{
    void setInputSource(final XMLInputSource p0) throws XMLConfigurationException, IOException;
    
    boolean parse(final boolean p0) throws XNIException, IOException;
    
    void cleanup();
}
