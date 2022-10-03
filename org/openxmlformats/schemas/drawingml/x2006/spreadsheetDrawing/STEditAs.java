package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

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

public interface STEditAs extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STEditAs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("steditasad40type");
    public static final Enum TWO_CELL = Enum.forString("twoCell");
    public static final Enum ONE_CELL = Enum.forString("oneCell");
    public static final Enum ABSOLUTE = Enum.forString("absolute");
    public static final int INT_TWO_CELL = 1;
    public static final int INT_ONE_CELL = 2;
    public static final int INT_ABSOLUTE = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STEditAs newValue(final Object o) {
            return (STEditAs)STEditAs.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STEditAs.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STEditAs newInstance() {
            return (STEditAs)getTypeLoader().newInstance(STEditAs.type, (XmlOptions)null);
        }
        
        public static STEditAs newInstance(final XmlOptions xmlOptions) {
            return (STEditAs)getTypeLoader().newInstance(STEditAs.type, xmlOptions);
        }
        
        public static STEditAs parse(final String s) throws XmlException {
            return (STEditAs)getTypeLoader().parse(s, STEditAs.type, (XmlOptions)null);
        }
        
        public static STEditAs parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STEditAs)getTypeLoader().parse(s, STEditAs.type, xmlOptions);
        }
        
        public static STEditAs parse(final File file) throws XmlException, IOException {
            return (STEditAs)getTypeLoader().parse(file, STEditAs.type, (XmlOptions)null);
        }
        
        public static STEditAs parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEditAs)getTypeLoader().parse(file, STEditAs.type, xmlOptions);
        }
        
        public static STEditAs parse(final URL url) throws XmlException, IOException {
            return (STEditAs)getTypeLoader().parse(url, STEditAs.type, (XmlOptions)null);
        }
        
        public static STEditAs parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEditAs)getTypeLoader().parse(url, STEditAs.type, xmlOptions);
        }
        
        public static STEditAs parse(final InputStream inputStream) throws XmlException, IOException {
            return (STEditAs)getTypeLoader().parse(inputStream, STEditAs.type, (XmlOptions)null);
        }
        
        public static STEditAs parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEditAs)getTypeLoader().parse(inputStream, STEditAs.type, xmlOptions);
        }
        
        public static STEditAs parse(final Reader reader) throws XmlException, IOException {
            return (STEditAs)getTypeLoader().parse(reader, STEditAs.type, (XmlOptions)null);
        }
        
        public static STEditAs parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEditAs)getTypeLoader().parse(reader, STEditAs.type, xmlOptions);
        }
        
        public static STEditAs parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STEditAs)getTypeLoader().parse(xmlStreamReader, STEditAs.type, (XmlOptions)null);
        }
        
        public static STEditAs parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STEditAs)getTypeLoader().parse(xmlStreamReader, STEditAs.type, xmlOptions);
        }
        
        public static STEditAs parse(final Node node) throws XmlException {
            return (STEditAs)getTypeLoader().parse(node, STEditAs.type, (XmlOptions)null);
        }
        
        public static STEditAs parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STEditAs)getTypeLoader().parse(node, STEditAs.type, xmlOptions);
        }
        
        @Deprecated
        public static STEditAs parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STEditAs)getTypeLoader().parse(xmlInputStream, STEditAs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STEditAs parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STEditAs)getTypeLoader().parse(xmlInputStream, STEditAs.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STEditAs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STEditAs.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TWO_CELL = 1;
        static final int INT_ONE_CELL = 2;
        static final int INT_ABSOLUTE = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("twoCell", 1), new Enum("oneCell", 2), new Enum("absolute", 3) });
        }
    }
}
