package org.apache.xmlbeans.impl.xb.xmlconfig;

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
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlToken;

public interface Qnametargetenum extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Qnametargetenum.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("qnametargetenum9f8ftype");
    public static final Enum TYPE = Enum.forString("type");
    public static final Enum DOCUMENT_TYPE = Enum.forString("document-type");
    public static final Enum ACCESSOR_ELEMENT = Enum.forString("accessor-element");
    public static final Enum ACCESSOR_ATTRIBUTE = Enum.forString("accessor-attribute");
    public static final int INT_TYPE = 1;
    public static final int INT_DOCUMENT_TYPE = 2;
    public static final int INT_ACCESSOR_ELEMENT = 3;
    public static final int INT_ACCESSOR_ATTRIBUTE = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TYPE = 1;
        static final int INT_DOCUMENT_TYPE = 2;
        static final int INT_ACCESSOR_ELEMENT = 3;
        static final int INT_ACCESSOR_ATTRIBUTE = 4;
        public static final Table table;
        private static final long serialVersionUID = 1L;
        
        public static Enum forString(final String s) {
            return (Enum)Enum.table.forString(s);
        }
        
        public static Enum forInt(final int i) {
            return (Enum)Enum.table.forInt(i);
        }
        
        private Enum(final String s, final int i) {
            super(s, i);
        }
        
        private Object readResolve() {
            return forInt(this.intValue());
        }
        
        static {
            table = new Table(new Enum[] { new Enum("type", 1), new Enum("document-type", 2), new Enum("accessor-element", 3), new Enum("accessor-attribute", 4) });
        }
    }
    
    public static final class Factory
    {
        public static Qnametargetenum newValue(final Object obj) {
            return (Qnametargetenum)Qnametargetenum.type.newValue(obj);
        }
        
        public static Qnametargetenum newInstance() {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().newInstance(Qnametargetenum.type, null);
        }
        
        public static Qnametargetenum newInstance(final XmlOptions options) {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().newInstance(Qnametargetenum.type, options);
        }
        
        public static Qnametargetenum parse(final String xmlAsString) throws XmlException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(xmlAsString, Qnametargetenum.type, null);
        }
        
        public static Qnametargetenum parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(xmlAsString, Qnametargetenum.type, options);
        }
        
        public static Qnametargetenum parse(final File file) throws XmlException, IOException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(file, Qnametargetenum.type, null);
        }
        
        public static Qnametargetenum parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(file, Qnametargetenum.type, options);
        }
        
        public static Qnametargetenum parse(final URL u) throws XmlException, IOException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(u, Qnametargetenum.type, null);
        }
        
        public static Qnametargetenum parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(u, Qnametargetenum.type, options);
        }
        
        public static Qnametargetenum parse(final InputStream is) throws XmlException, IOException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(is, Qnametargetenum.type, null);
        }
        
        public static Qnametargetenum parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(is, Qnametargetenum.type, options);
        }
        
        public static Qnametargetenum parse(final Reader r) throws XmlException, IOException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(r, Qnametargetenum.type, null);
        }
        
        public static Qnametargetenum parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(r, Qnametargetenum.type, options);
        }
        
        public static Qnametargetenum parse(final XMLStreamReader sr) throws XmlException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(sr, Qnametargetenum.type, null);
        }
        
        public static Qnametargetenum parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(sr, Qnametargetenum.type, options);
        }
        
        public static Qnametargetenum parse(final Node node) throws XmlException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(node, Qnametargetenum.type, null);
        }
        
        public static Qnametargetenum parse(final Node node, final XmlOptions options) throws XmlException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(node, Qnametargetenum.type, options);
        }
        
        @Deprecated
        public static Qnametargetenum parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(xis, Qnametargetenum.type, null);
        }
        
        @Deprecated
        public static Qnametargetenum parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Qnametargetenum)XmlBeans.getContextTypeLoader().parse(xis, Qnametargetenum.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Qnametargetenum.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Qnametargetenum.type, options);
        }
        
        private Factory() {
        }
    }
}
