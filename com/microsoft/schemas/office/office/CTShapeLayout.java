package com.microsoft.schemas.office.office;

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
import com.microsoft.schemas.vml.STExt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTShapeLayout extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShapeLayout.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshapelayoutbda4type");
    
    CTIdMap getIdmap();
    
    boolean isSetIdmap();
    
    void setIdmap(final CTIdMap p0);
    
    CTIdMap addNewIdmap();
    
    void unsetIdmap();
    
    CTRegroupTable getRegrouptable();
    
    boolean isSetRegrouptable();
    
    void setRegrouptable(final CTRegroupTable p0);
    
    CTRegroupTable addNewRegrouptable();
    
    void unsetRegrouptable();
    
    CTRules getRules();
    
    boolean isSetRules();
    
    void setRules(final CTRules p0);
    
    CTRules addNewRules();
    
    void unsetRules();
    
    STExt.Enum getExt();
    
    STExt xgetExt();
    
    boolean isSetExt();
    
    void setExt(final STExt.Enum p0);
    
    void xsetExt(final STExt p0);
    
    void unsetExt();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShapeLayout.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShapeLayout newInstance() {
            return (CTShapeLayout)getTypeLoader().newInstance(CTShapeLayout.type, (XmlOptions)null);
        }
        
        public static CTShapeLayout newInstance(final XmlOptions xmlOptions) {
            return (CTShapeLayout)getTypeLoader().newInstance(CTShapeLayout.type, xmlOptions);
        }
        
        public static CTShapeLayout parse(final String s) throws XmlException {
            return (CTShapeLayout)getTypeLoader().parse(s, CTShapeLayout.type, (XmlOptions)null);
        }
        
        public static CTShapeLayout parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeLayout)getTypeLoader().parse(s, CTShapeLayout.type, xmlOptions);
        }
        
        public static CTShapeLayout parse(final File file) throws XmlException, IOException {
            return (CTShapeLayout)getTypeLoader().parse(file, CTShapeLayout.type, (XmlOptions)null);
        }
        
        public static CTShapeLayout parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeLayout)getTypeLoader().parse(file, CTShapeLayout.type, xmlOptions);
        }
        
        public static CTShapeLayout parse(final URL url) throws XmlException, IOException {
            return (CTShapeLayout)getTypeLoader().parse(url, CTShapeLayout.type, (XmlOptions)null);
        }
        
        public static CTShapeLayout parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeLayout)getTypeLoader().parse(url, CTShapeLayout.type, xmlOptions);
        }
        
        public static CTShapeLayout parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShapeLayout)getTypeLoader().parse(inputStream, CTShapeLayout.type, (XmlOptions)null);
        }
        
        public static CTShapeLayout parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeLayout)getTypeLoader().parse(inputStream, CTShapeLayout.type, xmlOptions);
        }
        
        public static CTShapeLayout parse(final Reader reader) throws XmlException, IOException {
            return (CTShapeLayout)getTypeLoader().parse(reader, CTShapeLayout.type, (XmlOptions)null);
        }
        
        public static CTShapeLayout parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeLayout)getTypeLoader().parse(reader, CTShapeLayout.type, xmlOptions);
        }
        
        public static CTShapeLayout parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShapeLayout)getTypeLoader().parse(xmlStreamReader, CTShapeLayout.type, (XmlOptions)null);
        }
        
        public static CTShapeLayout parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeLayout)getTypeLoader().parse(xmlStreamReader, CTShapeLayout.type, xmlOptions);
        }
        
        public static CTShapeLayout parse(final Node node) throws XmlException {
            return (CTShapeLayout)getTypeLoader().parse(node, CTShapeLayout.type, (XmlOptions)null);
        }
        
        public static CTShapeLayout parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeLayout)getTypeLoader().parse(node, CTShapeLayout.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShapeLayout parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShapeLayout)getTypeLoader().parse(xmlInputStream, CTShapeLayout.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShapeLayout parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShapeLayout)getTypeLoader().parse(xmlInputStream, CTShapeLayout.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapeLayout.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapeLayout.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
