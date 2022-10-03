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

public interface STTextVerticalType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextVerticalType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextverticaltyped988type");
    public static final Enum HORZ = Enum.forString("horz");
    public static final Enum VERT = Enum.forString("vert");
    public static final Enum VERT_270 = Enum.forString("vert270");
    public static final Enum WORD_ART_VERT = Enum.forString("wordArtVert");
    public static final Enum EA_VERT = Enum.forString("eaVert");
    public static final Enum MONGOLIAN_VERT = Enum.forString("mongolianVert");
    public static final Enum WORD_ART_VERT_RTL = Enum.forString("wordArtVertRtl");
    public static final int INT_HORZ = 1;
    public static final int INT_VERT = 2;
    public static final int INT_VERT_270 = 3;
    public static final int INT_WORD_ART_VERT = 4;
    public static final int INT_EA_VERT = 5;
    public static final int INT_MONGOLIAN_VERT = 6;
    public static final int INT_WORD_ART_VERT_RTL = 7;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextVerticalType newValue(final Object o) {
            return (STTextVerticalType)STTextVerticalType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextVerticalType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextVerticalType newInstance() {
            return (STTextVerticalType)getTypeLoader().newInstance(STTextVerticalType.type, (XmlOptions)null);
        }
        
        public static STTextVerticalType newInstance(final XmlOptions xmlOptions) {
            return (STTextVerticalType)getTypeLoader().newInstance(STTextVerticalType.type, xmlOptions);
        }
        
        public static STTextVerticalType parse(final String s) throws XmlException {
            return (STTextVerticalType)getTypeLoader().parse(s, STTextVerticalType.type, (XmlOptions)null);
        }
        
        public static STTextVerticalType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextVerticalType)getTypeLoader().parse(s, STTextVerticalType.type, xmlOptions);
        }
        
        public static STTextVerticalType parse(final File file) throws XmlException, IOException {
            return (STTextVerticalType)getTypeLoader().parse(file, STTextVerticalType.type, (XmlOptions)null);
        }
        
        public static STTextVerticalType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextVerticalType)getTypeLoader().parse(file, STTextVerticalType.type, xmlOptions);
        }
        
        public static STTextVerticalType parse(final URL url) throws XmlException, IOException {
            return (STTextVerticalType)getTypeLoader().parse(url, STTextVerticalType.type, (XmlOptions)null);
        }
        
        public static STTextVerticalType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextVerticalType)getTypeLoader().parse(url, STTextVerticalType.type, xmlOptions);
        }
        
        public static STTextVerticalType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextVerticalType)getTypeLoader().parse(inputStream, STTextVerticalType.type, (XmlOptions)null);
        }
        
        public static STTextVerticalType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextVerticalType)getTypeLoader().parse(inputStream, STTextVerticalType.type, xmlOptions);
        }
        
        public static STTextVerticalType parse(final Reader reader) throws XmlException, IOException {
            return (STTextVerticalType)getTypeLoader().parse(reader, STTextVerticalType.type, (XmlOptions)null);
        }
        
        public static STTextVerticalType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextVerticalType)getTypeLoader().parse(reader, STTextVerticalType.type, xmlOptions);
        }
        
        public static STTextVerticalType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextVerticalType)getTypeLoader().parse(xmlStreamReader, STTextVerticalType.type, (XmlOptions)null);
        }
        
        public static STTextVerticalType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextVerticalType)getTypeLoader().parse(xmlStreamReader, STTextVerticalType.type, xmlOptions);
        }
        
        public static STTextVerticalType parse(final Node node) throws XmlException {
            return (STTextVerticalType)getTypeLoader().parse(node, STTextVerticalType.type, (XmlOptions)null);
        }
        
        public static STTextVerticalType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextVerticalType)getTypeLoader().parse(node, STTextVerticalType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextVerticalType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextVerticalType)getTypeLoader().parse(xmlInputStream, STTextVerticalType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextVerticalType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextVerticalType)getTypeLoader().parse(xmlInputStream, STTextVerticalType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextVerticalType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextVerticalType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_HORZ = 1;
        static final int INT_VERT = 2;
        static final int INT_VERT_270 = 3;
        static final int INT_WORD_ART_VERT = 4;
        static final int INT_EA_VERT = 5;
        static final int INT_MONGOLIAN_VERT = 6;
        static final int INT_WORD_ART_VERT_RTL = 7;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("horz", 1), new Enum("vert", 2), new Enum("vert270", 3), new Enum("wordArtVert", 4), new Enum("eaVert", 5), new Enum("mongolianVert", 6), new Enum("wordArtVertRtl", 7) });
        }
    }
}
