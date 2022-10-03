package com.adventnet.webclient.xml;

import java.io.File;
import com.adventnet.webclient.ClientException;
import org.w3c.dom.Element;
import java.util.Properties;

public interface XmlAction
{
    Element getRootElement(final Properties p0) throws ClientException;
    
    File getXslFile(final Properties p0);
}
