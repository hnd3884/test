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

public interface STDataValidationErrorStyle extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STDataValidationErrorStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stdatavalidationerrorstyleca85type");
    public static final Enum STOP = Enum.forString("stop");
    public static final Enum WARNING = Enum.forString("warning");
    public static final Enum INFORMATION = Enum.forString("information");
    public static final int INT_STOP = 1;
    public static final int INT_WARNING = 2;
    public static final int INT_INFORMATION = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STDataValidationErrorStyle newValue(final Object o) {
            return (STDataValidationErrorStyle)STDataValidationErrorStyle.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STDataValidationErrorStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STDataValidationErrorStyle newInstance() {
            return (STDataValidationErrorStyle)getTypeLoader().newInstance(STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        public static STDataValidationErrorStyle newInstance(final XmlOptions xmlOptions) {
            return (STDataValidationErrorStyle)getTypeLoader().newInstance(STDataValidationErrorStyle.type, xmlOptions);
        }
        
        public static STDataValidationErrorStyle parse(final String s) throws XmlException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(s, STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        public static STDataValidationErrorStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(s, STDataValidationErrorStyle.type, xmlOptions);
        }
        
        public static STDataValidationErrorStyle parse(final File file) throws XmlException, IOException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(file, STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        public static STDataValidationErrorStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(file, STDataValidationErrorStyle.type, xmlOptions);
        }
        
        public static STDataValidationErrorStyle parse(final URL url) throws XmlException, IOException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(url, STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        public static STDataValidationErrorStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(url, STDataValidationErrorStyle.type, xmlOptions);
        }
        
        public static STDataValidationErrorStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(inputStream, STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        public static STDataValidationErrorStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(inputStream, STDataValidationErrorStyle.type, xmlOptions);
        }
        
        public static STDataValidationErrorStyle parse(final Reader reader) throws XmlException, IOException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(reader, STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        public static STDataValidationErrorStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(reader, STDataValidationErrorStyle.type, xmlOptions);
        }
        
        public static STDataValidationErrorStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(xmlStreamReader, STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        public static STDataValidationErrorStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(xmlStreamReader, STDataValidationErrorStyle.type, xmlOptions);
        }
        
        public static STDataValidationErrorStyle parse(final Node node) throws XmlException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(node, STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        public static STDataValidationErrorStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(node, STDataValidationErrorStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static STDataValidationErrorStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(xmlInputStream, STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STDataValidationErrorStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STDataValidationErrorStyle)getTypeLoader().parse(xmlInputStream, STDataValidationErrorStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDataValidationErrorStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDataValidationErrorStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_STOP = 1;
        static final int INT_WARNING = 2;
        static final int INT_INFORMATION = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("stop", 1), new Enum("warning", 2), new Enum("information", 3) });
        }
    }
}
