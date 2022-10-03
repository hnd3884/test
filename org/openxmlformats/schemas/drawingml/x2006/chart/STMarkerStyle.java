package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface STMarkerStyle extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STMarkerStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stmarkerstyle177ftype");
    public static final Enum CIRCLE = Enum.forString("circle");
    public static final Enum DASH = Enum.forString("dash");
    public static final Enum DIAMOND = Enum.forString("diamond");
    public static final Enum DOT = Enum.forString("dot");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum PICTURE = Enum.forString("picture");
    public static final Enum PLUS = Enum.forString("plus");
    public static final Enum SQUARE = Enum.forString("square");
    public static final Enum STAR = Enum.forString("star");
    public static final Enum TRIANGLE = Enum.forString("triangle");
    public static final Enum X = Enum.forString("x");
    public static final int INT_CIRCLE = 1;
    public static final int INT_DASH = 2;
    public static final int INT_DIAMOND = 3;
    public static final int INT_DOT = 4;
    public static final int INT_NONE = 5;
    public static final int INT_PICTURE = 6;
    public static final int INT_PLUS = 7;
    public static final int INT_SQUARE = 8;
    public static final int INT_STAR = 9;
    public static final int INT_TRIANGLE = 10;
    public static final int INT_X = 11;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STMarkerStyle newValue(final Object o) {
            return (STMarkerStyle)STMarkerStyle.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STMarkerStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STMarkerStyle newInstance() {
            return (STMarkerStyle)getTypeLoader().newInstance(STMarkerStyle.type, (XmlOptions)null);
        }
        
        public static STMarkerStyle newInstance(final XmlOptions xmlOptions) {
            return (STMarkerStyle)getTypeLoader().newInstance(STMarkerStyle.type, xmlOptions);
        }
        
        public static STMarkerStyle parse(final String s) throws XmlException {
            return (STMarkerStyle)getTypeLoader().parse(s, STMarkerStyle.type, (XmlOptions)null);
        }
        
        public static STMarkerStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STMarkerStyle)getTypeLoader().parse(s, STMarkerStyle.type, xmlOptions);
        }
        
        public static STMarkerStyle parse(final File file) throws XmlException, IOException {
            return (STMarkerStyle)getTypeLoader().parse(file, STMarkerStyle.type, (XmlOptions)null);
        }
        
        public static STMarkerStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STMarkerStyle)getTypeLoader().parse(file, STMarkerStyle.type, xmlOptions);
        }
        
        public static STMarkerStyle parse(final URL url) throws XmlException, IOException {
            return (STMarkerStyle)getTypeLoader().parse(url, STMarkerStyle.type, (XmlOptions)null);
        }
        
        public static STMarkerStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STMarkerStyle)getTypeLoader().parse(url, STMarkerStyle.type, xmlOptions);
        }
        
        public static STMarkerStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (STMarkerStyle)getTypeLoader().parse(inputStream, STMarkerStyle.type, (XmlOptions)null);
        }
        
        public static STMarkerStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STMarkerStyle)getTypeLoader().parse(inputStream, STMarkerStyle.type, xmlOptions);
        }
        
        public static STMarkerStyle parse(final Reader reader) throws XmlException, IOException {
            return (STMarkerStyle)getTypeLoader().parse(reader, STMarkerStyle.type, (XmlOptions)null);
        }
        
        public static STMarkerStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STMarkerStyle)getTypeLoader().parse(reader, STMarkerStyle.type, xmlOptions);
        }
        
        public static STMarkerStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STMarkerStyle)getTypeLoader().parse(xmlStreamReader, STMarkerStyle.type, (XmlOptions)null);
        }
        
        public static STMarkerStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STMarkerStyle)getTypeLoader().parse(xmlStreamReader, STMarkerStyle.type, xmlOptions);
        }
        
        public static STMarkerStyle parse(final Node node) throws XmlException {
            return (STMarkerStyle)getTypeLoader().parse(node, STMarkerStyle.type, (XmlOptions)null);
        }
        
        public static STMarkerStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STMarkerStyle)getTypeLoader().parse(node, STMarkerStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static STMarkerStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STMarkerStyle)getTypeLoader().parse(xmlInputStream, STMarkerStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STMarkerStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STMarkerStyle)getTypeLoader().parse(xmlInputStream, STMarkerStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STMarkerStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STMarkerStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_CIRCLE = 1;
        static final int INT_DASH = 2;
        static final int INT_DIAMOND = 3;
        static final int INT_DOT = 4;
        static final int INT_NONE = 5;
        static final int INT_PICTURE = 6;
        static final int INT_PLUS = 7;
        static final int INT_SQUARE = 8;
        static final int INT_STAR = 9;
        static final int INT_TRIANGLE = 10;
        static final int INT_X = 11;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("circle", 1), new Enum("dash", 2), new Enum("diamond", 3), new Enum("dot", 4), new Enum("none", 5), new Enum("picture", 6), new Enum("plus", 7), new Enum("square", 8), new Enum("star", 9), new Enum("triangle", 10), new Enum("x", 11) });
        }
    }
}
