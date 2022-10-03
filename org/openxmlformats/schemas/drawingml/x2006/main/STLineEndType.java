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

public interface STLineEndType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLineEndType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlineendtype8902type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum TRIANGLE = Enum.forString("triangle");
    public static final Enum STEALTH = Enum.forString("stealth");
    public static final Enum DIAMOND = Enum.forString("diamond");
    public static final Enum OVAL = Enum.forString("oval");
    public static final Enum ARROW = Enum.forString("arrow");
    public static final int INT_NONE = 1;
    public static final int INT_TRIANGLE = 2;
    public static final int INT_STEALTH = 3;
    public static final int INT_DIAMOND = 4;
    public static final int INT_OVAL = 5;
    public static final int INT_ARROW = 6;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STLineEndType newValue(final Object o) {
            return (STLineEndType)STLineEndType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLineEndType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLineEndType newInstance() {
            return (STLineEndType)getTypeLoader().newInstance(STLineEndType.type, (XmlOptions)null);
        }
        
        public static STLineEndType newInstance(final XmlOptions xmlOptions) {
            return (STLineEndType)getTypeLoader().newInstance(STLineEndType.type, xmlOptions);
        }
        
        public static STLineEndType parse(final String s) throws XmlException {
            return (STLineEndType)getTypeLoader().parse(s, STLineEndType.type, (XmlOptions)null);
        }
        
        public static STLineEndType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLineEndType)getTypeLoader().parse(s, STLineEndType.type, xmlOptions);
        }
        
        public static STLineEndType parse(final File file) throws XmlException, IOException {
            return (STLineEndType)getTypeLoader().parse(file, STLineEndType.type, (XmlOptions)null);
        }
        
        public static STLineEndType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineEndType)getTypeLoader().parse(file, STLineEndType.type, xmlOptions);
        }
        
        public static STLineEndType parse(final URL url) throws XmlException, IOException {
            return (STLineEndType)getTypeLoader().parse(url, STLineEndType.type, (XmlOptions)null);
        }
        
        public static STLineEndType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineEndType)getTypeLoader().parse(url, STLineEndType.type, xmlOptions);
        }
        
        public static STLineEndType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLineEndType)getTypeLoader().parse(inputStream, STLineEndType.type, (XmlOptions)null);
        }
        
        public static STLineEndType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineEndType)getTypeLoader().parse(inputStream, STLineEndType.type, xmlOptions);
        }
        
        public static STLineEndType parse(final Reader reader) throws XmlException, IOException {
            return (STLineEndType)getTypeLoader().parse(reader, STLineEndType.type, (XmlOptions)null);
        }
        
        public static STLineEndType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineEndType)getTypeLoader().parse(reader, STLineEndType.type, xmlOptions);
        }
        
        public static STLineEndType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLineEndType)getTypeLoader().parse(xmlStreamReader, STLineEndType.type, (XmlOptions)null);
        }
        
        public static STLineEndType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLineEndType)getTypeLoader().parse(xmlStreamReader, STLineEndType.type, xmlOptions);
        }
        
        public static STLineEndType parse(final Node node) throws XmlException {
            return (STLineEndType)getTypeLoader().parse(node, STLineEndType.type, (XmlOptions)null);
        }
        
        public static STLineEndType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLineEndType)getTypeLoader().parse(node, STLineEndType.type, xmlOptions);
        }
        
        @Deprecated
        public static STLineEndType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLineEndType)getTypeLoader().parse(xmlInputStream, STLineEndType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLineEndType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLineEndType)getTypeLoader().parse(xmlInputStream, STLineEndType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineEndType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineEndType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_TRIANGLE = 2;
        static final int INT_STEALTH = 3;
        static final int INT_DIAMOND = 4;
        static final int INT_OVAL = 5;
        static final int INT_ARROW = 6;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("triangle", 2), new Enum("stealth", 3), new Enum("diamond", 4), new Enum("oval", 5), new Enum("arrow", 6) });
        }
    }
}
