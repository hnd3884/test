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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTItem extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTItem.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctitemc69ctype");
    
    String getN();
    
    STXstring xgetN();
    
    boolean isSetN();
    
    void setN(final String p0);
    
    void xsetN(final STXstring p0);
    
    void unsetN();
    
    STItemType.Enum getT();
    
    STItemType xgetT();
    
    boolean isSetT();
    
    void setT(final STItemType.Enum p0);
    
    void xsetT(final STItemType p0);
    
    void unsetT();
    
    boolean getH();
    
    XmlBoolean xgetH();
    
    boolean isSetH();
    
    void setH(final boolean p0);
    
    void xsetH(final XmlBoolean p0);
    
    void unsetH();
    
    boolean getS();
    
    XmlBoolean xgetS();
    
    boolean isSetS();
    
    void setS(final boolean p0);
    
    void xsetS(final XmlBoolean p0);
    
    void unsetS();
    
    boolean getSd();
    
    XmlBoolean xgetSd();
    
    boolean isSetSd();
    
    void setSd(final boolean p0);
    
    void xsetSd(final XmlBoolean p0);
    
    void unsetSd();
    
    boolean getF();
    
    XmlBoolean xgetF();
    
    boolean isSetF();
    
    void setF(final boolean p0);
    
    void xsetF(final XmlBoolean p0);
    
    void unsetF();
    
    boolean getM();
    
    XmlBoolean xgetM();
    
    boolean isSetM();
    
    void setM(final boolean p0);
    
    void xsetM(final XmlBoolean p0);
    
    void unsetM();
    
    boolean getC();
    
    XmlBoolean xgetC();
    
    boolean isSetC();
    
    void setC(final boolean p0);
    
    void xsetC(final XmlBoolean p0);
    
    void unsetC();
    
    long getX();
    
    XmlUnsignedInt xgetX();
    
    boolean isSetX();
    
    void setX(final long p0);
    
    void xsetX(final XmlUnsignedInt p0);
    
    void unsetX();
    
    boolean getD();
    
    XmlBoolean xgetD();
    
    boolean isSetD();
    
    void setD(final boolean p0);
    
    void xsetD(final XmlBoolean p0);
    
    void unsetD();
    
    boolean getE();
    
    XmlBoolean xgetE();
    
    boolean isSetE();
    
    void setE(final boolean p0);
    
    void xsetE(final XmlBoolean p0);
    
    void unsetE();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTItem.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTItem newInstance() {
            return (CTItem)getTypeLoader().newInstance(CTItem.type, (XmlOptions)null);
        }
        
        public static CTItem newInstance(final XmlOptions xmlOptions) {
            return (CTItem)getTypeLoader().newInstance(CTItem.type, xmlOptions);
        }
        
        public static CTItem parse(final String s) throws XmlException {
            return (CTItem)getTypeLoader().parse(s, CTItem.type, (XmlOptions)null);
        }
        
        public static CTItem parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTItem)getTypeLoader().parse(s, CTItem.type, xmlOptions);
        }
        
        public static CTItem parse(final File file) throws XmlException, IOException {
            return (CTItem)getTypeLoader().parse(file, CTItem.type, (XmlOptions)null);
        }
        
        public static CTItem parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTItem)getTypeLoader().parse(file, CTItem.type, xmlOptions);
        }
        
        public static CTItem parse(final URL url) throws XmlException, IOException {
            return (CTItem)getTypeLoader().parse(url, CTItem.type, (XmlOptions)null);
        }
        
        public static CTItem parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTItem)getTypeLoader().parse(url, CTItem.type, xmlOptions);
        }
        
        public static CTItem parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTItem)getTypeLoader().parse(inputStream, CTItem.type, (XmlOptions)null);
        }
        
        public static CTItem parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTItem)getTypeLoader().parse(inputStream, CTItem.type, xmlOptions);
        }
        
        public static CTItem parse(final Reader reader) throws XmlException, IOException {
            return (CTItem)getTypeLoader().parse(reader, CTItem.type, (XmlOptions)null);
        }
        
        public static CTItem parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTItem)getTypeLoader().parse(reader, CTItem.type, xmlOptions);
        }
        
        public static CTItem parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTItem)getTypeLoader().parse(xmlStreamReader, CTItem.type, (XmlOptions)null);
        }
        
        public static CTItem parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTItem)getTypeLoader().parse(xmlStreamReader, CTItem.type, xmlOptions);
        }
        
        public static CTItem parse(final Node node) throws XmlException {
            return (CTItem)getTypeLoader().parse(node, CTItem.type, (XmlOptions)null);
        }
        
        public static CTItem parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTItem)getTypeLoader().parse(node, CTItem.type, xmlOptions);
        }
        
        @Deprecated
        public static CTItem parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTItem)getTypeLoader().parse(xmlInputStream, CTItem.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTItem parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTItem)getTypeLoader().parse(xmlInputStream, CTItem.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTItem.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTItem.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
