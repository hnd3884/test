package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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
import org.apache.xmlbeans.XmlString;

public interface STTblWidth extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTblWidth.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttblwidth3a30type");
    public static final Enum NIL = Enum.forString("nil");
    public static final Enum PCT = Enum.forString("pct");
    public static final Enum DXA = Enum.forString("dxa");
    public static final Enum AUTO = Enum.forString("auto");
    public static final int INT_NIL = 1;
    public static final int INT_PCT = 2;
    public static final int INT_DXA = 3;
    public static final int INT_AUTO = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTblWidth newValue(final Object o) {
            return (STTblWidth)STTblWidth.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTblWidth.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTblWidth newInstance() {
            return (STTblWidth)getTypeLoader().newInstance(STTblWidth.type, (XmlOptions)null);
        }
        
        public static STTblWidth newInstance(final XmlOptions xmlOptions) {
            return (STTblWidth)getTypeLoader().newInstance(STTblWidth.type, xmlOptions);
        }
        
        public static STTblWidth parse(final String s) throws XmlException {
            return (STTblWidth)getTypeLoader().parse(s, STTblWidth.type, (XmlOptions)null);
        }
        
        public static STTblWidth parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTblWidth)getTypeLoader().parse(s, STTblWidth.type, xmlOptions);
        }
        
        public static STTblWidth parse(final File file) throws XmlException, IOException {
            return (STTblWidth)getTypeLoader().parse(file, STTblWidth.type, (XmlOptions)null);
        }
        
        public static STTblWidth parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTblWidth)getTypeLoader().parse(file, STTblWidth.type, xmlOptions);
        }
        
        public static STTblWidth parse(final URL url) throws XmlException, IOException {
            return (STTblWidth)getTypeLoader().parse(url, STTblWidth.type, (XmlOptions)null);
        }
        
        public static STTblWidth parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTblWidth)getTypeLoader().parse(url, STTblWidth.type, xmlOptions);
        }
        
        public static STTblWidth parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTblWidth)getTypeLoader().parse(inputStream, STTblWidth.type, (XmlOptions)null);
        }
        
        public static STTblWidth parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTblWidth)getTypeLoader().parse(inputStream, STTblWidth.type, xmlOptions);
        }
        
        public static STTblWidth parse(final Reader reader) throws XmlException, IOException {
            return (STTblWidth)getTypeLoader().parse(reader, STTblWidth.type, (XmlOptions)null);
        }
        
        public static STTblWidth parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTblWidth)getTypeLoader().parse(reader, STTblWidth.type, xmlOptions);
        }
        
        public static STTblWidth parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTblWidth)getTypeLoader().parse(xmlStreamReader, STTblWidth.type, (XmlOptions)null);
        }
        
        public static STTblWidth parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTblWidth)getTypeLoader().parse(xmlStreamReader, STTblWidth.type, xmlOptions);
        }
        
        public static STTblWidth parse(final Node node) throws XmlException {
            return (STTblWidth)getTypeLoader().parse(node, STTblWidth.type, (XmlOptions)null);
        }
        
        public static STTblWidth parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTblWidth)getTypeLoader().parse(node, STTblWidth.type, xmlOptions);
        }
        
        @Deprecated
        public static STTblWidth parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTblWidth)getTypeLoader().parse(xmlInputStream, STTblWidth.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTblWidth parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTblWidth)getTypeLoader().parse(xmlInputStream, STTblWidth.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTblWidth.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTblWidth.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NIL = 1;
        static final int INT_PCT = 2;
        static final int INT_DXA = 3;
        static final int INT_AUTO = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("nil", 1), new Enum("pct", 2), new Enum("dxa", 3), new Enum("auto", 4) });
        }
    }
}
