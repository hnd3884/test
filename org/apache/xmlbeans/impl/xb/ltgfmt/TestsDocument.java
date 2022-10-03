package org.apache.xmlbeans.impl.xb.ltgfmt;

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

public interface TestsDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TestsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("tests5621doctype");
    
    Tests getTests();
    
    void setTests(final Tests p0);
    
    Tests addNewTests();
    
    public interface Tests extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Tests.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("tests9d6eelemtype");
        
        TestCase[] getTestArray();
        
        TestCase getTestArray(final int p0);
        
        int sizeOfTestArray();
        
        void setTestArray(final TestCase[] p0);
        
        void setTestArray(final int p0, final TestCase p1);
        
        TestCase insertNewTest(final int p0);
        
        TestCase addNewTest();
        
        void removeTest(final int p0);
        
        public static final class Factory
        {
            public static Tests newInstance() {
                return (Tests)XmlBeans.getContextTypeLoader().newInstance(Tests.type, null);
            }
            
            public static Tests newInstance(final XmlOptions options) {
                return (Tests)XmlBeans.getContextTypeLoader().newInstance(Tests.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static TestsDocument newInstance() {
            return (TestsDocument)XmlBeans.getContextTypeLoader().newInstance(TestsDocument.type, null);
        }
        
        public static TestsDocument newInstance(final XmlOptions options) {
            return (TestsDocument)XmlBeans.getContextTypeLoader().newInstance(TestsDocument.type, options);
        }
        
        public static TestsDocument parse(final String xmlAsString) throws XmlException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, TestsDocument.type, null);
        }
        
        public static TestsDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, TestsDocument.type, options);
        }
        
        public static TestsDocument parse(final File file) throws XmlException, IOException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(file, TestsDocument.type, null);
        }
        
        public static TestsDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(file, TestsDocument.type, options);
        }
        
        public static TestsDocument parse(final URL u) throws XmlException, IOException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(u, TestsDocument.type, null);
        }
        
        public static TestsDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(u, TestsDocument.type, options);
        }
        
        public static TestsDocument parse(final InputStream is) throws XmlException, IOException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(is, TestsDocument.type, null);
        }
        
        public static TestsDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(is, TestsDocument.type, options);
        }
        
        public static TestsDocument parse(final Reader r) throws XmlException, IOException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(r, TestsDocument.type, null);
        }
        
        public static TestsDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(r, TestsDocument.type, options);
        }
        
        public static TestsDocument parse(final XMLStreamReader sr) throws XmlException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(sr, TestsDocument.type, null);
        }
        
        public static TestsDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(sr, TestsDocument.type, options);
        }
        
        public static TestsDocument parse(final Node node) throws XmlException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(node, TestsDocument.type, null);
        }
        
        public static TestsDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(node, TestsDocument.type, options);
        }
        
        @Deprecated
        public static TestsDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(xis, TestsDocument.type, null);
        }
        
        @Deprecated
        public static TestsDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (TestsDocument)XmlBeans.getContextTypeLoader().parse(xis, TestsDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TestsDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TestsDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
