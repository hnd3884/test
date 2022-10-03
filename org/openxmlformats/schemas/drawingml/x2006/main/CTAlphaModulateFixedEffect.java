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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTAlphaModulateFixedEffect extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAlphaModulateFixedEffect.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctalphamodulatefixedeffect9769type");
    
    int getAmt();
    
    STPositivePercentage xgetAmt();
    
    boolean isSetAmt();
    
    void setAmt(final int p0);
    
    void xsetAmt(final STPositivePercentage p0);
    
    void unsetAmt();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAlphaModulateFixedEffect.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAlphaModulateFixedEffect newInstance() {
            return (CTAlphaModulateFixedEffect)getTypeLoader().newInstance(CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        public static CTAlphaModulateFixedEffect newInstance(final XmlOptions xmlOptions) {
            return (CTAlphaModulateFixedEffect)getTypeLoader().newInstance(CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        public static CTAlphaModulateFixedEffect parse(final String s) throws XmlException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(s, CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        public static CTAlphaModulateFixedEffect parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(s, CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        public static CTAlphaModulateFixedEffect parse(final File file) throws XmlException, IOException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(file, CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        public static CTAlphaModulateFixedEffect parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(file, CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        public static CTAlphaModulateFixedEffect parse(final URL url) throws XmlException, IOException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(url, CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        public static CTAlphaModulateFixedEffect parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(url, CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        public static CTAlphaModulateFixedEffect parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(inputStream, CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        public static CTAlphaModulateFixedEffect parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(inputStream, CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        public static CTAlphaModulateFixedEffect parse(final Reader reader) throws XmlException, IOException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(reader, CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        public static CTAlphaModulateFixedEffect parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(reader, CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        public static CTAlphaModulateFixedEffect parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(xmlStreamReader, CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        public static CTAlphaModulateFixedEffect parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(xmlStreamReader, CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        public static CTAlphaModulateFixedEffect parse(final Node node) throws XmlException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(node, CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        public static CTAlphaModulateFixedEffect parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(node, CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAlphaModulateFixedEffect parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(xmlInputStream, CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAlphaModulateFixedEffect parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAlphaModulateFixedEffect)getTypeLoader().parse(xmlInputStream, CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAlphaModulateFixedEffect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAlphaModulateFixedEffect.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
