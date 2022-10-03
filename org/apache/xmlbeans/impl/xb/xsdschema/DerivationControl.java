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

public interface DerivationControl extends XmlNMTOKEN
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DerivationControl.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("derivationcontrola5dftype");
    public static final Enum SUBSTITUTION = Enum.forString("substitution");
    public static final Enum EXTENSION = Enum.forString("extension");
    public static final Enum RESTRICTION = Enum.forString("restriction");
    public static final Enum LIST = Enum.forString("list");
    public static final Enum UNION = Enum.forString("union");
    public static final int INT_SUBSTITUTION = 1;
    public static final int INT_EXTENSION = 2;
    public static final int INT_RESTRICTION = 3;
    public static final int INT_LIST = 4;
    public static final int INT_UNION = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SUBSTITUTION = 1;
        static final int INT_EXTENSION = 2;
        static final int INT_RESTRICTION = 3;
        static final int INT_LIST = 4;
        static final int INT_UNION = 5;
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
            table = new Table(new Enum[] { new Enum("substitution", 1), new Enum("extension", 2), new Enum("restriction", 3), new Enum("list", 4), new Enum("union", 5) });
        }
    }
    
    public static final class Factory
    {
        public static DerivationControl newValue(final Object obj) {
            return (DerivationControl)DerivationControl.type.newValue(obj);
        }
        
        public static DerivationControl newInstance() {
            return (DerivationControl)XmlBeans.getContextTypeLoader().newInstance(DerivationControl.type, null);
        }
        
        public static DerivationControl newInstance(final XmlOptions options) {
            return (DerivationControl)XmlBeans.getContextTypeLoader().newInstance(DerivationControl.type, options);
        }
        
        public static DerivationControl parse(final String xmlAsString) throws XmlException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(xmlAsString, DerivationControl.type, null);
        }
        
        public static DerivationControl parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(xmlAsString, DerivationControl.type, options);
        }
        
        public static DerivationControl parse(final File file) throws XmlException, IOException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(file, DerivationControl.type, null);
        }
        
        public static DerivationControl parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(file, DerivationControl.type, options);
        }
        
        public static DerivationControl parse(final URL u) throws XmlException, IOException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(u, DerivationControl.type, null);
        }
        
        public static DerivationControl parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(u, DerivationControl.type, options);
        }
        
        public static DerivationControl parse(final InputStream is) throws XmlException, IOException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(is, DerivationControl.type, null);
        }
        
        public static DerivationControl parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(is, DerivationControl.type, options);
        }
        
        public static DerivationControl parse(final Reader r) throws XmlException, IOException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(r, DerivationControl.type, null);
        }
        
        public static DerivationControl parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(r, DerivationControl.type, options);
        }
        
        public static DerivationControl parse(final XMLStreamReader sr) throws XmlException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(sr, DerivationControl.type, null);
        }
        
        public static DerivationControl parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(sr, DerivationControl.type, options);
        }
        
        public static DerivationControl parse(final Node node) throws XmlException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(node, DerivationControl.type, null);
        }
        
        public static DerivationControl parse(final Node node, final XmlOptions options) throws XmlException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(node, DerivationControl.type, options);
        }
        
        @Deprecated
        public static DerivationControl parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(xis, DerivationControl.type, null);
        }
        
        @Deprecated
        public static DerivationControl parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (DerivationControl)XmlBeans.getContextTypeLoader().parse(xis, DerivationControl.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DerivationControl.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DerivationControl.type, options);
        }
        
        private Factory() {
        }
    }
}
