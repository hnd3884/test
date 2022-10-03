package org.apache.xmlbeans.impl.xb.xmlconfig;

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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface Extensionconfig extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Extensionconfig.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("extensionconfig2ac2type");
    
    Interface[] getInterfaceArray();
    
    Interface getInterfaceArray(final int p0);
    
    int sizeOfInterfaceArray();
    
    void setInterfaceArray(final Interface[] p0);
    
    void setInterfaceArray(final int p0, final Interface p1);
    
    Interface insertNewInterface(final int p0);
    
    Interface addNewInterface();
    
    void removeInterface(final int p0);
    
    PrePostSet getPrePostSet();
    
    boolean isSetPrePostSet();
    
    void setPrePostSet(final PrePostSet p0);
    
    PrePostSet addNewPrePostSet();
    
    void unsetPrePostSet();
    
    Object getFor();
    
    JavaNameList xgetFor();
    
    boolean isSetFor();
    
    void setFor(final Object p0);
    
    void xsetFor(final JavaNameList p0);
    
    void unsetFor();
    
    public interface Interface extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Interface.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("interface02a7elemtype");
        
        String getStaticHandler();
        
        XmlString xgetStaticHandler();
        
        void setStaticHandler(final String p0);
        
        void xsetStaticHandler(final XmlString p0);
        
        String getName();
        
        XmlString xgetName();
        
        boolean isSetName();
        
        void setName(final String p0);
        
        void xsetName(final XmlString p0);
        
        void unsetName();
        
        public static final class Factory
        {
            public static Interface newInstance() {
                return (Interface)XmlBeans.getContextTypeLoader().newInstance(Interface.type, null);
            }
            
            public static Interface newInstance(final XmlOptions options) {
                return (Interface)XmlBeans.getContextTypeLoader().newInstance(Interface.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public interface PrePostSet extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PrePostSet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("prepostset5c9delemtype");
        
        String getStaticHandler();
        
        XmlString xgetStaticHandler();
        
        void setStaticHandler(final String p0);
        
        void xsetStaticHandler(final XmlString p0);
        
        public static final class Factory
        {
            public static PrePostSet newInstance() {
                return (PrePostSet)XmlBeans.getContextTypeLoader().newInstance(PrePostSet.type, null);
            }
            
            public static PrePostSet newInstance(final XmlOptions options) {
                return (PrePostSet)XmlBeans.getContextTypeLoader().newInstance(PrePostSet.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static Extensionconfig newInstance() {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().newInstance(Extensionconfig.type, null);
        }
        
        public static Extensionconfig newInstance(final XmlOptions options) {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().newInstance(Extensionconfig.type, options);
        }
        
        public static Extensionconfig parse(final String xmlAsString) throws XmlException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(xmlAsString, Extensionconfig.type, null);
        }
        
        public static Extensionconfig parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(xmlAsString, Extensionconfig.type, options);
        }
        
        public static Extensionconfig parse(final File file) throws XmlException, IOException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(file, Extensionconfig.type, null);
        }
        
        public static Extensionconfig parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(file, Extensionconfig.type, options);
        }
        
        public static Extensionconfig parse(final URL u) throws XmlException, IOException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(u, Extensionconfig.type, null);
        }
        
        public static Extensionconfig parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(u, Extensionconfig.type, options);
        }
        
        public static Extensionconfig parse(final InputStream is) throws XmlException, IOException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(is, Extensionconfig.type, null);
        }
        
        public static Extensionconfig parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(is, Extensionconfig.type, options);
        }
        
        public static Extensionconfig parse(final Reader r) throws XmlException, IOException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(r, Extensionconfig.type, null);
        }
        
        public static Extensionconfig parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(r, Extensionconfig.type, options);
        }
        
        public static Extensionconfig parse(final XMLStreamReader sr) throws XmlException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(sr, Extensionconfig.type, null);
        }
        
        public static Extensionconfig parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(sr, Extensionconfig.type, options);
        }
        
        public static Extensionconfig parse(final Node node) throws XmlException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(node, Extensionconfig.type, null);
        }
        
        public static Extensionconfig parse(final Node node, final XmlOptions options) throws XmlException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(node, Extensionconfig.type, options);
        }
        
        @Deprecated
        public static Extensionconfig parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(xis, Extensionconfig.type, null);
        }
        
        @Deprecated
        public static Extensionconfig parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Extensionconfig)XmlBeans.getContextTypeLoader().parse(xis, Extensionconfig.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Extensionconfig.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Extensionconfig.type, options);
        }
        
        private Factory() {
        }
    }
}
