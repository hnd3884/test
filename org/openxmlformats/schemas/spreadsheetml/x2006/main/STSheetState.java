package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface STSheetState extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSheetState.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stsheetstate158btype");
    public static final Enum VISIBLE = Enum.forString("visible");
    public static final Enum HIDDEN = Enum.forString("hidden");
    public static final Enum VERY_HIDDEN = Enum.forString("veryHidden");
    public static final int INT_VISIBLE = 1;
    public static final int INT_HIDDEN = 2;
    public static final int INT_VERY_HIDDEN = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSheetState newValue(final Object o) {
            return (STSheetState)STSheetState.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSheetState.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSheetState newInstance() {
            return (STSheetState)getTypeLoader().newInstance(STSheetState.type, (XmlOptions)null);
        }
        
        public static STSheetState newInstance(final XmlOptions xmlOptions) {
            return (STSheetState)getTypeLoader().newInstance(STSheetState.type, xmlOptions);
        }
        
        public static STSheetState parse(final String s) throws XmlException {
            return (STSheetState)getTypeLoader().parse(s, STSheetState.type, (XmlOptions)null);
        }
        
        public static STSheetState parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSheetState)getTypeLoader().parse(s, STSheetState.type, xmlOptions);
        }
        
        public static STSheetState parse(final File file) throws XmlException, IOException {
            return (STSheetState)getTypeLoader().parse(file, STSheetState.type, (XmlOptions)null);
        }
        
        public static STSheetState parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSheetState)getTypeLoader().parse(file, STSheetState.type, xmlOptions);
        }
        
        public static STSheetState parse(final URL url) throws XmlException, IOException {
            return (STSheetState)getTypeLoader().parse(url, STSheetState.type, (XmlOptions)null);
        }
        
        public static STSheetState parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSheetState)getTypeLoader().parse(url, STSheetState.type, xmlOptions);
        }
        
        public static STSheetState parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSheetState)getTypeLoader().parse(inputStream, STSheetState.type, (XmlOptions)null);
        }
        
        public static STSheetState parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSheetState)getTypeLoader().parse(inputStream, STSheetState.type, xmlOptions);
        }
        
        public static STSheetState parse(final Reader reader) throws XmlException, IOException {
            return (STSheetState)getTypeLoader().parse(reader, STSheetState.type, (XmlOptions)null);
        }
        
        public static STSheetState parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSheetState)getTypeLoader().parse(reader, STSheetState.type, xmlOptions);
        }
        
        public static STSheetState parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSheetState)getTypeLoader().parse(xmlStreamReader, STSheetState.type, (XmlOptions)null);
        }
        
        public static STSheetState parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSheetState)getTypeLoader().parse(xmlStreamReader, STSheetState.type, xmlOptions);
        }
        
        public static STSheetState parse(final Node node) throws XmlException {
            return (STSheetState)getTypeLoader().parse(node, STSheetState.type, (XmlOptions)null);
        }
        
        public static STSheetState parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSheetState)getTypeLoader().parse(node, STSheetState.type, xmlOptions);
        }
        
        @Deprecated
        public static STSheetState parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSheetState)getTypeLoader().parse(xmlInputStream, STSheetState.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSheetState parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSheetState)getTypeLoader().parse(xmlInputStream, STSheetState.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSheetState.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSheetState.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_VISIBLE = 1;
        static final int INT_HIDDEN = 2;
        static final int INT_VERY_HIDDEN = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("visible", 1), new Enum("hidden", 2), new Enum("veryHidden", 3) });
        }
    }
}
