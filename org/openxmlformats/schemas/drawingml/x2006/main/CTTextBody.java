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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTextBody extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextBody.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextbodya3catype");
    
    CTTextBodyProperties getBodyPr();
    
    void setBodyPr(final CTTextBodyProperties p0);
    
    CTTextBodyProperties addNewBodyPr();
    
    CTTextListStyle getLstStyle();
    
    boolean isSetLstStyle();
    
    void setLstStyle(final CTTextListStyle p0);
    
    CTTextListStyle addNewLstStyle();
    
    void unsetLstStyle();
    
    List<CTTextParagraph> getPList();
    
    @Deprecated
    CTTextParagraph[] getPArray();
    
    CTTextParagraph getPArray(final int p0);
    
    int sizeOfPArray();
    
    void setPArray(final CTTextParagraph[] p0);
    
    void setPArray(final int p0, final CTTextParagraph p1);
    
    CTTextParagraph insertNewP(final int p0);
    
    CTTextParagraph addNewP();
    
    void removeP(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextBody.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextBody newInstance() {
            return (CTTextBody)getTypeLoader().newInstance(CTTextBody.type, (XmlOptions)null);
        }
        
        public static CTTextBody newInstance(final XmlOptions xmlOptions) {
            return (CTTextBody)getTypeLoader().newInstance(CTTextBody.type, xmlOptions);
        }
        
        public static CTTextBody parse(final String s) throws XmlException {
            return (CTTextBody)getTypeLoader().parse(s, CTTextBody.type, (XmlOptions)null);
        }
        
        public static CTTextBody parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBody)getTypeLoader().parse(s, CTTextBody.type, xmlOptions);
        }
        
        public static CTTextBody parse(final File file) throws XmlException, IOException {
            return (CTTextBody)getTypeLoader().parse(file, CTTextBody.type, (XmlOptions)null);
        }
        
        public static CTTextBody parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBody)getTypeLoader().parse(file, CTTextBody.type, xmlOptions);
        }
        
        public static CTTextBody parse(final URL url) throws XmlException, IOException {
            return (CTTextBody)getTypeLoader().parse(url, CTTextBody.type, (XmlOptions)null);
        }
        
        public static CTTextBody parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBody)getTypeLoader().parse(url, CTTextBody.type, xmlOptions);
        }
        
        public static CTTextBody parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextBody)getTypeLoader().parse(inputStream, CTTextBody.type, (XmlOptions)null);
        }
        
        public static CTTextBody parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBody)getTypeLoader().parse(inputStream, CTTextBody.type, xmlOptions);
        }
        
        public static CTTextBody parse(final Reader reader) throws XmlException, IOException {
            return (CTTextBody)getTypeLoader().parse(reader, CTTextBody.type, (XmlOptions)null);
        }
        
        public static CTTextBody parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBody)getTypeLoader().parse(reader, CTTextBody.type, xmlOptions);
        }
        
        public static CTTextBody parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextBody)getTypeLoader().parse(xmlStreamReader, CTTextBody.type, (XmlOptions)null);
        }
        
        public static CTTextBody parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBody)getTypeLoader().parse(xmlStreamReader, CTTextBody.type, xmlOptions);
        }
        
        public static CTTextBody parse(final Node node) throws XmlException {
            return (CTTextBody)getTypeLoader().parse(node, CTTextBody.type, (XmlOptions)null);
        }
        
        public static CTTextBody parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBody)getTypeLoader().parse(node, CTTextBody.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextBody parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextBody)getTypeLoader().parse(xmlInputStream, CTTextBody.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextBody parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextBody)getTypeLoader().parse(xmlInputStream, CTTextBody.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBody.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBody.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
