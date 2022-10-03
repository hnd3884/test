package org.apache.xmlbeans.impl.xb.substwsdl;

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

public interface DefinitionsDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DefinitionsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("definitionsc7f1doctype");
    
    Definitions getDefinitions();
    
    void setDefinitions(final Definitions p0);
    
    Definitions addNewDefinitions();
    
    public interface Definitions extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Definitions.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("definitions05ddelemtype");
        
        TImport[] getImportArray();
        
        TImport getImportArray(final int p0);
        
        int sizeOfImportArray();
        
        void setImportArray(final TImport[] p0);
        
        void setImportArray(final int p0, final TImport p1);
        
        TImport insertNewImport(final int p0);
        
        TImport addNewImport();
        
        void removeImport(final int p0);
        
        XmlObject[] getTypesArray();
        
        XmlObject getTypesArray(final int p0);
        
        int sizeOfTypesArray();
        
        void setTypesArray(final XmlObject[] p0);
        
        void setTypesArray(final int p0, final XmlObject p1);
        
        XmlObject insertNewTypes(final int p0);
        
        XmlObject addNewTypes();
        
        void removeTypes(final int p0);
        
        XmlObject[] getMessageArray();
        
        XmlObject getMessageArray(final int p0);
        
        int sizeOfMessageArray();
        
        void setMessageArray(final XmlObject[] p0);
        
        void setMessageArray(final int p0, final XmlObject p1);
        
        XmlObject insertNewMessage(final int p0);
        
        XmlObject addNewMessage();
        
        void removeMessage(final int p0);
        
        XmlObject[] getBindingArray();
        
        XmlObject getBindingArray(final int p0);
        
        int sizeOfBindingArray();
        
        void setBindingArray(final XmlObject[] p0);
        
        void setBindingArray(final int p0, final XmlObject p1);
        
        XmlObject insertNewBinding(final int p0);
        
        XmlObject addNewBinding();
        
        void removeBinding(final int p0);
        
        XmlObject[] getPortTypeArray();
        
        XmlObject getPortTypeArray(final int p0);
        
        int sizeOfPortTypeArray();
        
        void setPortTypeArray(final XmlObject[] p0);
        
        void setPortTypeArray(final int p0, final XmlObject p1);
        
        XmlObject insertNewPortType(final int p0);
        
        XmlObject addNewPortType();
        
        void removePortType(final int p0);
        
        XmlObject[] getServiceArray();
        
        XmlObject getServiceArray(final int p0);
        
        int sizeOfServiceArray();
        
        void setServiceArray(final XmlObject[] p0);
        
        void setServiceArray(final int p0, final XmlObject p1);
        
        XmlObject insertNewService(final int p0);
        
        XmlObject addNewService();
        
        void removeService(final int p0);
        
        public static final class Factory
        {
            public static Definitions newInstance() {
                return (Definitions)XmlBeans.getContextTypeLoader().newInstance(Definitions.type, null);
            }
            
            public static Definitions newInstance(final XmlOptions options) {
                return (Definitions)XmlBeans.getContextTypeLoader().newInstance(Definitions.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static DefinitionsDocument newInstance() {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().newInstance(DefinitionsDocument.type, null);
        }
        
        public static DefinitionsDocument newInstance(final XmlOptions options) {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().newInstance(DefinitionsDocument.type, options);
        }
        
        public static DefinitionsDocument parse(final String xmlAsString) throws XmlException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, DefinitionsDocument.type, null);
        }
        
        public static DefinitionsDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, DefinitionsDocument.type, options);
        }
        
        public static DefinitionsDocument parse(final File file) throws XmlException, IOException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(file, DefinitionsDocument.type, null);
        }
        
        public static DefinitionsDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(file, DefinitionsDocument.type, options);
        }
        
        public static DefinitionsDocument parse(final URL u) throws XmlException, IOException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(u, DefinitionsDocument.type, null);
        }
        
        public static DefinitionsDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(u, DefinitionsDocument.type, options);
        }
        
        public static DefinitionsDocument parse(final InputStream is) throws XmlException, IOException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(is, DefinitionsDocument.type, null);
        }
        
        public static DefinitionsDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(is, DefinitionsDocument.type, options);
        }
        
        public static DefinitionsDocument parse(final Reader r) throws XmlException, IOException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(r, DefinitionsDocument.type, null);
        }
        
        public static DefinitionsDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(r, DefinitionsDocument.type, options);
        }
        
        public static DefinitionsDocument parse(final XMLStreamReader sr) throws XmlException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(sr, DefinitionsDocument.type, null);
        }
        
        public static DefinitionsDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(sr, DefinitionsDocument.type, options);
        }
        
        public static DefinitionsDocument parse(final Node node) throws XmlException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(node, DefinitionsDocument.type, null);
        }
        
        public static DefinitionsDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(node, DefinitionsDocument.type, options);
        }
        
        @Deprecated
        public static DefinitionsDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(xis, DefinitionsDocument.type, null);
        }
        
        @Deprecated
        public static DefinitionsDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (DefinitionsDocument)XmlBeans.getContextTypeLoader().parse(xis, DefinitionsDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DefinitionsDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DefinitionsDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
