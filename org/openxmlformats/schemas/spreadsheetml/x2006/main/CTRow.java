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
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTRow extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRow.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrowdd39type");
    
    List<CTCell> getCList();
    
    @Deprecated
    CTCell[] getCArray();
    
    CTCell getCArray(final int p0);
    
    int sizeOfCArray();
    
    void setCArray(final CTCell[] p0);
    
    void setCArray(final int p0, final CTCell p1);
    
    CTCell insertNewC(final int p0);
    
    CTCell addNewC();
    
    void removeC(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getR();
    
    XmlUnsignedInt xgetR();
    
    boolean isSetR();
    
    void setR(final long p0);
    
    void xsetR(final XmlUnsignedInt p0);
    
    void unsetR();
    
    List getSpans();
    
    STCellSpans xgetSpans();
    
    boolean isSetSpans();
    
    void setSpans(final List p0);
    
    void xsetSpans(final STCellSpans p0);
    
    void unsetSpans();
    
    long getS();
    
    XmlUnsignedInt xgetS();
    
    boolean isSetS();
    
    void setS(final long p0);
    
    void xsetS(final XmlUnsignedInt p0);
    
    void unsetS();
    
    boolean getCustomFormat();
    
    XmlBoolean xgetCustomFormat();
    
    boolean isSetCustomFormat();
    
    void setCustomFormat(final boolean p0);
    
    void xsetCustomFormat(final XmlBoolean p0);
    
    void unsetCustomFormat();
    
    double getHt();
    
    XmlDouble xgetHt();
    
    boolean isSetHt();
    
    void setHt(final double p0);
    
    void xsetHt(final XmlDouble p0);
    
    void unsetHt();
    
    boolean getHidden();
    
    XmlBoolean xgetHidden();
    
    boolean isSetHidden();
    
    void setHidden(final boolean p0);
    
    void xsetHidden(final XmlBoolean p0);
    
    void unsetHidden();
    
    boolean getCustomHeight();
    
    XmlBoolean xgetCustomHeight();
    
    boolean isSetCustomHeight();
    
    void setCustomHeight(final boolean p0);
    
    void xsetCustomHeight(final XmlBoolean p0);
    
    void unsetCustomHeight();
    
    short getOutlineLevel();
    
    XmlUnsignedByte xgetOutlineLevel();
    
    boolean isSetOutlineLevel();
    
    void setOutlineLevel(final short p0);
    
    void xsetOutlineLevel(final XmlUnsignedByte p0);
    
    void unsetOutlineLevel();
    
    boolean getCollapsed();
    
    XmlBoolean xgetCollapsed();
    
    boolean isSetCollapsed();
    
    void setCollapsed(final boolean p0);
    
    void xsetCollapsed(final XmlBoolean p0);
    
    void unsetCollapsed();
    
    boolean getThickTop();
    
    XmlBoolean xgetThickTop();
    
    boolean isSetThickTop();
    
    void setThickTop(final boolean p0);
    
    void xsetThickTop(final XmlBoolean p0);
    
    void unsetThickTop();
    
    boolean getThickBot();
    
    XmlBoolean xgetThickBot();
    
    boolean isSetThickBot();
    
    void setThickBot(final boolean p0);
    
    void xsetThickBot(final XmlBoolean p0);
    
    void unsetThickBot();
    
    boolean getPh();
    
    XmlBoolean xgetPh();
    
    boolean isSetPh();
    
    void setPh(final boolean p0);
    
    void xsetPh(final XmlBoolean p0);
    
    void unsetPh();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRow.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRow newInstance() {
            return (CTRow)getTypeLoader().newInstance(CTRow.type, (XmlOptions)null);
        }
        
        public static CTRow newInstance(final XmlOptions xmlOptions) {
            return (CTRow)getTypeLoader().newInstance(CTRow.type, xmlOptions);
        }
        
        public static CTRow parse(final String s) throws XmlException {
            return (CTRow)getTypeLoader().parse(s, CTRow.type, (XmlOptions)null);
        }
        
        public static CTRow parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRow)getTypeLoader().parse(s, CTRow.type, xmlOptions);
        }
        
        public static CTRow parse(final File file) throws XmlException, IOException {
            return (CTRow)getTypeLoader().parse(file, CTRow.type, (XmlOptions)null);
        }
        
        public static CTRow parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRow)getTypeLoader().parse(file, CTRow.type, xmlOptions);
        }
        
        public static CTRow parse(final URL url) throws XmlException, IOException {
            return (CTRow)getTypeLoader().parse(url, CTRow.type, (XmlOptions)null);
        }
        
        public static CTRow parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRow)getTypeLoader().parse(url, CTRow.type, xmlOptions);
        }
        
        public static CTRow parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRow)getTypeLoader().parse(inputStream, CTRow.type, (XmlOptions)null);
        }
        
        public static CTRow parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRow)getTypeLoader().parse(inputStream, CTRow.type, xmlOptions);
        }
        
        public static CTRow parse(final Reader reader) throws XmlException, IOException {
            return (CTRow)getTypeLoader().parse(reader, CTRow.type, (XmlOptions)null);
        }
        
        public static CTRow parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRow)getTypeLoader().parse(reader, CTRow.type, xmlOptions);
        }
        
        public static CTRow parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRow)getTypeLoader().parse(xmlStreamReader, CTRow.type, (XmlOptions)null);
        }
        
        public static CTRow parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRow)getTypeLoader().parse(xmlStreamReader, CTRow.type, xmlOptions);
        }
        
        public static CTRow parse(final Node node) throws XmlException {
            return (CTRow)getTypeLoader().parse(node, CTRow.type, (XmlOptions)null);
        }
        
        public static CTRow parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRow)getTypeLoader().parse(node, CTRow.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRow parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRow)getTypeLoader().parse(xmlInputStream, CTRow.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRow parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRow)getTypeLoader().parse(xmlInputStream, CTRow.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRow.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRow.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
