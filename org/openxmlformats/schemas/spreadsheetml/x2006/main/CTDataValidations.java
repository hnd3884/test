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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDataValidations extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDataValidations.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdatavalidationse46ftype");
    
    List<CTDataValidation> getDataValidationList();
    
    @Deprecated
    CTDataValidation[] getDataValidationArray();
    
    CTDataValidation getDataValidationArray(final int p0);
    
    int sizeOfDataValidationArray();
    
    void setDataValidationArray(final CTDataValidation[] p0);
    
    void setDataValidationArray(final int p0, final CTDataValidation p1);
    
    CTDataValidation insertNewDataValidation(final int p0);
    
    CTDataValidation addNewDataValidation();
    
    void removeDataValidation(final int p0);
    
    boolean getDisablePrompts();
    
    XmlBoolean xgetDisablePrompts();
    
    boolean isSetDisablePrompts();
    
    void setDisablePrompts(final boolean p0);
    
    void xsetDisablePrompts(final XmlBoolean p0);
    
    void unsetDisablePrompts();
    
    long getXWindow();
    
    XmlUnsignedInt xgetXWindow();
    
    boolean isSetXWindow();
    
    void setXWindow(final long p0);
    
    void xsetXWindow(final XmlUnsignedInt p0);
    
    void unsetXWindow();
    
    long getYWindow();
    
    XmlUnsignedInt xgetYWindow();
    
    boolean isSetYWindow();
    
    void setYWindow(final long p0);
    
    void xsetYWindow(final XmlUnsignedInt p0);
    
    void unsetYWindow();
    
    long getCount();
    
    XmlUnsignedInt xgetCount();
    
    boolean isSetCount();
    
    void setCount(final long p0);
    
    void xsetCount(final XmlUnsignedInt p0);
    
    void unsetCount();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDataValidations.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDataValidations newInstance() {
            return (CTDataValidations)getTypeLoader().newInstance(CTDataValidations.type, (XmlOptions)null);
        }
        
        public static CTDataValidations newInstance(final XmlOptions xmlOptions) {
            return (CTDataValidations)getTypeLoader().newInstance(CTDataValidations.type, xmlOptions);
        }
        
        public static CTDataValidations parse(final String s) throws XmlException {
            return (CTDataValidations)getTypeLoader().parse(s, CTDataValidations.type, (XmlOptions)null);
        }
        
        public static CTDataValidations parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataValidations)getTypeLoader().parse(s, CTDataValidations.type, xmlOptions);
        }
        
        public static CTDataValidations parse(final File file) throws XmlException, IOException {
            return (CTDataValidations)getTypeLoader().parse(file, CTDataValidations.type, (XmlOptions)null);
        }
        
        public static CTDataValidations parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataValidations)getTypeLoader().parse(file, CTDataValidations.type, xmlOptions);
        }
        
        public static CTDataValidations parse(final URL url) throws XmlException, IOException {
            return (CTDataValidations)getTypeLoader().parse(url, CTDataValidations.type, (XmlOptions)null);
        }
        
        public static CTDataValidations parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataValidations)getTypeLoader().parse(url, CTDataValidations.type, xmlOptions);
        }
        
        public static CTDataValidations parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDataValidations)getTypeLoader().parse(inputStream, CTDataValidations.type, (XmlOptions)null);
        }
        
        public static CTDataValidations parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataValidations)getTypeLoader().parse(inputStream, CTDataValidations.type, xmlOptions);
        }
        
        public static CTDataValidations parse(final Reader reader) throws XmlException, IOException {
            return (CTDataValidations)getTypeLoader().parse(reader, CTDataValidations.type, (XmlOptions)null);
        }
        
        public static CTDataValidations parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataValidations)getTypeLoader().parse(reader, CTDataValidations.type, xmlOptions);
        }
        
        public static CTDataValidations parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDataValidations)getTypeLoader().parse(xmlStreamReader, CTDataValidations.type, (XmlOptions)null);
        }
        
        public static CTDataValidations parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataValidations)getTypeLoader().parse(xmlStreamReader, CTDataValidations.type, xmlOptions);
        }
        
        public static CTDataValidations parse(final Node node) throws XmlException {
            return (CTDataValidations)getTypeLoader().parse(node, CTDataValidations.type, (XmlOptions)null);
        }
        
        public static CTDataValidations parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataValidations)getTypeLoader().parse(node, CTDataValidations.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDataValidations parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDataValidations)getTypeLoader().parse(xmlInputStream, CTDataValidations.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDataValidations parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDataValidations)getTypeLoader().parse(xmlInputStream, CTDataValidations.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataValidations.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataValidations.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
