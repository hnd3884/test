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

public interface STTileFlipMode extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTileFlipMode.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttileflipmode2429type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum X = Enum.forString("x");
    public static final Enum Y = Enum.forString("y");
    public static final Enum XY = Enum.forString("xy");
    public static final int INT_NONE = 1;
    public static final int INT_X = 2;
    public static final int INT_Y = 3;
    public static final int INT_XY = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTileFlipMode newValue(final Object o) {
            return (STTileFlipMode)STTileFlipMode.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTileFlipMode.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTileFlipMode newInstance() {
            return (STTileFlipMode)getTypeLoader().newInstance(STTileFlipMode.type, (XmlOptions)null);
        }
        
        public static STTileFlipMode newInstance(final XmlOptions xmlOptions) {
            return (STTileFlipMode)getTypeLoader().newInstance(STTileFlipMode.type, xmlOptions);
        }
        
        public static STTileFlipMode parse(final String s) throws XmlException {
            return (STTileFlipMode)getTypeLoader().parse(s, STTileFlipMode.type, (XmlOptions)null);
        }
        
        public static STTileFlipMode parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTileFlipMode)getTypeLoader().parse(s, STTileFlipMode.type, xmlOptions);
        }
        
        public static STTileFlipMode parse(final File file) throws XmlException, IOException {
            return (STTileFlipMode)getTypeLoader().parse(file, STTileFlipMode.type, (XmlOptions)null);
        }
        
        public static STTileFlipMode parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTileFlipMode)getTypeLoader().parse(file, STTileFlipMode.type, xmlOptions);
        }
        
        public static STTileFlipMode parse(final URL url) throws XmlException, IOException {
            return (STTileFlipMode)getTypeLoader().parse(url, STTileFlipMode.type, (XmlOptions)null);
        }
        
        public static STTileFlipMode parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTileFlipMode)getTypeLoader().parse(url, STTileFlipMode.type, xmlOptions);
        }
        
        public static STTileFlipMode parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTileFlipMode)getTypeLoader().parse(inputStream, STTileFlipMode.type, (XmlOptions)null);
        }
        
        public static STTileFlipMode parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTileFlipMode)getTypeLoader().parse(inputStream, STTileFlipMode.type, xmlOptions);
        }
        
        public static STTileFlipMode parse(final Reader reader) throws XmlException, IOException {
            return (STTileFlipMode)getTypeLoader().parse(reader, STTileFlipMode.type, (XmlOptions)null);
        }
        
        public static STTileFlipMode parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTileFlipMode)getTypeLoader().parse(reader, STTileFlipMode.type, xmlOptions);
        }
        
        public static STTileFlipMode parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTileFlipMode)getTypeLoader().parse(xmlStreamReader, STTileFlipMode.type, (XmlOptions)null);
        }
        
        public static STTileFlipMode parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTileFlipMode)getTypeLoader().parse(xmlStreamReader, STTileFlipMode.type, xmlOptions);
        }
        
        public static STTileFlipMode parse(final Node node) throws XmlException {
            return (STTileFlipMode)getTypeLoader().parse(node, STTileFlipMode.type, (XmlOptions)null);
        }
        
        public static STTileFlipMode parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTileFlipMode)getTypeLoader().parse(node, STTileFlipMode.type, xmlOptions);
        }
        
        @Deprecated
        public static STTileFlipMode parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTileFlipMode)getTypeLoader().parse(xmlInputStream, STTileFlipMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTileFlipMode parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTileFlipMode)getTypeLoader().parse(xmlInputStream, STTileFlipMode.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTileFlipMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTileFlipMode.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_X = 2;
        static final int INT_Y = 3;
        static final int INT_XY = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("x", 2), new Enum("y", 3), new Enum("xy", 4) });
        }
    }
}
