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
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDrawing extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDrawing.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdrawing8d34type");
    
    List<CTAnchor> getAnchorList();
    
    @Deprecated
    CTAnchor[] getAnchorArray();
    
    CTAnchor getAnchorArray(final int p0);
    
    int sizeOfAnchorArray();
    
    void setAnchorArray(final CTAnchor[] p0);
    
    void setAnchorArray(final int p0, final CTAnchor p1);
    
    CTAnchor insertNewAnchor(final int p0);
    
    CTAnchor addNewAnchor();
    
    void removeAnchor(final int p0);
    
    List<CTInline> getInlineList();
    
    @Deprecated
    CTInline[] getInlineArray();
    
    CTInline getInlineArray(final int p0);
    
    int sizeOfInlineArray();
    
    void setInlineArray(final CTInline[] p0);
    
    void setInlineArray(final int p0, final CTInline p1);
    
    CTInline insertNewInline(final int p0);
    
    CTInline addNewInline();
    
    void removeInline(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDrawing.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDrawing newInstance() {
            return (CTDrawing)getTypeLoader().newInstance(CTDrawing.type, (XmlOptions)null);
        }
        
        public static CTDrawing newInstance(final XmlOptions xmlOptions) {
            return (CTDrawing)getTypeLoader().newInstance(CTDrawing.type, xmlOptions);
        }
        
        public static CTDrawing parse(final String s) throws XmlException {
            return (CTDrawing)getTypeLoader().parse(s, CTDrawing.type, (XmlOptions)null);
        }
        
        public static CTDrawing parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDrawing)getTypeLoader().parse(s, CTDrawing.type, xmlOptions);
        }
        
        public static CTDrawing parse(final File file) throws XmlException, IOException {
            return (CTDrawing)getTypeLoader().parse(file, CTDrawing.type, (XmlOptions)null);
        }
        
        public static CTDrawing parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDrawing)getTypeLoader().parse(file, CTDrawing.type, xmlOptions);
        }
        
        public static CTDrawing parse(final URL url) throws XmlException, IOException {
            return (CTDrawing)getTypeLoader().parse(url, CTDrawing.type, (XmlOptions)null);
        }
        
        public static CTDrawing parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDrawing)getTypeLoader().parse(url, CTDrawing.type, xmlOptions);
        }
        
        public static CTDrawing parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDrawing)getTypeLoader().parse(inputStream, CTDrawing.type, (XmlOptions)null);
        }
        
        public static CTDrawing parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDrawing)getTypeLoader().parse(inputStream, CTDrawing.type, xmlOptions);
        }
        
        public static CTDrawing parse(final Reader reader) throws XmlException, IOException {
            return (CTDrawing)getTypeLoader().parse(reader, CTDrawing.type, (XmlOptions)null);
        }
        
        public static CTDrawing parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDrawing)getTypeLoader().parse(reader, CTDrawing.type, xmlOptions);
        }
        
        public static CTDrawing parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDrawing)getTypeLoader().parse(xmlStreamReader, CTDrawing.type, (XmlOptions)null);
        }
        
        public static CTDrawing parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDrawing)getTypeLoader().parse(xmlStreamReader, CTDrawing.type, xmlOptions);
        }
        
        public static CTDrawing parse(final Node node) throws XmlException {
            return (CTDrawing)getTypeLoader().parse(node, CTDrawing.type, (XmlOptions)null);
        }
        
        public static CTDrawing parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDrawing)getTypeLoader().parse(node, CTDrawing.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDrawing parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDrawing)getTypeLoader().parse(xmlInputStream, CTDrawing.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDrawing parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDrawing)getTypeLoader().parse(xmlInputStream, CTDrawing.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDrawing.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDrawing.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
