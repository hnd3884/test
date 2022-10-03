package org.openxmlformats.schemas.drawingml.x2006.main;

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
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlToken;

public interface STCompoundLine extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCompoundLine.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcompoundline712atype");
    public static final Enum SNG = Enum.forString("sng");
    public static final Enum DBL = Enum.forString("dbl");
    public static final Enum THICK_THIN = Enum.forString("thickThin");
    public static final Enum THIN_THICK = Enum.forString("thinThick");
    public static final Enum TRI = Enum.forString("tri");
    public static final int INT_SNG = 1;
    public static final int INT_DBL = 2;
    public static final int INT_THICK_THIN = 3;
    public static final int INT_THIN_THICK = 4;
    public static final int INT_TRI = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCompoundLine newValue(final Object o) {
            return (STCompoundLine)STCompoundLine.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCompoundLine.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCompoundLine newInstance() {
            return (STCompoundLine)getTypeLoader().newInstance(STCompoundLine.type, (XmlOptions)null);
        }
        
        public static STCompoundLine newInstance(final XmlOptions xmlOptions) {
            return (STCompoundLine)getTypeLoader().newInstance(STCompoundLine.type, xmlOptions);
        }
        
        public static STCompoundLine parse(final String s) throws XmlException {
            return (STCompoundLine)getTypeLoader().parse(s, STCompoundLine.type, (XmlOptions)null);
        }
        
        public static STCompoundLine parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCompoundLine)getTypeLoader().parse(s, STCompoundLine.type, xmlOptions);
        }
        
        public static STCompoundLine parse(final File file) throws XmlException, IOException {
            return (STCompoundLine)getTypeLoader().parse(file, STCompoundLine.type, (XmlOptions)null);
        }
        
        public static STCompoundLine parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCompoundLine)getTypeLoader().parse(file, STCompoundLine.type, xmlOptions);
        }
        
        public static STCompoundLine parse(final URL url) throws XmlException, IOException {
            return (STCompoundLine)getTypeLoader().parse(url, STCompoundLine.type, (XmlOptions)null);
        }
        
        public static STCompoundLine parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCompoundLine)getTypeLoader().parse(url, STCompoundLine.type, xmlOptions);
        }
        
        public static STCompoundLine parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCompoundLine)getTypeLoader().parse(inputStream, STCompoundLine.type, (XmlOptions)null);
        }
        
        public static STCompoundLine parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCompoundLine)getTypeLoader().parse(inputStream, STCompoundLine.type, xmlOptions);
        }
        
        public static STCompoundLine parse(final Reader reader) throws XmlException, IOException {
            return (STCompoundLine)getTypeLoader().parse(reader, STCompoundLine.type, (XmlOptions)null);
        }
        
        public static STCompoundLine parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCompoundLine)getTypeLoader().parse(reader, STCompoundLine.type, xmlOptions);
        }
        
        public static STCompoundLine parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCompoundLine)getTypeLoader().parse(xmlStreamReader, STCompoundLine.type, (XmlOptions)null);
        }
        
        public static STCompoundLine parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCompoundLine)getTypeLoader().parse(xmlStreamReader, STCompoundLine.type, xmlOptions);
        }
        
        public static STCompoundLine parse(final Node node) throws XmlException {
            return (STCompoundLine)getTypeLoader().parse(node, STCompoundLine.type, (XmlOptions)null);
        }
        
        public static STCompoundLine parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCompoundLine)getTypeLoader().parse(node, STCompoundLine.type, xmlOptions);
        }
        
        @Deprecated
        public static STCompoundLine parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCompoundLine)getTypeLoader().parse(xmlInputStream, STCompoundLine.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCompoundLine parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCompoundLine)getTypeLoader().parse(xmlInputStream, STCompoundLine.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCompoundLine.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCompoundLine.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SNG = 1;
        static final int INT_DBL = 2;
        static final int INT_THICK_THIN = 3;
        static final int INT_THIN_THICK = 4;
        static final int INT_TRI = 5;
        public static final StringEnumAbstractBase.Table table;
        private static final long serialVersionUID = 1L;
        
        public static Enum forString(final String s) {
            return (Enum)Enum.table.forString(s);
        }
        
        public static Enum forInt(final int n) {
            return (Enum)Enum.table.forInt(n);
        }
        
        private Enum(final String s, final int n) {
            super(s, n);
        }
        
        private Object readResolve() {
            return forInt(this.intValue());
        }
        
        static {
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("sng", 1), new Enum("dbl", 2), new Enum("thickThin", 3), new Enum("thinThick", 4), new Enum("tri", 5) });
        }
    }
}
