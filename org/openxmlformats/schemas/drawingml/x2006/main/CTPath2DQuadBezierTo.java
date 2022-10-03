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

public interface CTPath2DQuadBezierTo extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPath2DQuadBezierTo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpath2dquadbezierto3f53type");
    
    List<CTAdjPoint2D> getPtList();
    
    @Deprecated
    CTAdjPoint2D[] getPtArray();
    
    CTAdjPoint2D getPtArray(final int p0);
    
    int sizeOfPtArray();
    
    void setPtArray(final CTAdjPoint2D[] p0);
    
    void setPtArray(final int p0, final CTAdjPoint2D p1);
    
    CTAdjPoint2D insertNewPt(final int p0);
    
    CTAdjPoint2D addNewPt();
    
    void removePt(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPath2DQuadBezierTo.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPath2DQuadBezierTo newInstance() {
            return (CTPath2DQuadBezierTo)getTypeLoader().newInstance(CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DQuadBezierTo newInstance(final XmlOptions xmlOptions) {
            return (CTPath2DQuadBezierTo)getTypeLoader().newInstance(CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        public static CTPath2DQuadBezierTo parse(final String s) throws XmlException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(s, CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DQuadBezierTo parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(s, CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        public static CTPath2DQuadBezierTo parse(final File file) throws XmlException, IOException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(file, CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DQuadBezierTo parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(file, CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        public static CTPath2DQuadBezierTo parse(final URL url) throws XmlException, IOException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(url, CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DQuadBezierTo parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(url, CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        public static CTPath2DQuadBezierTo parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(inputStream, CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DQuadBezierTo parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(inputStream, CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        public static CTPath2DQuadBezierTo parse(final Reader reader) throws XmlException, IOException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(reader, CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DQuadBezierTo parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(reader, CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        public static CTPath2DQuadBezierTo parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(xmlStreamReader, CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DQuadBezierTo parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(xmlStreamReader, CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        public static CTPath2DQuadBezierTo parse(final Node node) throws XmlException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(node, CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DQuadBezierTo parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(node, CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPath2DQuadBezierTo parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(xmlInputStream, CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPath2DQuadBezierTo parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPath2DQuadBezierTo)getTypeLoader().parse(xmlInputStream, CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2DQuadBezierTo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2DQuadBezierTo.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
