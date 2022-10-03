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

public interface STPresetLineDashVal extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPresetLineDashVal.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpresetlinedashval159dtype");
    public static final Enum SOLID = Enum.forString("solid");
    public static final Enum DOT = Enum.forString("dot");
    public static final Enum DASH = Enum.forString("dash");
    public static final Enum LG_DASH = Enum.forString("lgDash");
    public static final Enum DASH_DOT = Enum.forString("dashDot");
    public static final Enum LG_DASH_DOT = Enum.forString("lgDashDot");
    public static final Enum LG_DASH_DOT_DOT = Enum.forString("lgDashDotDot");
    public static final Enum SYS_DASH = Enum.forString("sysDash");
    public static final Enum SYS_DOT = Enum.forString("sysDot");
    public static final Enum SYS_DASH_DOT = Enum.forString("sysDashDot");
    public static final Enum SYS_DASH_DOT_DOT = Enum.forString("sysDashDotDot");
    public static final int INT_SOLID = 1;
    public static final int INT_DOT = 2;
    public static final int INT_DASH = 3;
    public static final int INT_LG_DASH = 4;
    public static final int INT_DASH_DOT = 5;
    public static final int INT_LG_DASH_DOT = 6;
    public static final int INT_LG_DASH_DOT_DOT = 7;
    public static final int INT_SYS_DASH = 8;
    public static final int INT_SYS_DOT = 9;
    public static final int INT_SYS_DASH_DOT = 10;
    public static final int INT_SYS_DASH_DOT_DOT = 11;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPresetLineDashVal newValue(final Object o) {
            return (STPresetLineDashVal)STPresetLineDashVal.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPresetLineDashVal.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPresetLineDashVal newInstance() {
            return (STPresetLineDashVal)getTypeLoader().newInstance(STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        public static STPresetLineDashVal newInstance(final XmlOptions xmlOptions) {
            return (STPresetLineDashVal)getTypeLoader().newInstance(STPresetLineDashVal.type, xmlOptions);
        }
        
        public static STPresetLineDashVal parse(final String s) throws XmlException {
            return (STPresetLineDashVal)getTypeLoader().parse(s, STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        public static STPresetLineDashVal parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPresetLineDashVal)getTypeLoader().parse(s, STPresetLineDashVal.type, xmlOptions);
        }
        
        public static STPresetLineDashVal parse(final File file) throws XmlException, IOException {
            return (STPresetLineDashVal)getTypeLoader().parse(file, STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        public static STPresetLineDashVal parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPresetLineDashVal)getTypeLoader().parse(file, STPresetLineDashVal.type, xmlOptions);
        }
        
        public static STPresetLineDashVal parse(final URL url) throws XmlException, IOException {
            return (STPresetLineDashVal)getTypeLoader().parse(url, STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        public static STPresetLineDashVal parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPresetLineDashVal)getTypeLoader().parse(url, STPresetLineDashVal.type, xmlOptions);
        }
        
        public static STPresetLineDashVal parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPresetLineDashVal)getTypeLoader().parse(inputStream, STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        public static STPresetLineDashVal parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPresetLineDashVal)getTypeLoader().parse(inputStream, STPresetLineDashVal.type, xmlOptions);
        }
        
        public static STPresetLineDashVal parse(final Reader reader) throws XmlException, IOException {
            return (STPresetLineDashVal)getTypeLoader().parse(reader, STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        public static STPresetLineDashVal parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPresetLineDashVal)getTypeLoader().parse(reader, STPresetLineDashVal.type, xmlOptions);
        }
        
        public static STPresetLineDashVal parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPresetLineDashVal)getTypeLoader().parse(xmlStreamReader, STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        public static STPresetLineDashVal parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPresetLineDashVal)getTypeLoader().parse(xmlStreamReader, STPresetLineDashVal.type, xmlOptions);
        }
        
        public static STPresetLineDashVal parse(final Node node) throws XmlException {
            return (STPresetLineDashVal)getTypeLoader().parse(node, STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        public static STPresetLineDashVal parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPresetLineDashVal)getTypeLoader().parse(node, STPresetLineDashVal.type, xmlOptions);
        }
        
        @Deprecated
        public static STPresetLineDashVal parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPresetLineDashVal)getTypeLoader().parse(xmlInputStream, STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPresetLineDashVal parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPresetLineDashVal)getTypeLoader().parse(xmlInputStream, STPresetLineDashVal.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPresetLineDashVal.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPresetLineDashVal.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SOLID = 1;
        static final int INT_DOT = 2;
        static final int INT_DASH = 3;
        static final int INT_LG_DASH = 4;
        static final int INT_DASH_DOT = 5;
        static final int INT_LG_DASH_DOT = 6;
        static final int INT_LG_DASH_DOT_DOT = 7;
        static final int INT_SYS_DASH = 8;
        static final int INT_SYS_DOT = 9;
        static final int INT_SYS_DASH_DOT = 10;
        static final int INT_SYS_DASH_DOT_DOT = 11;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("solid", 1), new Enum("dot", 2), new Enum("dash", 3), new Enum("lgDash", 4), new Enum("dashDot", 5), new Enum("lgDashDot", 6), new Enum("lgDashDotDot", 7), new Enum("sysDash", 8), new Enum("sysDot", 9), new Enum("sysDashDot", 10), new Enum("sysDashDotDot", 11) });
        }
    }
}
