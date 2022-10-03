package org.apache.xmlbeans.impl.xb.xsdschema;

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
import org.apache.xmlbeans.XmlNMTOKEN;

public interface FormChoice extends XmlNMTOKEN
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(FormChoice.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("formchoicef2aetype");
    public static final Enum QUALIFIED = Enum.forString("qualified");
    public static final Enum UNQUALIFIED = Enum.forString("unqualified");
    public static final int INT_QUALIFIED = 1;
    public static final int INT_UNQUALIFIED = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_QUALIFIED = 1;
        static final int INT_UNQUALIFIED = 2;
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
            table = new Table(new Enum[] { new Enum("qualified", 1), new Enum("unqualified", 2) });
        }
    }
    
    public static final class Factory
    {
        public static FormChoice newValue(final Object obj) {
            return (FormChoice)FormChoice.type.newValue(obj);
        }
        
        public static FormChoice newInstance() {
            return (FormChoice)XmlBeans.getContextTypeLoader().newInstance(FormChoice.type, null);
        }
        
        public static FormChoice newInstance(final XmlOptions options) {
            return (FormChoice)XmlBeans.getContextTypeLoader().newInstance(FormChoice.type, options);
        }
        
        public static FormChoice parse(final String xmlAsString) throws XmlException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(xmlAsString, FormChoice.type, null);
        }
        
        public static FormChoice parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(xmlAsString, FormChoice.type, options);
        }
        
        public static FormChoice parse(final File file) throws XmlException, IOException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(file, FormChoice.type, null);
        }
        
        public static FormChoice parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(file, FormChoice.type, options);
        }
        
        public static FormChoice parse(final URL u) throws XmlException, IOException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(u, FormChoice.type, null);
        }
        
        public static FormChoice parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(u, FormChoice.type, options);
        }
        
        public static FormChoice parse(final InputStream is) throws XmlException, IOException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(is, FormChoice.type, null);
        }
        
        public static FormChoice parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(is, FormChoice.type, options);
        }
        
        public static FormChoice parse(final Reader r) throws XmlException, IOException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(r, FormChoice.type, null);
        }
        
        public static FormChoice parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(r, FormChoice.type, options);
        }
        
        public static FormChoice parse(final XMLStreamReader sr) throws XmlException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(sr, FormChoice.type, null);
        }
        
        public static FormChoice parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(sr, FormChoice.type, options);
        }
        
        public static FormChoice parse(final Node node) throws XmlException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(node, FormChoice.type, null);
        }
        
        public static FormChoice parse(final Node node, final XmlOptions options) throws XmlException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(node, FormChoice.type, options);
        }
        
        @Deprecated
        public static FormChoice parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(xis, FormChoice.type, null);
        }
        
        @Deprecated
        public static FormChoice parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (FormChoice)XmlBeans.getContextTypeLoader().parse(xis, FormChoice.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, FormChoice.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, FormChoice.type, options);
        }
        
        private Factory() {
        }
    }
}
