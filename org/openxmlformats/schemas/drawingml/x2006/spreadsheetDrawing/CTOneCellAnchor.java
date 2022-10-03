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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTOneCellAnchor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOneCellAnchor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctonecellanchor0527type");
    
    CTMarker getFrom();
    
    void setFrom(final CTMarker p0);
    
    CTMarker addNewFrom();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOneCellAnchor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOneCellAnchor newInstance() {
            return (CTOneCellAnchor)getTypeLoader().newInstance(CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTOneCellAnchor newInstance(final XmlOptions xmlOptions) {
            return (CTOneCellAnchor)getTypeLoader().newInstance(CTOneCellAnchor.type, xmlOptions);
        }
        
        public static CTOneCellAnchor parse(final String s) throws XmlException {
            return (CTOneCellAnchor)getTypeLoader().parse(s, CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTOneCellAnchor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOneCellAnchor)getTypeLoader().parse(s, CTOneCellAnchor.type, xmlOptions);
        }
        
        public static CTOneCellAnchor parse(final File file) throws XmlException, IOException {
            return (CTOneCellAnchor)getTypeLoader().parse(file, CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTOneCellAnchor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOneCellAnchor)getTypeLoader().parse(file, CTOneCellAnchor.type, xmlOptions);
        }
        
        public static CTOneCellAnchor parse(final URL url) throws XmlException, IOException {
            return (CTOneCellAnchor)getTypeLoader().parse(url, CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTOneCellAnchor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOneCellAnchor)getTypeLoader().parse(url, CTOneCellAnchor.type, xmlOptions);
        }
        
        public static CTOneCellAnchor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOneCellAnchor)getTypeLoader().parse(inputStream, CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTOneCellAnchor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOneCellAnchor)getTypeLoader().parse(inputStream, CTOneCellAnchor.type, xmlOptions);
        }
        
        public static CTOneCellAnchor parse(final Reader reader) throws XmlException, IOException {
            return (CTOneCellAnchor)getTypeLoader().parse(reader, CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTOneCellAnchor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOneCellAnchor)getTypeLoader().parse(reader, CTOneCellAnchor.type, xmlOptions);
        }
        
        public static CTOneCellAnchor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOneCellAnchor)getTypeLoader().parse(xmlStreamReader, CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTOneCellAnchor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOneCellAnchor)getTypeLoader().parse(xmlStreamReader, CTOneCellAnchor.type, xmlOptions);
        }
        
        public static CTOneCellAnchor parse(final Node node) throws XmlException {
            return (CTOneCellAnchor)getTypeLoader().parse(node, CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTOneCellAnchor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOneCellAnchor)getTypeLoader().parse(node, CTOneCellAnchor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOneCellAnchor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOneCellAnchor)getTypeLoader().parse(xmlInputStream, CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOneCellAnchor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOneCellAnchor)getTypeLoader().parse(xmlInputStream, CTOneCellAnchor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOneCellAnchor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOneCellAnchor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
