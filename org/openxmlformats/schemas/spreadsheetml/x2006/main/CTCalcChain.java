package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCalcChain extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCalcChain.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcalcchain5a0btype");
    
    List<CTCalcCell> getCList();
    
    @Deprecated
    CTCalcCell[] getCArray();
    
    CTCalcCell getCArray(final int p0);
    
    int sizeOfCArray();
    
    void setCArray(final CTCalcCell[] p0);
    
    void setCArray(final int p0, final CTCalcCell p1);
    
    CTCalcCell insertNewC(final int p0);
    
    CTCalcCell addNewC();
    
    void removeC(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCalcChain.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCalcChain newInstance() {
            return (CTCalcChain)getTypeLoader().newInstance(CTCalcChain.type, (XmlOptions)null);
        }
        
        public static CTCalcChain newInstance(final XmlOptions xmlOptions) {
            return (CTCalcChain)getTypeLoader().newInstance(CTCalcChain.type, xmlOptions);
        }
        
        public static CTCalcChain parse(final String s) throws XmlException {
            return (CTCalcChain)getTypeLoader().parse(s, CTCalcChain.type, (XmlOptions)null);
        }
        
        public static CTCalcChain parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCalcChain)getTypeLoader().parse(s, CTCalcChain.type, xmlOptions);
        }
        
        public static CTCalcChain parse(final File file) throws XmlException, IOException {
            return (CTCalcChain)getTypeLoader().parse(file, CTCalcChain.type, (XmlOptions)null);
        }
        
        public static CTCalcChain parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcChain)getTypeLoader().parse(file, CTCalcChain.type, xmlOptions);
        }
        
        public static CTCalcChain parse(final URL url) throws XmlException, IOException {
            return (CTCalcChain)getTypeLoader().parse(url, CTCalcChain.type, (XmlOptions)null);
        }
        
        public static CTCalcChain parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcChain)getTypeLoader().parse(url, CTCalcChain.type, xmlOptions);
        }
        
        public static CTCalcChain parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCalcChain)getTypeLoader().parse(inputStream, CTCalcChain.type, (XmlOptions)null);
        }
        
        public static CTCalcChain parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcChain)getTypeLoader().parse(inputStream, CTCalcChain.type, xmlOptions);
        }
        
        public static CTCalcChain parse(final Reader reader) throws XmlException, IOException {
            return (CTCalcChain)getTypeLoader().parse(reader, CTCalcChain.type, (XmlOptions)null);
        }
        
        public static CTCalcChain parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcChain)getTypeLoader().parse(reader, CTCalcChain.type, xmlOptions);
        }
        
        public static CTCalcChain parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCalcChain)getTypeLoader().parse(xmlStreamReader, CTCalcChain.type, (XmlOptions)null);
        }
        
        public static CTCalcChain parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCalcChain)getTypeLoader().parse(xmlStreamReader, CTCalcChain.type, xmlOptions);
        }
        
        public static CTCalcChain parse(final Node node) throws XmlException {
            return (CTCalcChain)getTypeLoader().parse(node, CTCalcChain.type, (XmlOptions)null);
        }
        
        public static CTCalcChain parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCalcChain)getTypeLoader().parse(node, CTCalcChain.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCalcChain parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCalcChain)getTypeLoader().parse(xmlInputStream, CTCalcChain.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCalcChain parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCalcChain)getTypeLoader().parse(xmlInputStream, CTCalcChain.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCalcChain.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCalcChain.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
