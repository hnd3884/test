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
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTGroupShape extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGroupShape.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgroupshape5b43type");
    
    CTGroupShapeNonVisual getNvGrpSpPr();
    
    void setNvGrpSpPr(final CTGroupShapeNonVisual p0);
    
    CTGroupShapeNonVisual addNewNvGrpSpPr();
    
    CTGroupShapeProperties getGrpSpPr();
    
    void setGrpSpPr(final CTGroupShapeProperties p0);
    
    CTGroupShapeProperties addNewGrpSpPr();
    
    List<CTShape> getSpList();
    
    @Deprecated
    CTShape[] getSpArray();
    
    CTShape getSpArray(final int p0);
    
    int sizeOfSpArray();
    
    void setSpArray(final CTShape[] p0);
    
    void setSpArray(final int p0, final CTShape p1);
    
    CTShape insertNewSp(final int p0);
    
    CTShape addNewSp();
    
    void removeSp(final int p0);
    
    List<CTGroupShape> getGrpSpList();
    
    @Deprecated
    CTGroupShape[] getGrpSpArray();
    
    CTGroupShape getGrpSpArray(final int p0);
    
    int sizeOfGrpSpArray();
    
    void setGrpSpArray(final CTGroupShape[] p0);
    
    void setGrpSpArray(final int p0, final CTGroupShape p1);
    
    CTGroupShape insertNewGrpSp(final int p0);
    
    CTGroupShape addNewGrpSp();
    
    void removeGrpSp(final int p0);
    
    List<CTGraphicalObjectFrame> getGraphicFrameList();
    
    @Deprecated
    CTGraphicalObjectFrame[] getGraphicFrameArray();
    
    CTGraphicalObjectFrame getGraphicFrameArray(final int p0);
    
    int sizeOfGraphicFrameArray();
    
    void setGraphicFrameArray(final CTGraphicalObjectFrame[] p0);
    
    void setGraphicFrameArray(final int p0, final CTGraphicalObjectFrame p1);
    
    CTGraphicalObjectFrame insertNewGraphicFrame(final int p0);
    
    CTGraphicalObjectFrame addNewGraphicFrame();
    
    void removeGraphicFrame(final int p0);
    
    List<CTConnector> getCxnSpList();
    
    @Deprecated
    CTConnector[] getCxnSpArray();
    
    CTConnector getCxnSpArray(final int p0);
    
    int sizeOfCxnSpArray();
    
    void setCxnSpArray(final CTConnector[] p0);
    
    void setCxnSpArray(final int p0, final CTConnector p1);
    
    CTConnector insertNewCxnSp(final int p0);
    
    CTConnector addNewCxnSp();
    
    void removeCxnSp(final int p0);
    
    List<CTPicture> getPicList();
    
    @Deprecated
    CTPicture[] getPicArray();
    
    CTPicture getPicArray(final int p0);
    
    int sizeOfPicArray();
    
    void setPicArray(final CTPicture[] p0);
    
    void setPicArray(final int p0, final CTPicture p1);
    
    CTPicture insertNewPic(final int p0);
    
    CTPicture addNewPic();
    
    void removePic(final int p0);
    
    CTExtensionListModify getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionListModify p0);
    
    CTExtensionListModify addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGroupShape.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGroupShape newInstance() {
            return (CTGroupShape)getTypeLoader().newInstance(CTGroupShape.type, (XmlOptions)null);
        }
        
        public static CTGroupShape newInstance(final XmlOptions xmlOptions) {
            return (CTGroupShape)getTypeLoader().newInstance(CTGroupShape.type, xmlOptions);
        }
        
        public static CTGroupShape parse(final String s) throws XmlException {
            return (CTGroupShape)getTypeLoader().parse(s, CTGroupShape.type, (XmlOptions)null);
        }
        
        public static CTGroupShape parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupShape)getTypeLoader().parse(s, CTGroupShape.type, xmlOptions);
        }
        
        public static CTGroupShape parse(final File file) throws XmlException, IOException {
            return (CTGroupShape)getTypeLoader().parse(file, CTGroupShape.type, (XmlOptions)null);
        }
        
        public static CTGroupShape parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShape)getTypeLoader().parse(file, CTGroupShape.type, xmlOptions);
        }
        
        public static CTGroupShape parse(final URL url) throws XmlException, IOException {
            return (CTGroupShape)getTypeLoader().parse(url, CTGroupShape.type, (XmlOptions)null);
        }
        
        public static CTGroupShape parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShape)getTypeLoader().parse(url, CTGroupShape.type, xmlOptions);
        }
        
        public static CTGroupShape parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGroupShape)getTypeLoader().parse(inputStream, CTGroupShape.type, (XmlOptions)null);
        }
        
        public static CTGroupShape parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShape)getTypeLoader().parse(inputStream, CTGroupShape.type, xmlOptions);
        }
        
        public static CTGroupShape parse(final Reader reader) throws XmlException, IOException {
            return (CTGroupShape)getTypeLoader().parse(reader, CTGroupShape.type, (XmlOptions)null);
        }
        
        public static CTGroupShape parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShape)getTypeLoader().parse(reader, CTGroupShape.type, xmlOptions);
        }
        
        public static CTGroupShape parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGroupShape)getTypeLoader().parse(xmlStreamReader, CTGroupShape.type, (XmlOptions)null);
        }
        
        public static CTGroupShape parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupShape)getTypeLoader().parse(xmlStreamReader, CTGroupShape.type, xmlOptions);
        }
        
        public static CTGroupShape parse(final Node node) throws XmlException {
            return (CTGroupShape)getTypeLoader().parse(node, CTGroupShape.type, (XmlOptions)null);
        }
        
        public static CTGroupShape parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupShape)getTypeLoader().parse(node, CTGroupShape.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGroupShape parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGroupShape)getTypeLoader().parse(xmlInputStream, CTGroupShape.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGroupShape parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGroupShape)getTypeLoader().parse(xmlInputStream, CTGroupShape.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupShape.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupShape.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
