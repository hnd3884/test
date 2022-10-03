package org.apache.poi.ooxml.util;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.apache.poi.util.XMLHelper;
import org.xml.sax.XMLReader;
import org.apache.poi.util.Removal;

@Deprecated
@Removal(version = "5.0.0")
public final class SAXHelper
{
    public static XMLReader newXMLReader() throws SAXException, ParserConfigurationException {
        return XMLHelper.newXMLReader();
    }
}
