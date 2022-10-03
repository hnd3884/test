package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;

public interface RealGroup extends Group
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(RealGroup.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("realgroup1f64type");
    
    All[] getAllArray();
    
    All getAllArray(final int p0);
    
    int sizeOfAllArray();
    
    void setAllArray(final All[] p0);
    
    void setAllArray(final int p0, final All p1);
    
    All insertNewAll(final int p0);
    
    All addNewAll();
    
    void removeAll(final int p0);
    
    ExplicitGroup[] getChoiceArray();
    
    ExplicitGroup getChoiceArray(final int p0);
    
    int sizeOfChoiceArray();
    
    void setChoiceArray(final ExplicitGroup[] p0);
    
    void setChoiceArray(final int p0, final ExplicitGroup p1);
    
    ExplicitGroup insertNewChoice(final int p0);
    
    ExplicitGroup addNewChoice();
    
    void removeChoice(final int p0);
    
    ExplicitGroup[] getSequenceArray();
    
    ExplicitGroup getSequenceArray(final int p0);
    
    int sizeOfSequenceArray();
    
    void setSequenceArray(final ExplicitGroup[] p0);
    
    void setSequenceArray(final int p0, final ExplicitGroup p1);
    
    ExplicitGroup insertNewSequence(final int p0);
    
    ExplicitGroup addNewSequence();
    
    void removeSequence(final int p0);
    
    public static final class Factory
    {
        public static RealGroup newInstance() {
            return (RealGroup)XmlBeans.getContextTypeLoader().newInstance(RealGroup.type, null);
        }
        
        public static RealGroup newInstance(final XmlOptions options) {
            return (RealGroup)XmlBeans.getContextTypeLoader().newInstance(RealGroup.type, options);
        }
        
        public static RealGroup parse(final String xmlAsString) throws XmlException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, RealGroup.type, null);
        }
        
        public static RealGroup parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, RealGroup.type, options);
        }
        
        public static RealGroup parse(final File file) throws XmlException, IOException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(file, RealGroup.type, null);
        }
        
        public static RealGroup parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(file, RealGroup.type, options);
        }
        
        public static RealGroup parse(final URL u) throws XmlException, IOException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(u, RealGroup.type, null);
        }
        
        public static RealGroup parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(u, RealGroup.type, options);
        }
        
        public static RealGroup parse(final InputStream is) throws XmlException, IOException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(is, RealGroup.type, null);
        }
        
        public static RealGroup parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(is, RealGroup.type, options);
        }
        
        public static RealGroup parse(final Reader r) throws XmlException, IOException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(r, RealGroup.type, null);
        }
        
        public static RealGroup parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(r, RealGroup.type, options);
        }
        
        public static RealGroup parse(final XMLStreamReader sr) throws XmlException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(sr, RealGroup.type, null);
        }
        
        public static RealGroup parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(sr, RealGroup.type, options);
        }
        
        public static RealGroup parse(final Node node) throws XmlException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(node, RealGroup.type, null);
        }
        
        public static RealGroup parse(final Node node, final XmlOptions options) throws XmlException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(node, RealGroup.type, options);
        }
        
        @Deprecated
        public static RealGroup parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(xis, RealGroup.type, null);
        }
        
        @Deprecated
        public static RealGroup parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (RealGroup)XmlBeans.getContextTypeLoader().parse(xis, RealGroup.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, RealGroup.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, RealGroup.type, options);
        }
        
        private Factory() {
        }
    }
}
