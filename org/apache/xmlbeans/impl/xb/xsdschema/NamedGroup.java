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
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface NamedGroup extends RealGroup
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NamedGroup.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("namedgroup878dtype");
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    public interface All extends org.apache.xmlbeans.impl.xb.xsdschema.All
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(All.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("all82daelemtype");
        
        public static final class Factory
        {
            public static All newInstance() {
                return (All)XmlBeans.getContextTypeLoader().newInstance(All.type, null);
            }
            
            public static All newInstance(final XmlOptions options) {
                return (All)XmlBeans.getContextTypeLoader().newInstance(All.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static NamedGroup newInstance() {
            return (NamedGroup)XmlBeans.getContextTypeLoader().newInstance(NamedGroup.type, null);
        }
        
        public static NamedGroup newInstance(final XmlOptions options) {
            return (NamedGroup)XmlBeans.getContextTypeLoader().newInstance(NamedGroup.type, options);
        }
        
        public static NamedGroup parse(final String xmlAsString) throws XmlException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, NamedGroup.type, null);
        }
        
        public static NamedGroup parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, NamedGroup.type, options);
        }
        
        public static NamedGroup parse(final File file) throws XmlException, IOException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(file, NamedGroup.type, null);
        }
        
        public static NamedGroup parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(file, NamedGroup.type, options);
        }
        
        public static NamedGroup parse(final URL u) throws XmlException, IOException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(u, NamedGroup.type, null);
        }
        
        public static NamedGroup parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(u, NamedGroup.type, options);
        }
        
        public static NamedGroup parse(final InputStream is) throws XmlException, IOException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(is, NamedGroup.type, null);
        }
        
        public static NamedGroup parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(is, NamedGroup.type, options);
        }
        
        public static NamedGroup parse(final Reader r) throws XmlException, IOException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(r, NamedGroup.type, null);
        }
        
        public static NamedGroup parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(r, NamedGroup.type, options);
        }
        
        public static NamedGroup parse(final XMLStreamReader sr) throws XmlException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(sr, NamedGroup.type, null);
        }
        
        public static NamedGroup parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(sr, NamedGroup.type, options);
        }
        
        public static NamedGroup parse(final Node node) throws XmlException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(node, NamedGroup.type, null);
        }
        
        public static NamedGroup parse(final Node node, final XmlOptions options) throws XmlException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(node, NamedGroup.type, options);
        }
        
        @Deprecated
        public static NamedGroup parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(xis, NamedGroup.type, null);
        }
        
        @Deprecated
        public static NamedGroup parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (NamedGroup)XmlBeans.getContextTypeLoader().parse(xis, NamedGroup.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NamedGroup.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NamedGroup.type, options);
        }
        
        private Factory() {
        }
    }
}
