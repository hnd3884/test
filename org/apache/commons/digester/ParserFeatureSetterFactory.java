package org.apache.commons.digester;

import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.digester.parser.GenericParser;
import org.apache.commons.digester.parser.XercesParser;
import javax.xml.parsers.SAXParser;
import java.util.Properties;

public class ParserFeatureSetterFactory
{
    private static boolean isXercesUsed;
    
    public static SAXParser newSAXParser(final Properties properties) throws ParserConfigurationException, SAXException, SAXNotRecognizedException, SAXNotSupportedException {
        if (ParserFeatureSetterFactory.isXercesUsed) {
            return XercesParser.newSAXParser(properties);
        }
        return GenericParser.newSAXParser(properties);
    }
    
    static {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            if (factory.getClass().getName().startsWith("org.apache.xerces")) {
                ParserFeatureSetterFactory.isXercesUsed = true;
            }
        }
        catch (final Exception ex) {
            ParserFeatureSetterFactory.isXercesUsed = false;
        }
    }
}
