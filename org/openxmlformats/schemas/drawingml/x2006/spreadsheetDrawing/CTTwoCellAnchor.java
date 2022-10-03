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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTwoCellAnchor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTwoCellAnchor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttwocellanchor1e8dtype");
    
    CTMarker getFrom();
    
    void setFrom(final CTMarker p0);
    
    CTMarker addNewFrom();
    
    CTMarker getTo();
    
    void setTo(final CTMarker p0);
    
    CTMarker addNewTo();
    
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
    
    STEditAs.Enum getEditAs();
    
    STEditAs xgetEditAs();
    
    boolean isSetEditAs();
    
    void setEditAs(final STEditAs.Enum p0);
    
    void xsetEditAs(final STEditAs p0);
    
    void unsetEditAs();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTwoCellAnchor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTwoCellAnchor newInstance() {
            return (CTTwoCellAnchor)getTypeLoader().newInstance(CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTTwoCellAnchor newInstance(final XmlOptions xmlOptions) {
            return (CTTwoCellAnchor)getTypeLoader().newInstance(CTTwoCellAnchor.type, xmlOptions);
        }
        
        public static CTTwoCellAnchor parse(final String s) throws XmlException {
            return (CTTwoCellAnchor)getTypeLoader().parse(s, CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTTwoCellAnchor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTwoCellAnchor)getTypeLoader().parse(s, CTTwoCellAnchor.type, xmlOptions);
        }
        
        public static CTTwoCellAnchor parse(final File file) throws XmlException, IOException {
            return (CTTwoCellAnchor)getTypeLoader().parse(file, CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTTwoCellAnchor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTwoCellAnchor)getTypeLoader().parse(file, CTTwoCellAnchor.type, xmlOptions);
        }
        
        public static CTTwoCellAnchor parse(final URL url) throws XmlException, IOException {
            return (CTTwoCellAnchor)getTypeLoader().parse(url, CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTTwoCellAnchor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTwoCellAnchor)getTypeLoader().parse(url, CTTwoCellAnchor.type, xmlOptions);
        }
        
        public static CTTwoCellAnchor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTwoCellAnchor)getTypeLoader().parse(inputStream, CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTTwoCellAnchor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTwoCellAnchor)getTypeLoader().parse(inputStream, CTTwoCellAnchor.type, xmlOptions);
        }
        
        public static CTTwoCellAnchor parse(final Reader reader) throws XmlException, IOException {
            return (CTTwoCellAnchor)getTypeLoader().parse(reader, CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTTwoCellAnchor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTwoCellAnchor)getTypeLoader().parse(reader, CTTwoCellAnchor.type, xmlOptions);
        }
        
        public static CTTwoCellAnchor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTwoCellAnchor)getTypeLoader().parse(xmlStreamReader, CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTTwoCellAnchor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTwoCellAnchor)getTypeLoader().parse(xmlStreamReader, CTTwoCellAnchor.type, xmlOptions);
        }
        
        public static CTTwoCellAnchor parse(final Node node) throws XmlException {
            return (CTTwoCellAnchor)getTypeLoader().parse(node, CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        public static CTTwoCellAnchor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTwoCellAnchor)getTypeLoader().parse(node, CTTwoCellAnchor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTwoCellAnchor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTwoCellAnchor)getTypeLoader().parse(xmlInputStream, CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTwoCellAnchor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTwoCellAnchor)getTypeLoader().parse(xmlInputStream, CTTwoCellAnchor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTwoCellAnchor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTwoCellAnchor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
