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
import org.apache.xmlbeans.XmlBoolean;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPath2D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPath2D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpath2d73d2type");
    
    List<CTPath2DClose> getCloseList();
    
    @Deprecated
    CTPath2DClose[] getCloseArray();
    
    CTPath2DClose getCloseArray(final int p0);
    
    int sizeOfCloseArray();
    
    void setCloseArray(final CTPath2DClose[] p0);
    
    void setCloseArray(final int p0, final CTPath2DClose p1);
    
    CTPath2DClose insertNewClose(final int p0);
    
    CTPath2DClose addNewClose();
    
    void removeClose(final int p0);
    
    List<CTPath2DMoveTo> getMoveToList();
    
    @Deprecated
    CTPath2DMoveTo[] getMoveToArray();
    
    CTPath2DMoveTo getMoveToArray(final int p0);
    
    int sizeOfMoveToArray();
    
    void setMoveToArray(final CTPath2DMoveTo[] p0);
    
    void setMoveToArray(final int p0, final CTPath2DMoveTo p1);
    
    CTPath2DMoveTo insertNewMoveTo(final int p0);
    
    CTPath2DMoveTo addNewMoveTo();
    
    void removeMoveTo(final int p0);
    
    List<CTPath2DLineTo> getLnToList();
    
    @Deprecated
    CTPath2DLineTo[] getLnToArray();
    
    CTPath2DLineTo getLnToArray(final int p0);
    
    int sizeOfLnToArray();
    
    void setLnToArray(final CTPath2DLineTo[] p0);
    
    void setLnToArray(final int p0, final CTPath2DLineTo p1);
    
    CTPath2DLineTo insertNewLnTo(final int p0);
    
    CTPath2DLineTo addNewLnTo();
    
    void removeLnTo(final int p0);
    
    List<CTPath2DArcTo> getArcToList();
    
    @Deprecated
    CTPath2DArcTo[] getArcToArray();
    
    CTPath2DArcTo getArcToArray(final int p0);
    
    int sizeOfArcToArray();
    
    void setArcToArray(final CTPath2DArcTo[] p0);
    
    void setArcToArray(final int p0, final CTPath2DArcTo p1);
    
    CTPath2DArcTo insertNewArcTo(final int p0);
    
    CTPath2DArcTo addNewArcTo();
    
    void removeArcTo(final int p0);
    
    List<CTPath2DQuadBezierTo> getQuadBezToList();
    
    @Deprecated
    CTPath2DQuadBezierTo[] getQuadBezToArray();
    
    CTPath2DQuadBezierTo getQuadBezToArray(final int p0);
    
    int sizeOfQuadBezToArray();
    
    void setQuadBezToArray(final CTPath2DQuadBezierTo[] p0);
    
    void setQuadBezToArray(final int p0, final CTPath2DQuadBezierTo p1);
    
    CTPath2DQuadBezierTo insertNewQuadBezTo(final int p0);
    
    CTPath2DQuadBezierTo addNewQuadBezTo();
    
    void removeQuadBezTo(final int p0);
    
    List<CTPath2DCubicBezierTo> getCubicBezToList();
    
    @Deprecated
    CTPath2DCubicBezierTo[] getCubicBezToArray();
    
    CTPath2DCubicBezierTo getCubicBezToArray(final int p0);
    
    int sizeOfCubicBezToArray();
    
    void setCubicBezToArray(final CTPath2DCubicBezierTo[] p0);
    
    void setCubicBezToArray(final int p0, final CTPath2DCubicBezierTo p1);
    
    CTPath2DCubicBezierTo insertNewCubicBezTo(final int p0);
    
    CTPath2DCubicBezierTo addNewCubicBezTo();
    
    void removeCubicBezTo(final int p0);
    
    long getW();
    
    STPositiveCoordinate xgetW();
    
    boolean isSetW();
    
    void setW(final long p0);
    
    void xsetW(final STPositiveCoordinate p0);
    
    void unsetW();
    
    long getH();
    
    STPositiveCoordinate xgetH();
    
    boolean isSetH();
    
    void setH(final long p0);
    
    void xsetH(final STPositiveCoordinate p0);
    
    void unsetH();
    
    STPathFillMode.Enum getFill();
    
    STPathFillMode xgetFill();
    
    boolean isSetFill();
    
    void setFill(final STPathFillMode.Enum p0);
    
    void xsetFill(final STPathFillMode p0);
    
    void unsetFill();
    
    boolean getStroke();
    
    XmlBoolean xgetStroke();
    
    boolean isSetStroke();
    
    void setStroke(final boolean p0);
    
    void xsetStroke(final XmlBoolean p0);
    
    void unsetStroke();
    
    boolean getExtrusionOk();
    
    XmlBoolean xgetExtrusionOk();
    
    boolean isSetExtrusionOk();
    
    void setExtrusionOk(final boolean p0);
    
    void xsetExtrusionOk(final XmlBoolean p0);
    
    void unsetExtrusionOk();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPath2D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPath2D newInstance() {
            return (CTPath2D)getTypeLoader().newInstance(CTPath2D.type, (XmlOptions)null);
        }
        
        public static CTPath2D newInstance(final XmlOptions xmlOptions) {
            return (CTPath2D)getTypeLoader().newInstance(CTPath2D.type, xmlOptions);
        }
        
        public static CTPath2D parse(final String s) throws XmlException {
            return (CTPath2D)getTypeLoader().parse(s, CTPath2D.type, (XmlOptions)null);
        }
        
        public static CTPath2D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2D)getTypeLoader().parse(s, CTPath2D.type, xmlOptions);
        }
        
        public static CTPath2D parse(final File file) throws XmlException, IOException {
            return (CTPath2D)getTypeLoader().parse(file, CTPath2D.type, (XmlOptions)null);
        }
        
        public static CTPath2D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2D)getTypeLoader().parse(file, CTPath2D.type, xmlOptions);
        }
        
        public static CTPath2D parse(final URL url) throws XmlException, IOException {
            return (CTPath2D)getTypeLoader().parse(url, CTPath2D.type, (XmlOptions)null);
        }
        
        public static CTPath2D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2D)getTypeLoader().parse(url, CTPath2D.type, xmlOptions);
        }
        
        public static CTPath2D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPath2D)getTypeLoader().parse(inputStream, CTPath2D.type, (XmlOptions)null);
        }
        
        public static CTPath2D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2D)getTypeLoader().parse(inputStream, CTPath2D.type, xmlOptions);
        }
        
        public static CTPath2D parse(final Reader reader) throws XmlException, IOException {
            return (CTPath2D)getTypeLoader().parse(reader, CTPath2D.type, (XmlOptions)null);
        }
        
        public static CTPath2D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2D)getTypeLoader().parse(reader, CTPath2D.type, xmlOptions);
        }
        
        public static CTPath2D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPath2D)getTypeLoader().parse(xmlStreamReader, CTPath2D.type, (XmlOptions)null);
        }
        
        public static CTPath2D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2D)getTypeLoader().parse(xmlStreamReader, CTPath2D.type, xmlOptions);
        }
        
        public static CTPath2D parse(final Node node) throws XmlException {
            return (CTPath2D)getTypeLoader().parse(node, CTPath2D.type, (XmlOptions)null);
        }
        
        public static CTPath2D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2D)getTypeLoader().parse(node, CTPath2D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPath2D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPath2D)getTypeLoader().parse(xmlInputStream, CTPath2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPath2D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPath2D)getTypeLoader().parse(xmlInputStream, CTPath2D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
