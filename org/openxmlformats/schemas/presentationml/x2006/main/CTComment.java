package org.openxmlformats.schemas.presentationml.x2006.main;

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
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTComment extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTComment.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcomment2d10type");
    
    CTPoint2D getPos();
    
    void setPos(final CTPoint2D p0);
    
    CTPoint2D addNewPos();
    
    String getText();
    
    XmlString xgetText();
    
    void setText(final String p0);
    
    void xsetText(final XmlString p0);
    
    CTExtensionListModify getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionListModify p0);
    
    CTExtensionListModify addNewExtLst();
    
    void unsetExtLst();
    
    long getAuthorId();
    
    XmlUnsignedInt xgetAuthorId();
    
    void setAuthorId(final long p0);
    
    void xsetAuthorId(final XmlUnsignedInt p0);
    
    Calendar getDt();
    
    XmlDateTime xgetDt();
    
    boolean isSetDt();
    
    void setDt(final Calendar p0);
    
    void xsetDt(final XmlDateTime p0);
    
    void unsetDt();
    
    long getIdx();
    
    STIndex xgetIdx();
    
    void setIdx(final long p0);
    
    void xsetIdx(final STIndex p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTComment.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTComment newInstance() {
            return (CTComment)getTypeLoader().newInstance(CTComment.type, (XmlOptions)null);
        }
        
        public static CTComment newInstance(final XmlOptions xmlOptions) {
            return (CTComment)getTypeLoader().newInstance(CTComment.type, xmlOptions);
        }
        
        public static CTComment parse(final String s) throws XmlException {
            return (CTComment)getTypeLoader().parse(s, CTComment.type, (XmlOptions)null);
        }
        
        public static CTComment parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTComment)getTypeLoader().parse(s, CTComment.type, xmlOptions);
        }
        
        public static CTComment parse(final File file) throws XmlException, IOException {
            return (CTComment)getTypeLoader().parse(file, CTComment.type, (XmlOptions)null);
        }
        
        public static CTComment parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTComment)getTypeLoader().parse(file, CTComment.type, xmlOptions);
        }
        
        public static CTComment parse(final URL url) throws XmlException, IOException {
            return (CTComment)getTypeLoader().parse(url, CTComment.type, (XmlOptions)null);
        }
        
        public static CTComment parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTComment)getTypeLoader().parse(url, CTComment.type, xmlOptions);
        }
        
        public static CTComment parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTComment)getTypeLoader().parse(inputStream, CTComment.type, (XmlOptions)null);
        }
        
        public static CTComment parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTComment)getTypeLoader().parse(inputStream, CTComment.type, xmlOptions);
        }
        
        public static CTComment parse(final Reader reader) throws XmlException, IOException {
            return (CTComment)getTypeLoader().parse(reader, CTComment.type, (XmlOptions)null);
        }
        
        public static CTComment parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTComment)getTypeLoader().parse(reader, CTComment.type, xmlOptions);
        }
        
        public static CTComment parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTComment)getTypeLoader().parse(xmlStreamReader, CTComment.type, (XmlOptions)null);
        }
        
        public static CTComment parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTComment)getTypeLoader().parse(xmlStreamReader, CTComment.type, xmlOptions);
        }
        
        public static CTComment parse(final Node node) throws XmlException {
            return (CTComment)getTypeLoader().parse(node, CTComment.type, (XmlOptions)null);
        }
        
        public static CTComment parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTComment)getTypeLoader().parse(node, CTComment.type, xmlOptions);
        }
        
        @Deprecated
        public static CTComment parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTComment)getTypeLoader().parse(xmlInputStream, CTComment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTComment parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTComment)getTypeLoader().parse(xmlInputStream, CTComment.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTComment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTComment.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
