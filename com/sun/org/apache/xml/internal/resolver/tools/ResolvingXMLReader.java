package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import jdk.xml.internal.JdkXmlUtils;

public class ResolvingXMLReader extends ResolvingXMLFilter
{
    public static boolean namespaceAware;
    public static boolean validating;
    
    public ResolvingXMLReader() {
        final SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
        spf.setValidating(ResolvingXMLReader.validating);
        try {
            final SAXParser parser = spf.newSAXParser();
            this.setParent(parser.getXMLReader());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public ResolvingXMLReader(final CatalogManager manager) {
        super(manager);
        final SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
        spf.setValidating(ResolvingXMLReader.validating);
        try {
            final SAXParser parser = spf.newSAXParser();
            this.setParent(parser.getXMLReader());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static {
        ResolvingXMLReader.namespaceAware = true;
        ResolvingXMLReader.validating = false;
    }
}
