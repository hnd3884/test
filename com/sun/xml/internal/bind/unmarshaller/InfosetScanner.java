package com.sun.xml.internal.bind.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface InfosetScanner<XmlNode>
{
    void scan(final XmlNode p0) throws SAXException;
    
    void setContentHandler(final ContentHandler p0);
    
    ContentHandler getContentHandler();
    
    XmlNode getCurrentElement();
    
    LocatorEx getLocator();
}
