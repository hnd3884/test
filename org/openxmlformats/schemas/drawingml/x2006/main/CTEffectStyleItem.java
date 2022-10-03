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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTEffectStyleItem extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEffectStyleItem.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cteffectstyleitem05c4type");
    
    CTEffectList getEffectLst();
    
    boolean isSetEffectLst();
    
    void setEffectLst(final CTEffectList p0);
    
    CTEffectList addNewEffectLst();
    
    void unsetEffectLst();
    
    CTEffectContainer getEffectDag();
    
    boolean isSetEffectDag();
    
    void setEffectDag(final CTEffectContainer p0);
    
    CTEffectContainer addNewEffectDag();
    
    void unsetEffectDag();
    
    CTScene3D getScene3D();
    
    boolean isSetScene3D();
    
    void setScene3D(final CTScene3D p0);
    
    CTScene3D addNewScene3D();
    
    void unsetScene3D();
    
    CTShape3D getSp3D();
    
    boolean isSetSp3D();
    
    void setSp3D(final CTShape3D p0);
    
    CTShape3D addNewSp3D();
    
    void unsetSp3D();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEffectStyleItem.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEffectStyleItem newInstance() {
            return (CTEffectStyleItem)getTypeLoader().newInstance(CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleItem newInstance(final XmlOptions xmlOptions) {
            return (CTEffectStyleItem)getTypeLoader().newInstance(CTEffectStyleItem.type, xmlOptions);
        }
        
        public static CTEffectStyleItem parse(final String s) throws XmlException {
            return (CTEffectStyleItem)getTypeLoader().parse(s, CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleItem parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectStyleItem)getTypeLoader().parse(s, CTEffectStyleItem.type, xmlOptions);
        }
        
        public static CTEffectStyleItem parse(final File file) throws XmlException, IOException {
            return (CTEffectStyleItem)getTypeLoader().parse(file, CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleItem parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectStyleItem)getTypeLoader().parse(file, CTEffectStyleItem.type, xmlOptions);
        }
        
        public static CTEffectStyleItem parse(final URL url) throws XmlException, IOException {
            return (CTEffectStyleItem)getTypeLoader().parse(url, CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleItem parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectStyleItem)getTypeLoader().parse(url, CTEffectStyleItem.type, xmlOptions);
        }
        
        public static CTEffectStyleItem parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEffectStyleItem)getTypeLoader().parse(inputStream, CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleItem parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectStyleItem)getTypeLoader().parse(inputStream, CTEffectStyleItem.type, xmlOptions);
        }
        
        public static CTEffectStyleItem parse(final Reader reader) throws XmlException, IOException {
            return (CTEffectStyleItem)getTypeLoader().parse(reader, CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleItem parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectStyleItem)getTypeLoader().parse(reader, CTEffectStyleItem.type, xmlOptions);
        }
        
        public static CTEffectStyleItem parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEffectStyleItem)getTypeLoader().parse(xmlStreamReader, CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleItem parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectStyleItem)getTypeLoader().parse(xmlStreamReader, CTEffectStyleItem.type, xmlOptions);
        }
        
        public static CTEffectStyleItem parse(final Node node) throws XmlException {
            return (CTEffectStyleItem)getTypeLoader().parse(node, CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleItem parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectStyleItem)getTypeLoader().parse(node, CTEffectStyleItem.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEffectStyleItem parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEffectStyleItem)getTypeLoader().parse(xmlInputStream, CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEffectStyleItem parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEffectStyleItem)getTypeLoader().parse(xmlInputStream, CTEffectStyleItem.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEffectStyleItem.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEffectStyleItem.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
