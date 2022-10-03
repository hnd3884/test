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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCalcCell extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCalcCell.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcalccellb960type");
    
    String getR();
    
    STCellRef xgetR();
    
    void setR(final String p0);
    
    void xsetR(final STCellRef p0);
    
    int getI();
    
    XmlInt xgetI();
    
    boolean isSetI();
    
    void setI(final int p0);
    
    void xsetI(final XmlInt p0);
    
    void unsetI();
    
    boolean getS();
    
    XmlBoolean xgetS();
    
    boolean isSetS();
    
    void setS(final boolean p0);
    
    void xsetS(final XmlBoolean p0);
    
    void unsetS();
    
    boolean getL();
    
    XmlBoolean xgetL();
    
    boolean isSetL();
    
    void setL(final boolean p0);
    
    void xsetL(final XmlBoolean p0);
    
    void unsetL();
    
    boolean getT();
    
    XmlBoolean xgetT();
    
    boolean isSetT();
    
    void setT(final boolean p0);
    
    void xsetT(final XmlBoolean p0);
    
    void unsetT();
    
    boolean getA();
    
    XmlBoolean xgetA();
    
    boolean isSetA();
    
    void setA(final boolean p0);
    
    void xsetA(final XmlBoolean p0);
    
    void unsetA();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCalcCell.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCalcCell newInstance() {
            return (CTCalcCell)getTypeLoader().newInstance(CTCalcCell.type, (XmlOptions)null);
        }
        
        public static CTCalcCell newInstance(final XmlOptions xmlOptions) {
            return (CTCalcCell)getTypeLoader().newInstance(CTCalcCell.type, xmlOptions);
        }
        
        public static CTCalcCell parse(final String s) throws XmlException {
            return (CTCalcCell)getTypeLoader().parse(s, CTCalcCell.type, (XmlOptions)null);
        }
        
        public static CTCalcCell parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCalcCell)getTypeLoader().parse(s, CTCalcCell.type, xmlOptions);
        }
        
        public static CTCalcCell parse(final File file) throws XmlException, IOException {
            return (CTCalcCell)getTypeLoader().parse(file, CTCalcCell.type, (XmlOptions)null);
        }
        
        public static CTCalcCell parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcCell)getTypeLoader().parse(file, CTCalcCell.type, xmlOptions);
        }
        
        public static CTCalcCell parse(final URL url) throws XmlException, IOException {
            return (CTCalcCell)getTypeLoader().parse(url, CTCalcCell.type, (XmlOptions)null);
        }
        
        public static CTCalcCell parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcCell)getTypeLoader().parse(url, CTCalcCell.type, xmlOptions);
        }
        
        public static CTCalcCell parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCalcCell)getTypeLoader().parse(inputStream, CTCalcCell.type, (XmlOptions)null);
        }
        
        public static CTCalcCell parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcCell)getTypeLoader().parse(inputStream, CTCalcCell.type, xmlOptions);
        }
        
        public static CTCalcCell parse(final Reader reader) throws XmlException, IOException {
            return (CTCalcCell)getTypeLoader().parse(reader, CTCalcCell.type, (XmlOptions)null);
        }
        
        public static CTCalcCell parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcCell)getTypeLoader().parse(reader, CTCalcCell.type, xmlOptions);
        }
        
        public static CTCalcCell parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCalcCell)getTypeLoader().parse(xmlStreamReader, CTCalcCell.type, (XmlOptions)null);
        }
        
        public static CTCalcCell parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCalcCell)getTypeLoader().parse(xmlStreamReader, CTCalcCell.type, xmlOptions);
        }
        
        public static CTCalcCell parse(final Node node) throws XmlException {
            return (CTCalcCell)getTypeLoader().parse(node, CTCalcCell.type, (XmlOptions)null);
        }
        
        public static CTCalcCell parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCalcCell)getTypeLoader().parse(node, CTCalcCell.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCalcCell parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCalcCell)getTypeLoader().parse(xmlInputStream, CTCalcCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCalcCell parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCalcCell)getTypeLoader().parse(xmlInputStream, CTCalcCell.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCalcCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCalcCell.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
