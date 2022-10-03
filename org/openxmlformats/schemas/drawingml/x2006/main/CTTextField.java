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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTextField extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextField.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextfield187etype");
    
    CTTextCharacterProperties getRPr();
    
    boolean isSetRPr();
    
    void setRPr(final CTTextCharacterProperties p0);
    
    CTTextCharacterProperties addNewRPr();
    
    void unsetRPr();
    
    CTTextParagraphProperties getPPr();
    
    boolean isSetPPr();
    
    void setPPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewPPr();
    
    void unsetPPr();
    
    String getT();
    
    XmlString xgetT();
    
    boolean isSetT();
    
    void setT(final String p0);
    
    void xsetT(final XmlString p0);
    
    void unsetT();
    
    String getId();
    
    STGuid xgetId();
    
    void setId(final String p0);
    
    void xsetId(final STGuid p0);
    
    String getType();
    
    XmlString xgetType();
    
    boolean isSetType();
    
    void setType(final String p0);
    
    void xsetType(final XmlString p0);
    
    void unsetType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextField.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextField newInstance() {
            return (CTTextField)getTypeLoader().newInstance(CTTextField.type, (XmlOptions)null);
        }
        
        public static CTTextField newInstance(final XmlOptions xmlOptions) {
            return (CTTextField)getTypeLoader().newInstance(CTTextField.type, xmlOptions);
        }
        
        public static CTTextField parse(final String s) throws XmlException {
            return (CTTextField)getTypeLoader().parse(s, CTTextField.type, (XmlOptions)null);
        }
        
        public static CTTextField parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextField)getTypeLoader().parse(s, CTTextField.type, xmlOptions);
        }
        
        public static CTTextField parse(final File file) throws XmlException, IOException {
            return (CTTextField)getTypeLoader().parse(file, CTTextField.type, (XmlOptions)null);
        }
        
        public static CTTextField parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextField)getTypeLoader().parse(file, CTTextField.type, xmlOptions);
        }
        
        public static CTTextField parse(final URL url) throws XmlException, IOException {
            return (CTTextField)getTypeLoader().parse(url, CTTextField.type, (XmlOptions)null);
        }
        
        public static CTTextField parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextField)getTypeLoader().parse(url, CTTextField.type, xmlOptions);
        }
        
        public static CTTextField parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextField)getTypeLoader().parse(inputStream, CTTextField.type, (XmlOptions)null);
        }
        
        public static CTTextField parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextField)getTypeLoader().parse(inputStream, CTTextField.type, xmlOptions);
        }
        
        public static CTTextField parse(final Reader reader) throws XmlException, IOException {
            return (CTTextField)getTypeLoader().parse(reader, CTTextField.type, (XmlOptions)null);
        }
        
        public static CTTextField parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextField)getTypeLoader().parse(reader, CTTextField.type, xmlOptions);
        }
        
        public static CTTextField parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextField)getTypeLoader().parse(xmlStreamReader, CTTextField.type, (XmlOptions)null);
        }
        
        public static CTTextField parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextField)getTypeLoader().parse(xmlStreamReader, CTTextField.type, xmlOptions);
        }
        
        public static CTTextField parse(final Node node) throws XmlException {
            return (CTTextField)getTypeLoader().parse(node, CTTextField.type, (XmlOptions)null);
        }
        
        public static CTTextField parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextField)getTypeLoader().parse(node, CTTextField.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextField parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextField)getTypeLoader().parse(xmlInputStream, CTTextField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextField parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextField)getTypeLoader().parse(xmlInputStream, CTTextField.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextField.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
