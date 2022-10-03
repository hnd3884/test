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
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ConfigDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ConfigDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("config4185doctype");
    
    Config getConfig();
    
    void setConfig(final Config p0);
    
    Config addNewConfig();
    
    public interface Config extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Config.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("configf467elemtype");
        
        Nsconfig[] getNamespaceArray();
        
        Nsconfig getNamespaceArray(final int p0);
        
        int sizeOfNamespaceArray();
        
        void setNamespaceArray(final Nsconfig[] p0);
        
        void setNamespaceArray(final int p0, final Nsconfig p1);
        
        Nsconfig insertNewNamespace(final int p0);
        
        Nsconfig addNewNamespace();
        
        void removeNamespace(final int p0);
        
        Qnameconfig[] getQnameArray();
        
        Qnameconfig getQnameArray(final int p0);
        
        int sizeOfQnameArray();
        
        void setQnameArray(final Qnameconfig[] p0);
        
        void setQnameArray(final int p0, final Qnameconfig p1);
        
        Qnameconfig insertNewQname(final int p0);
        
        Qnameconfig addNewQname();
        
        void removeQname(final int p0);
        
        Extensionconfig[] getExtensionArray();
        
        Extensionconfig getExtensionArray(final int p0);
        
        int sizeOfExtensionArray();
        
        void setExtensionArray(final Extensionconfig[] p0);
        
        void setExtensionArray(final int p0, final Extensionconfig p1);
        
        Extensionconfig insertNewExtension(final int p0);
        
        Extensionconfig addNewExtension();
        
        void removeExtension(final int p0);
        
        Usertypeconfig[] getUsertypeArray();
        
        Usertypeconfig getUsertypeArray(final int p0);
        
        int sizeOfUsertypeArray();
        
        void setUsertypeArray(final Usertypeconfig[] p0);
        
        void setUsertypeArray(final int p0, final Usertypeconfig p1);
        
        Usertypeconfig insertNewUsertype(final int p0);
        
        Usertypeconfig addNewUsertype();
        
        void removeUsertype(final int p0);
        
        public static final class Factory
        {
            public static Config newInstance() {
                return (Config)XmlBeans.getContextTypeLoader().newInstance(Config.type, null);
            }
            
            public static Config newInstance(final XmlOptions options) {
                return (Config)XmlBeans.getContextTypeLoader().newInstance(Config.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static ConfigDocument newInstance() {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().newInstance(ConfigDocument.type, null);
        }
        
        public static ConfigDocument newInstance(final XmlOptions options) {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().newInstance(ConfigDocument.type, options);
        }
        
        public static ConfigDocument parse(final String xmlAsString) throws XmlException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ConfigDocument.type, null);
        }
        
        public static ConfigDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ConfigDocument.type, options);
        }
        
        public static ConfigDocument parse(final File file) throws XmlException, IOException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(file, ConfigDocument.type, null);
        }
        
        public static ConfigDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(file, ConfigDocument.type, options);
        }
        
        public static ConfigDocument parse(final URL u) throws XmlException, IOException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(u, ConfigDocument.type, null);
        }
        
        public static ConfigDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(u, ConfigDocument.type, options);
        }
        
        public static ConfigDocument parse(final InputStream is) throws XmlException, IOException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(is, ConfigDocument.type, null);
        }
        
        public static ConfigDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(is, ConfigDocument.type, options);
        }
        
        public static ConfigDocument parse(final Reader r) throws XmlException, IOException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(r, ConfigDocument.type, null);
        }
        
        public static ConfigDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(r, ConfigDocument.type, options);
        }
        
        public static ConfigDocument parse(final XMLStreamReader sr) throws XmlException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(sr, ConfigDocument.type, null);
        }
        
        public static ConfigDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(sr, ConfigDocument.type, options);
        }
        
        public static ConfigDocument parse(final Node node) throws XmlException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(node, ConfigDocument.type, null);
        }
        
        public static ConfigDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(node, ConfigDocument.type, options);
        }
        
        @Deprecated
        public static ConfigDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(xis, ConfigDocument.type, null);
        }
        
        @Deprecated
        public static ConfigDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ConfigDocument)XmlBeans.getContextTypeLoader().parse(xis, ConfigDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ConfigDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ConfigDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
