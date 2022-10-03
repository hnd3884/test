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

public interface STTextUnderlineType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextUnderlineType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextunderlinetype469atype");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum WORDS = Enum.forString("words");
    public static final Enum SNG = Enum.forString("sng");
    public static final Enum DBL = Enum.forString("dbl");
    public static final Enum HEAVY = Enum.forString("heavy");
    public static final Enum DOTTED = Enum.forString("dotted");
    public static final Enum DOTTED_HEAVY = Enum.forString("dottedHeavy");
    public static final Enum DASH = Enum.forString("dash");
    public static final Enum DASH_HEAVY = Enum.forString("dashHeavy");
    public static final Enum DASH_LONG = Enum.forString("dashLong");
    public static final Enum DASH_LONG_HEAVY = Enum.forString("dashLongHeavy");
    public static final Enum DOT_DASH = Enum.forString("dotDash");
    public static final Enum DOT_DASH_HEAVY = Enum.forString("dotDashHeavy");
    public static final Enum DOT_DOT_DASH = Enum.forString("dotDotDash");
    public static final Enum DOT_DOT_DASH_HEAVY = Enum.forString("dotDotDashHeavy");
    public static final Enum WAVY = Enum.forString("wavy");
    public static final Enum WAVY_HEAVY = Enum.forString("wavyHeavy");
    public static final Enum WAVY_DBL = Enum.forString("wavyDbl");
    public static final int INT_NONE = 1;
    public static final int INT_WORDS = 2;
    public static final int INT_SNG = 3;
    public static final int INT_DBL = 4;
    public static final int INT_HEAVY = 5;
    public static final int INT_DOTTED = 6;
    public static final int INT_DOTTED_HEAVY = 7;
    public static final int INT_DASH = 8;
    public static final int INT_DASH_HEAVY = 9;
    public static final int INT_DASH_LONG = 10;
    public static final int INT_DASH_LONG_HEAVY = 11;
    public static final int INT_DOT_DASH = 12;
    public static final int INT_DOT_DASH_HEAVY = 13;
    public static final int INT_DOT_DOT_DASH = 14;
    public static final int INT_DOT_DOT_DASH_HEAVY = 15;
    public static final int INT_WAVY = 16;
    public static final int INT_WAVY_HEAVY = 17;
    public static final int INT_WAVY_DBL = 18;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextUnderlineType newValue(final Object o) {
            return (STTextUnderlineType)STTextUnderlineType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextUnderlineType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextUnderlineType newInstance() {
            return (STTextUnderlineType)getTypeLoader().newInstance(STTextUnderlineType.type, (XmlOptions)null);
        }
        
        public static STTextUnderlineType newInstance(final XmlOptions xmlOptions) {
            return (STTextUnderlineType)getTypeLoader().newInstance(STTextUnderlineType.type, xmlOptions);
        }
        
        public static STTextUnderlineType parse(final String s) throws XmlException {
            return (STTextUnderlineType)getTypeLoader().parse(s, STTextUnderlineType.type, (XmlOptions)null);
        }
        
        public static STTextUnderlineType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextUnderlineType)getTypeLoader().parse(s, STTextUnderlineType.type, xmlOptions);
        }
        
        public static STTextUnderlineType parse(final File file) throws XmlException, IOException {
            return (STTextUnderlineType)getTypeLoader().parse(file, STTextUnderlineType.type, (XmlOptions)null);
        }
        
        public static STTextUnderlineType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextUnderlineType)getTypeLoader().parse(file, STTextUnderlineType.type, xmlOptions);
        }
        
        public static STTextUnderlineType parse(final URL url) throws XmlException, IOException {
            return (STTextUnderlineType)getTypeLoader().parse(url, STTextUnderlineType.type, (XmlOptions)null);
        }
        
        public static STTextUnderlineType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextUnderlineType)getTypeLoader().parse(url, STTextUnderlineType.type, xmlOptions);
        }
        
        public static STTextUnderlineType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextUnderlineType)getTypeLoader().parse(inputStream, STTextUnderlineType.type, (XmlOptions)null);
        }
        
        public static STTextUnderlineType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextUnderlineType)getTypeLoader().parse(inputStream, STTextUnderlineType.type, xmlOptions);
        }
        
        public static STTextUnderlineType parse(final Reader reader) throws XmlException, IOException {
            return (STTextUnderlineType)getTypeLoader().parse(reader, STTextUnderlineType.type, (XmlOptions)null);
        }
        
        public static STTextUnderlineType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextUnderlineType)getTypeLoader().parse(reader, STTextUnderlineType.type, xmlOptions);
        }
        
        public static STTextUnderlineType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextUnderlineType)getTypeLoader().parse(xmlStreamReader, STTextUnderlineType.type, (XmlOptions)null);
        }
        
        public static STTextUnderlineType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextUnderlineType)getTypeLoader().parse(xmlStreamReader, STTextUnderlineType.type, xmlOptions);
        }
        
        public static STTextUnderlineType parse(final Node node) throws XmlException {
            return (STTextUnderlineType)getTypeLoader().parse(node, STTextUnderlineType.type, (XmlOptions)null);
        }
        
        public static STTextUnderlineType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextUnderlineType)getTypeLoader().parse(node, STTextUnderlineType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextUnderlineType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextUnderlineType)getTypeLoader().parse(xmlInputStream, STTextUnderlineType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextUnderlineType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextUnderlineType)getTypeLoader().parse(xmlInputStream, STTextUnderlineType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextUnderlineType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextUnderlineType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_WORDS = 2;
        static final int INT_SNG = 3;
        static final int INT_DBL = 4;
        static final int INT_HEAVY = 5;
        static final int INT_DOTTED = 6;
        static final int INT_DOTTED_HEAVY = 7;
        static final int INT_DASH = 8;
        static final int INT_DASH_HEAVY = 9;
        static final int INT_DASH_LONG = 10;
        static final int INT_DASH_LONG_HEAVY = 11;
        static final int INT_DOT_DASH = 12;
        static final int INT_DOT_DASH_HEAVY = 13;
        static final int INT_DOT_DOT_DASH = 14;
        static final int INT_DOT_DOT_DASH_HEAVY = 15;
        static final int INT_WAVY = 16;
        static final int INT_WAVY_HEAVY = 17;
        static final int INT_WAVY_DBL = 18;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("words", 2), new Enum("sng", 3), new Enum("dbl", 4), new Enum("heavy", 5), new Enum("dotted", 6), new Enum("dottedHeavy", 7), new Enum("dash", 8), new Enum("dashHeavy", 9), new Enum("dashLong", 10), new Enum("dashLongHeavy", 11), new Enum("dotDash", 12), new Enum("dotDashHeavy", 13), new Enum("dotDotDash", 14), new Enum("dotDotDashHeavy", 15), new Enum("wavy", 16), new Enum("wavyHeavy", 17), new Enum("wavyDbl", 18) });
        }
    }
}
