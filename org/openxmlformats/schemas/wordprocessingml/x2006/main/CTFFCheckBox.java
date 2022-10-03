package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTFFCheckBox extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFFCheckBox.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctffcheckboxf3a5type");
    
    CTHpsMeasure getSize();
    
    boolean isSetSize();
    
    void setSize(final CTHpsMeasure p0);
    
    CTHpsMeasure addNewSize();
    
    void unsetSize();
    
    CTOnOff getSizeAuto();
    
    boolean isSetSizeAuto();
    
    void setSizeAuto(final CTOnOff p0);
    
    CTOnOff addNewSizeAuto();
    
    void unsetSizeAuto();
    
    CTOnOff getDefault();
    
    boolean isSetDefault();
    
    void setDefault(final CTOnOff p0);
    
    CTOnOff addNewDefault();
    
    void unsetDefault();
    
    CTOnOff getChecked();
    
    boolean isSetChecked();
    
    void setChecked(final CTOnOff p0);
    
    CTOnOff addNewChecked();
    
    void unsetChecked();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFFCheckBox.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFFCheckBox newInstance() {
            return (CTFFCheckBox)getTypeLoader().newInstance(CTFFCheckBox.type, (XmlOptions)null);
        }
        
        public static CTFFCheckBox newInstance(final XmlOptions xmlOptions) {
            return (CTFFCheckBox)getTypeLoader().newInstance(CTFFCheckBox.type, xmlOptions);
        }
        
        public static CTFFCheckBox parse(final String s) throws XmlException {
            return (CTFFCheckBox)getTypeLoader().parse(s, CTFFCheckBox.type, (XmlOptions)null);
        }
        
        public static CTFFCheckBox parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFFCheckBox)getTypeLoader().parse(s, CTFFCheckBox.type, xmlOptions);
        }
        
        public static CTFFCheckBox parse(final File file) throws XmlException, IOException {
            return (CTFFCheckBox)getTypeLoader().parse(file, CTFFCheckBox.type, (XmlOptions)null);
        }
        
        public static CTFFCheckBox parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFFCheckBox)getTypeLoader().parse(file, CTFFCheckBox.type, xmlOptions);
        }
        
        public static CTFFCheckBox parse(final URL url) throws XmlException, IOException {
            return (CTFFCheckBox)getTypeLoader().parse(url, CTFFCheckBox.type, (XmlOptions)null);
        }
        
        public static CTFFCheckBox parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFFCheckBox)getTypeLoader().parse(url, CTFFCheckBox.type, xmlOptions);
        }
        
        public static CTFFCheckBox parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFFCheckBox)getTypeLoader().parse(inputStream, CTFFCheckBox.type, (XmlOptions)null);
        }
        
        public static CTFFCheckBox parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFFCheckBox)getTypeLoader().parse(inputStream, CTFFCheckBox.type, xmlOptions);
        }
        
        public static CTFFCheckBox parse(final Reader reader) throws XmlException, IOException {
            return (CTFFCheckBox)getTypeLoader().parse(reader, CTFFCheckBox.type, (XmlOptions)null);
        }
        
        public static CTFFCheckBox parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFFCheckBox)getTypeLoader().parse(reader, CTFFCheckBox.type, xmlOptions);
        }
        
        public static CTFFCheckBox parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFFCheckBox)getTypeLoader().parse(xmlStreamReader, CTFFCheckBox.type, (XmlOptions)null);
        }
        
        public static CTFFCheckBox parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFFCheckBox)getTypeLoader().parse(xmlStreamReader, CTFFCheckBox.type, xmlOptions);
        }
        
        public static CTFFCheckBox parse(final Node node) throws XmlException {
            return (CTFFCheckBox)getTypeLoader().parse(node, CTFFCheckBox.type, (XmlOptions)null);
        }
        
        public static CTFFCheckBox parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFFCheckBox)getTypeLoader().parse(node, CTFFCheckBox.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFFCheckBox parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFFCheckBox)getTypeLoader().parse(xmlInputStream, CTFFCheckBox.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFFCheckBox parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFFCheckBox)getTypeLoader().parse(xmlInputStream, CTFFCheckBox.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFFCheckBox.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFFCheckBox.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
