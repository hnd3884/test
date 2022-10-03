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

public interface CTLock extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLock.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlock6b8etype");
    
    STExt.Enum getExt();
    
    STExt xgetExt();
    
    boolean isSetExt();
    
    void setExt(final STExt.Enum p0);
    
    void xsetExt(final STExt p0);
    
    void unsetExt();
    
    STTrueFalse.Enum getPosition();
    
    STTrueFalse xgetPosition();
    
    boolean isSetPosition();
    
    void setPosition(final STTrueFalse.Enum p0);
    
    void xsetPosition(final STTrueFalse p0);
    
    void unsetPosition();
    
    STTrueFalse.Enum getSelection();
    
    STTrueFalse xgetSelection();
    
    boolean isSetSelection();
    
    void setSelection(final STTrueFalse.Enum p0);
    
    void xsetSelection(final STTrueFalse p0);
    
    void unsetSelection();
    
    STTrueFalse.Enum getGrouping();
    
    STTrueFalse xgetGrouping();
    
    boolean isSetGrouping();
    
    void setGrouping(final STTrueFalse.Enum p0);
    
    void xsetGrouping(final STTrueFalse p0);
    
    void unsetGrouping();
    
    STTrueFalse.Enum getUngrouping();
    
    STTrueFalse xgetUngrouping();
    
    boolean isSetUngrouping();
    
    void setUngrouping(final STTrueFalse.Enum p0);
    
    void xsetUngrouping(final STTrueFalse p0);
    
    void unsetUngrouping();
    
    STTrueFalse.Enum getRotation();
    
    STTrueFalse xgetRotation();
    
    boolean isSetRotation();
    
    void setRotation(final STTrueFalse.Enum p0);
    
    void xsetRotation(final STTrueFalse p0);
    
    void unsetRotation();
    
    STTrueFalse.Enum getCropping();
    
    STTrueFalse xgetCropping();
    
    boolean isSetCropping();
    
    void setCropping(final STTrueFalse.Enum p0);
    
    void xsetCropping(final STTrueFalse p0);
    
    void unsetCropping();
    
    STTrueFalse.Enum getVerticies();
    
    STTrueFalse xgetVerticies();
    
    boolean isSetVerticies();
    
    void setVerticies(final STTrueFalse.Enum p0);
    
    void xsetVerticies(final STTrueFalse p0);
    
    void unsetVerticies();
    
    STTrueFalse.Enum getAdjusthandles();
    
    STTrueFalse xgetAdjusthandles();
    
    boolean isSetAdjusthandles();
    
    void setAdjusthandles(final STTrueFalse.Enum p0);
    
    void xsetAdjusthandles(final STTrueFalse p0);
    
    void unsetAdjusthandles();
    
    STTrueFalse.Enum getText();
    
    STTrueFalse xgetText();
    
    boolean isSetText();
    
    void setText(final STTrueFalse.Enum p0);
    
    void xsetText(final STTrueFalse p0);
    
    void unsetText();
    
    STTrueFalse.Enum getAspectratio();
    
    STTrueFalse xgetAspectratio();
    
    boolean isSetAspectratio();
    
    void setAspectratio(final STTrueFalse.Enum p0);
    
    void xsetAspectratio(final STTrueFalse p0);
    
    void unsetAspectratio();
    
    STTrueFalse.Enum getShapetype();
    
    STTrueFalse xgetShapetype();
    
    boolean isSetShapetype();
    
    void setShapetype(final STTrueFalse.Enum p0);
    
    void xsetShapetype(final STTrueFalse p0);
    
    void unsetShapetype();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLock.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLock newInstance() {
            return (CTLock)getTypeLoader().newInstance(CTLock.type, (XmlOptions)null);
        }
        
        public static CTLock newInstance(final XmlOptions xmlOptions) {
            return (CTLock)getTypeLoader().newInstance(CTLock.type, xmlOptions);
        }
        
        public static CTLock parse(final String s) throws XmlException {
            return (CTLock)getTypeLoader().parse(s, CTLock.type, (XmlOptions)null);
        }
        
        public static CTLock parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLock)getTypeLoader().parse(s, CTLock.type, xmlOptions);
        }
        
        public static CTLock parse(final File file) throws XmlException, IOException {
            return (CTLock)getTypeLoader().parse(file, CTLock.type, (XmlOptions)null);
        }
        
        public static CTLock parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLock)getTypeLoader().parse(file, CTLock.type, xmlOptions);
        }
        
        public static CTLock parse(final URL url) throws XmlException, IOException {
            return (CTLock)getTypeLoader().parse(url, CTLock.type, (XmlOptions)null);
        }
        
        public static CTLock parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLock)getTypeLoader().parse(url, CTLock.type, xmlOptions);
        }
        
        public static CTLock parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLock)getTypeLoader().parse(inputStream, CTLock.type, (XmlOptions)null);
        }
        
        public static CTLock parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLock)getTypeLoader().parse(inputStream, CTLock.type, xmlOptions);
        }
        
        public static CTLock parse(final Reader reader) throws XmlException, IOException {
            return (CTLock)getTypeLoader().parse(reader, CTLock.type, (XmlOptions)null);
        }
        
        public static CTLock parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLock)getTypeLoader().parse(reader, CTLock.type, xmlOptions);
        }
        
        public static CTLock parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLock)getTypeLoader().parse(xmlStreamReader, CTLock.type, (XmlOptions)null);
        }
        
        public static CTLock parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLock)getTypeLoader().parse(xmlStreamReader, CTLock.type, xmlOptions);
        }
        
        public static CTLock parse(final Node node) throws XmlException {
            return (CTLock)getTypeLoader().parse(node, CTLock.type, (XmlOptions)null);
        }
        
        public static CTLock parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLock)getTypeLoader().parse(node, CTLock.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLock parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLock)getTypeLoader().parse(xmlInputStream, CTLock.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLock parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLock)getTypeLoader().parse(xmlInputStream, CTLock.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLock.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLock.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
