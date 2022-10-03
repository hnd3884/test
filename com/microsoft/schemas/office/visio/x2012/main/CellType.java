package com.microsoft.schemas.office.visio.x2012.main;

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
import org.apache.xmlbeans.XmlString;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CellType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CellType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("celltyped857type");
    
    List<RefByType> getRefByList();
    
    @Deprecated
    RefByType[] getRefByArray();
    
    RefByType getRefByArray(final int p0);
    
    int sizeOfRefByArray();
    
    void setRefByArray(final RefByType[] p0);
    
    void setRefByArray(final int p0, final RefByType p1);
    
    RefByType insertNewRefBy(final int p0);
    
    RefByType addNewRefBy();
    
    void removeRefBy(final int p0);
    
    String getN();
    
    XmlString xgetN();
    
    void setN(final String p0);
    
    void xsetN(final XmlString p0);
    
    String getU();
    
    XmlString xgetU();
    
    boolean isSetU();
    
    void setU(final String p0);
    
    void xsetU(final XmlString p0);
    
    void unsetU();
    
    String getE();
    
    XmlString xgetE();
    
    boolean isSetE();
    
    void setE(final String p0);
    
    void xsetE(final XmlString p0);
    
    void unsetE();
    
    String getF();
    
    XmlString xgetF();
    
    boolean isSetF();
    
    void setF(final String p0);
    
    void xsetF(final XmlString p0);
    
    void unsetF();
    
    String getV();
    
    XmlString xgetV();
    
    boolean isSetV();
    
    void setV(final String p0);
    
    void xsetV(final XmlString p0);
    
    void unsetV();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CellType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CellType newInstance() {
            return (CellType)getTypeLoader().newInstance(CellType.type, (XmlOptions)null);
        }
        
        public static CellType newInstance(final XmlOptions xmlOptions) {
            return (CellType)getTypeLoader().newInstance(CellType.type, xmlOptions);
        }
        
        public static CellType parse(final String s) throws XmlException {
            return (CellType)getTypeLoader().parse(s, CellType.type, (XmlOptions)null);
        }
        
        public static CellType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CellType)getTypeLoader().parse(s, CellType.type, xmlOptions);
        }
        
        public static CellType parse(final File file) throws XmlException, IOException {
            return (CellType)getTypeLoader().parse(file, CellType.type, (XmlOptions)null);
        }
        
        public static CellType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CellType)getTypeLoader().parse(file, CellType.type, xmlOptions);
        }
        
        public static CellType parse(final URL url) throws XmlException, IOException {
            return (CellType)getTypeLoader().parse(url, CellType.type, (XmlOptions)null);
        }
        
        public static CellType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CellType)getTypeLoader().parse(url, CellType.type, xmlOptions);
        }
        
        public static CellType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CellType)getTypeLoader().parse(inputStream, CellType.type, (XmlOptions)null);
        }
        
        public static CellType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CellType)getTypeLoader().parse(inputStream, CellType.type, xmlOptions);
        }
        
        public static CellType parse(final Reader reader) throws XmlException, IOException {
            return (CellType)getTypeLoader().parse(reader, CellType.type, (XmlOptions)null);
        }
        
        public static CellType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CellType)getTypeLoader().parse(reader, CellType.type, xmlOptions);
        }
        
        public static CellType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CellType)getTypeLoader().parse(xmlStreamReader, CellType.type, (XmlOptions)null);
        }
        
        public static CellType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CellType)getTypeLoader().parse(xmlStreamReader, CellType.type, xmlOptions);
        }
        
        public static CellType parse(final Node node) throws XmlException {
            return (CellType)getTypeLoader().parse(node, CellType.type, (XmlOptions)null);
        }
        
        public static CellType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CellType)getTypeLoader().parse(node, CellType.type, xmlOptions);
        }
        
        @Deprecated
        public static CellType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CellType)getTypeLoader().parse(xmlInputStream, CellType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CellType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CellType)getTypeLoader().parse(xmlInputStream, CellType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CellType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CellType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
