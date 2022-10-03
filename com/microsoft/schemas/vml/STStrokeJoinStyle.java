package com.microsoft.schemas.vml;

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

public interface STStrokeJoinStyle extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STStrokeJoinStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ststrokejoinstyle3c13type");
    public static final Enum ROUND = Enum.forString("round");
    public static final Enum BEVEL = Enum.forString("bevel");
    public static final Enum MITER = Enum.forString("miter");
    public static final int INT_ROUND = 1;
    public static final int INT_BEVEL = 2;
    public static final int INT_MITER = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STStrokeJoinStyle newValue(final Object o) {
            return (STStrokeJoinStyle)STStrokeJoinStyle.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STStrokeJoinStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STStrokeJoinStyle newInstance() {
            return (STStrokeJoinStyle)getTypeLoader().newInstance(STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        public static STStrokeJoinStyle newInstance(final XmlOptions xmlOptions) {
            return (STStrokeJoinStyle)getTypeLoader().newInstance(STStrokeJoinStyle.type, xmlOptions);
        }
        
        public static STStrokeJoinStyle parse(final String s) throws XmlException {
            return (STStrokeJoinStyle)getTypeLoader().parse(s, STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        public static STStrokeJoinStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STStrokeJoinStyle)getTypeLoader().parse(s, STStrokeJoinStyle.type, xmlOptions);
        }
        
        public static STStrokeJoinStyle parse(final File file) throws XmlException, IOException {
            return (STStrokeJoinStyle)getTypeLoader().parse(file, STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        public static STStrokeJoinStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStrokeJoinStyle)getTypeLoader().parse(file, STStrokeJoinStyle.type, xmlOptions);
        }
        
        public static STStrokeJoinStyle parse(final URL url) throws XmlException, IOException {
            return (STStrokeJoinStyle)getTypeLoader().parse(url, STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        public static STStrokeJoinStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStrokeJoinStyle)getTypeLoader().parse(url, STStrokeJoinStyle.type, xmlOptions);
        }
        
        public static STStrokeJoinStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (STStrokeJoinStyle)getTypeLoader().parse(inputStream, STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        public static STStrokeJoinStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStrokeJoinStyle)getTypeLoader().parse(inputStream, STStrokeJoinStyle.type, xmlOptions);
        }
        
        public static STStrokeJoinStyle parse(final Reader reader) throws XmlException, IOException {
            return (STStrokeJoinStyle)getTypeLoader().parse(reader, STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        public static STStrokeJoinStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStrokeJoinStyle)getTypeLoader().parse(reader, STStrokeJoinStyle.type, xmlOptions);
        }
        
        public static STStrokeJoinStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STStrokeJoinStyle)getTypeLoader().parse(xmlStreamReader, STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        public static STStrokeJoinStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STStrokeJoinStyle)getTypeLoader().parse(xmlStreamReader, STStrokeJoinStyle.type, xmlOptions);
        }
        
        public static STStrokeJoinStyle parse(final Node node) throws XmlException {
            return (STStrokeJoinStyle)getTypeLoader().parse(node, STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        public static STStrokeJoinStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STStrokeJoinStyle)getTypeLoader().parse(node, STStrokeJoinStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static STStrokeJoinStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STStrokeJoinStyle)getTypeLoader().parse(xmlInputStream, STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STStrokeJoinStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STStrokeJoinStyle)getTypeLoader().parse(xmlInputStream, STStrokeJoinStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STStrokeJoinStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STStrokeJoinStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_ROUND = 1;
        static final int INT_BEVEL = 2;
        static final int INT_MITER = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("round", 1), new Enum("bevel", 2), new Enum("miter", 3) });
        }
    }
}
