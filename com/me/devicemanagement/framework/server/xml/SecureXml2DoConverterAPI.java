package com.me.devicemanagement.framework.server.xml;

import java.net.URL;
import com.adventnet.persistence.DataObject;

public interface SecureXml2DoConverterAPI
{
    DataObject convertXMLToDO(final byte[] p0);
    
    DataObject convertXMLToDO(final URL p0);
    
    DataObject convertXMLToDO(final String p0);
}
