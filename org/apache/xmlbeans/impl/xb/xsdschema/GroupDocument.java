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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface GroupDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(GroupDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("group6eb6doctype");
    
    NamedGroup getGroup();
    
    void setGroup(final NamedGroup p0);
    
    NamedGroup addNewGroup();
    
    public static final class Factory
    {
        public static GroupDocument newInstance() {
            return (GroupDocument)XmlBeans.getContextTypeLoader().newInstance(GroupDocument.type, null);
        }
        
        public static GroupDocument newInstance(final XmlOptions options) {
            return (GroupDocument)XmlBeans.getContextTypeLoader().newInstance(GroupDocument.type, options);
        }
        
        public static GroupDocument parse(final String xmlAsString) throws XmlException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, GroupDocument.type, null);
        }
        
        public static GroupDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, GroupDocument.type, options);
        }
        
        public static GroupDocument parse(final File file) throws XmlException, IOException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(file, GroupDocument.type, null);
        }
        
        public static GroupDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(file, GroupDocument.type, options);
        }
        
        public static GroupDocument parse(final URL u) throws XmlException, IOException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(u, GroupDocument.type, null);
        }
        
        public static GroupDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(u, GroupDocument.type, options);
        }
        
        public static GroupDocument parse(final InputStream is) throws XmlException, IOException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(is, GroupDocument.type, null);
        }
        
        public static GroupDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(is, GroupDocument.type, options);
        }
        
        public static GroupDocument parse(final Reader r) throws XmlException, IOException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(r, GroupDocument.type, null);
        }
        
        public static GroupDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(r, GroupDocument.type, options);
        }
        
        public static GroupDocument parse(final XMLStreamReader sr) throws XmlException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(sr, GroupDocument.type, null);
        }
        
        public static GroupDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(sr, GroupDocument.type, options);
        }
        
        public static GroupDocument parse(final Node node) throws XmlException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(node, GroupDocument.type, null);
        }
        
        public static GroupDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(node, GroupDocument.type, options);
        }
        
        @Deprecated
        public static GroupDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(xis, GroupDocument.type, null);
        }
        
        @Deprecated
        public static GroupDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (GroupDocument)XmlBeans.getContextTypeLoader().parse(xis, GroupDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, GroupDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, GroupDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
