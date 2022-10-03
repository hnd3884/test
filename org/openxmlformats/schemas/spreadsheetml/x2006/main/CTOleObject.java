package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTOleObject extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOleObject.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctoleobjectd866type");
    
    String getProgId();
    
    XmlString xgetProgId();
    
    boolean isSetProgId();
    
    void setProgId(final String p0);
    
    void xsetProgId(final XmlString p0);
    
    void unsetProgId();
    
    STDvAspect.Enum getDvAspect();
    
    STDvAspect xgetDvAspect();
    
    boolean isSetDvAspect();
    
    void setDvAspect(final STDvAspect.Enum p0);
    
    void xsetDvAspect(final STDvAspect p0);
    
    void unsetDvAspect();
    
    String getLink();
    
    STXstring xgetLink();
    
    boolean isSetLink();
    
    void setLink(final String p0);
    
    void xsetLink(final STXstring p0);
    
    void unsetLink();
    
    STOleUpdate.Enum getOleUpdate();
    
    STOleUpdate xgetOleUpdate();
    
    boolean isSetOleUpdate();
    
    void setOleUpdate(final STOleUpdate.Enum p0);
    
    void xsetOleUpdate(final STOleUpdate p0);
    
    void unsetOleUpdate();
    
    boolean getAutoLoad();
    
    XmlBoolean xgetAutoLoad();
    
    boolean isSetAutoLoad();
    
    void setAutoLoad(final boolean p0);
    
    void xsetAutoLoad(final XmlBoolean p0);
    
    void unsetAutoLoad();
    
    long getShapeId();
    
    XmlUnsignedInt xgetShapeId();
    
    void setShapeId(final long p0);
    
    void xsetShapeId(final XmlUnsignedInt p0);
    
    String getId();
    
    STRelationshipId xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final STRelationshipId p0);
    
    void unsetId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOleObject.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOleObject newInstance() {
            return (CTOleObject)getTypeLoader().newInstance(CTOleObject.type, (XmlOptions)null);
        }
        
        public static CTOleObject newInstance(final XmlOptions xmlOptions) {
            return (CTOleObject)getTypeLoader().newInstance(CTOleObject.type, xmlOptions);
        }
        
        public static CTOleObject parse(final String s) throws XmlException {
            return (CTOleObject)getTypeLoader().parse(s, CTOleObject.type, (XmlOptions)null);
        }
        
        public static CTOleObject parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOleObject)getTypeLoader().parse(s, CTOleObject.type, xmlOptions);
        }
        
        public static CTOleObject parse(final File file) throws XmlException, IOException {
            return (CTOleObject)getTypeLoader().parse(file, CTOleObject.type, (XmlOptions)null);
        }
        
        public static CTOleObject parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOleObject)getTypeLoader().parse(file, CTOleObject.type, xmlOptions);
        }
        
        public static CTOleObject parse(final URL url) throws XmlException, IOException {
            return (CTOleObject)getTypeLoader().parse(url, CTOleObject.type, (XmlOptions)null);
        }
        
        public static CTOleObject parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOleObject)getTypeLoader().parse(url, CTOleObject.type, xmlOptions);
        }
        
        public static CTOleObject parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOleObject)getTypeLoader().parse(inputStream, CTOleObject.type, (XmlOptions)null);
        }
        
        public static CTOleObject parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOleObject)getTypeLoader().parse(inputStream, CTOleObject.type, xmlOptions);
        }
        
        public static CTOleObject parse(final Reader reader) throws XmlException, IOException {
            return (CTOleObject)getTypeLoader().parse(reader, CTOleObject.type, (XmlOptions)null);
        }
        
        public static CTOleObject parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOleObject)getTypeLoader().parse(reader, CTOleObject.type, xmlOptions);
        }
        
        public static CTOleObject parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOleObject)getTypeLoader().parse(xmlStreamReader, CTOleObject.type, (XmlOptions)null);
        }
        
        public static CTOleObject parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOleObject)getTypeLoader().parse(xmlStreamReader, CTOleObject.type, xmlOptions);
        }
        
        public static CTOleObject parse(final Node node) throws XmlException {
            return (CTOleObject)getTypeLoader().parse(node, CTOleObject.type, (XmlOptions)null);
        }
        
        public static CTOleObject parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOleObject)getTypeLoader().parse(node, CTOleObject.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOleObject parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOleObject)getTypeLoader().parse(xmlInputStream, CTOleObject.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOleObject parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOleObject)getTypeLoader().parse(xmlInputStream, CTOleObject.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOleObject.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOleObject.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
