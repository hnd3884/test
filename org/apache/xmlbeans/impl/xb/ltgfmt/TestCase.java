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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface TestCase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TestCase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("testcase939btype");
    
    String getDescription();
    
    XmlString xgetDescription();
    
    boolean isSetDescription();
    
    void setDescription(final String p0);
    
    void xsetDescription(final XmlString p0);
    
    void unsetDescription();
    
    Files getFiles();
    
    void setFiles(final Files p0);
    
    Files addNewFiles();
    
    String getId();
    
    XmlID xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlID p0);
    
    void unsetId();
    
    String getOrigin();
    
    XmlToken xgetOrigin();
    
    boolean isSetOrigin();
    
    void setOrigin(final String p0);
    
    void xsetOrigin(final XmlToken p0);
    
    void unsetOrigin();
    
    boolean getModified();
    
    XmlBoolean xgetModified();
    
    boolean isSetModified();
    
    void setModified(final boolean p0);
    
    void xsetModified(final XmlBoolean p0);
    
    void unsetModified();
    
    public interface Files extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Files.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("files7c3eelemtype");
        
        FileDesc[] getFileArray();
        
        FileDesc getFileArray(final int p0);
        
        int sizeOfFileArray();
        
        void setFileArray(final FileDesc[] p0);
        
        void setFileArray(final int p0, final FileDesc p1);
        
        FileDesc insertNewFile(final int p0);
        
        FileDesc addNewFile();
        
        void removeFile(final int p0);
        
        public static final class Factory
        {
            public static Files newInstance() {
                return (Files)XmlBeans.getContextTypeLoader().newInstance(Files.type, null);
            }
            
            public static Files newInstance(final XmlOptions options) {
                return (Files)XmlBeans.getContextTypeLoader().newInstance(Files.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static TestCase newInstance() {
            return (TestCase)XmlBeans.getContextTypeLoader().newInstance(TestCase.type, null);
        }
        
        public static TestCase newInstance(final XmlOptions options) {
            return (TestCase)XmlBeans.getContextTypeLoader().newInstance(TestCase.type, options);
        }
        
        public static TestCase parse(final String xmlAsString) throws XmlException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(xmlAsString, TestCase.type, null);
        }
        
        public static TestCase parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(xmlAsString, TestCase.type, options);
        }
        
        public static TestCase parse(final File file) throws XmlException, IOException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(file, TestCase.type, null);
        }
        
        public static TestCase parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(file, TestCase.type, options);
        }
        
        public static TestCase parse(final URL u) throws XmlException, IOException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(u, TestCase.type, null);
        }
        
        public static TestCase parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(u, TestCase.type, options);
        }
        
        public static TestCase parse(final InputStream is) throws XmlException, IOException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(is, TestCase.type, null);
        }
        
        public static TestCase parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(is, TestCase.type, options);
        }
        
        public static TestCase parse(final Reader r) throws XmlException, IOException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(r, TestCase.type, null);
        }
        
        public static TestCase parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(r, TestCase.type, options);
        }
        
        public static TestCase parse(final XMLStreamReader sr) throws XmlException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(sr, TestCase.type, null);
        }
        
        public static TestCase parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(sr, TestCase.type, options);
        }
        
        public static TestCase parse(final Node node) throws XmlException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(node, TestCase.type, null);
        }
        
        public static TestCase parse(final Node node, final XmlOptions options) throws XmlException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(node, TestCase.type, options);
        }
        
        @Deprecated
        public static TestCase parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(xis, TestCase.type, null);
        }
        
        @Deprecated
        public static TestCase parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (TestCase)XmlBeans.getContextTypeLoader().parse(xis, TestCase.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TestCase.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TestCase.type, options);
        }
        
        private Factory() {
        }
    }
}
