package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTAbsoluteAnchor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAbsoluteAnchor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctabsoluteanchore360type");
    
    CTPoint2D getPos();
    
    void setPos(final CTPoint2D p0);
    
    CTPoint2D addNewPos();
    
    CTPositiveSize2D getExt();
    
    void setExt(final CTPositiveSize2D p0);
    
    CTPositiveSize2D addNewExt();
    
    CTShape getSp();
    
    boolean isSetSp();
    
    void setSp(final CTShape p0);
    
    CTShape addNewSp();
    
    void unsetSp();
    
    CTGroupShape getGrpSp();
    
    boolean isSetGrpSp();
    
    void setGrpSp(final CTGroupShape p0);
    
    CTGroupShape addNewGrpSp();
    
    void unsetGrpSp();
    
    CTGraphicalObjectFrame getGraphicFrame();
    
    boolean isSetGraphicFrame();
    
    void setGraphicFrame(final CTGraphicalObjectFrame p0);
    
    CTGraphicalObjectFrame addNewGraphicFrame();
    
    void unsetGraphicFrame();
    
    CTConnector getCxnSp();
    
    boolean isSetCxnSp();
    
    void setCxnSp(final CTConnector p0);
    
    CTConnector addNewCxnSp();
    
    void unsetCxnSp();
    
    CTPicture getPic();
    
    boolean isSetPic();
    
    void setPic(final CTPicture p0);
    
    CTPicture addNewPic();
    
    void unsetPic();
    
    CTAnchorClientData getClientData();
    
    void setClientData(final CTAnchorClientData p0);
    
    CTAnchorClientData addNewClientData();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAbsoluteAnchor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAbsoluteAnchor newInstance() {
            return (CTAbsoluteAnchor)getTypeLoader().newInstance(CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        public static CTAbsoluteAnchor newInstance(final XmlOptions xmlOptions) {
            return (CTAbsoluteAnchor)getTypeLoader().newInstance(CTAbsoluteAnchor.type, xmlOptions);
        }
        
        public static CTAbsoluteAnchor parse(final String s) throws XmlException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(s, CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        public static CTAbsoluteAnchor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(s, CTAbsoluteAnchor.type, xmlOptions);
        }
        
        public static CTAbsoluteAnchor parse(final File file) throws XmlException, IOException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(file, CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        public static CTAbsoluteAnchor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(file, CTAbsoluteAnchor.type, xmlOptions);
        }
        
        public static CTAbsoluteAnchor parse(final URL url) throws XmlException, IOException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(url, CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        public static CTAbsoluteAnchor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(url, CTAbsoluteAnchor.type, xmlOptions);
        }
        
        public static CTAbsoluteAnchor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(inputStream, CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        public static CTAbsoluteAnchor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(inputStream, CTAbsoluteAnchor.type, xmlOptions);
        }
        
        public static CTAbsoluteAnchor parse(final Reader reader) throws XmlException, IOException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(reader, CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        public static CTAbsoluteAnchor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(reader, CTAbsoluteAnchor.type, xmlOptions);
        }
        
        public static CTAbsoluteAnchor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(xmlStreamReader, CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        public static CTAbsoluteAnchor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(xmlStreamReader, CTAbsoluteAnchor.type, xmlOptions);
        }
        
        public static CTAbsoluteAnchor parse(final Node node) throws XmlException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(node, CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        public static CTAbsoluteAnchor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(node, CTAbsoluteAnchor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAbsoluteAnchor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(xmlInputStream, CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAbsoluteAnchor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAbsoluteAnchor)getTypeLoader().parse(xmlInputStream, CTAbsoluteAnchor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAbsoluteAnchor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAbsoluteAnchor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
