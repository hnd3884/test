package com.microsoft.schemas.compatibility;

import org.apache.xmlbeans.XmlString;
import java.util.List;
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

public interface AlternateContentDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AlternateContentDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("alternatecontentdd64doctype");
    
    AlternateContent getAlternateContent();
    
    void setAlternateContent(final AlternateContent p0);
    
    AlternateContent addNewAlternateContent();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(AlternateContentDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static AlternateContentDocument newInstance() {
            return (AlternateContentDocument)getTypeLoader().newInstance(AlternateContentDocument.type, (XmlOptions)null);
        }
        
        public static AlternateContentDocument newInstance(final XmlOptions xmlOptions) {
            return (AlternateContentDocument)getTypeLoader().newInstance(AlternateContentDocument.type, xmlOptions);
        }
        
        public static AlternateContentDocument parse(final String s) throws XmlException {
            return (AlternateContentDocument)getTypeLoader().parse(s, AlternateContentDocument.type, (XmlOptions)null);
        }
        
        public static AlternateContentDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (AlternateContentDocument)getTypeLoader().parse(s, AlternateContentDocument.type, xmlOptions);
        }
        
        public static AlternateContentDocument parse(final File file) throws XmlException, IOException {
            return (AlternateContentDocument)getTypeLoader().parse(file, AlternateContentDocument.type, (XmlOptions)null);
        }
        
        public static AlternateContentDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (AlternateContentDocument)getTypeLoader().parse(file, AlternateContentDocument.type, xmlOptions);
        }
        
        public static AlternateContentDocument parse(final URL url) throws XmlException, IOException {
            return (AlternateContentDocument)getTypeLoader().parse(url, AlternateContentDocument.type, (XmlOptions)null);
        }
        
        public static AlternateContentDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (AlternateContentDocument)getTypeLoader().parse(url, AlternateContentDocument.type, xmlOptions);
        }
        
        public static AlternateContentDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (AlternateContentDocument)getTypeLoader().parse(inputStream, AlternateContentDocument.type, (XmlOptions)null);
        }
        
        public static AlternateContentDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (AlternateContentDocument)getTypeLoader().parse(inputStream, AlternateContentDocument.type, xmlOptions);
        }
        
        public static AlternateContentDocument parse(final Reader reader) throws XmlException, IOException {
            return (AlternateContentDocument)getTypeLoader().parse(reader, AlternateContentDocument.type, (XmlOptions)null);
        }
        
        public static AlternateContentDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (AlternateContentDocument)getTypeLoader().parse(reader, AlternateContentDocument.type, xmlOptions);
        }
        
        public static AlternateContentDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (AlternateContentDocument)getTypeLoader().parse(xmlStreamReader, AlternateContentDocument.type, (XmlOptions)null);
        }
        
        public static AlternateContentDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (AlternateContentDocument)getTypeLoader().parse(xmlStreamReader, AlternateContentDocument.type, xmlOptions);
        }
        
        public static AlternateContentDocument parse(final Node node) throws XmlException {
            return (AlternateContentDocument)getTypeLoader().parse(node, AlternateContentDocument.type, (XmlOptions)null);
        }
        
        public static AlternateContentDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (AlternateContentDocument)getTypeLoader().parse(node, AlternateContentDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static AlternateContentDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (AlternateContentDocument)getTypeLoader().parse(xmlInputStream, AlternateContentDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static AlternateContentDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (AlternateContentDocument)getTypeLoader().parse(xmlInputStream, AlternateContentDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, AlternateContentDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, AlternateContentDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public interface AlternateContent extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AlternateContent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("alternatecontenta8a9elemtype");
        
        List<Choice> getChoiceList();
        
        @Deprecated
        Choice[] getChoiceArray();
        
        Choice getChoiceArray(final int p0);
        
        int sizeOfChoiceArray();
        
        void setChoiceArray(final Choice[] p0);
        
        void setChoiceArray(final int p0, final Choice p1);
        
        Choice insertNewChoice(final int p0);
        
        Choice addNewChoice();
        
        void removeChoice(final int p0);
        
        Fallback getFallback();
        
        boolean isSetFallback();
        
        void setFallback(final Fallback p0);
        
        Fallback addNewFallback();
        
        void unsetFallback();
        
        String getIgnorable();
        
        XmlString xgetIgnorable();
        
        boolean isSetIgnorable();
        
        void setIgnorable(final String p0);
        
        void xsetIgnorable(final XmlString p0);
        
        void unsetIgnorable();
        
        String getMustUnderstand();
        
        XmlString xgetMustUnderstand();
        
        boolean isSetMustUnderstand();
        
        void setMustUnderstand(final String p0);
        
        void xsetMustUnderstand(final XmlString p0);
        
        void unsetMustUnderstand();
        
        String getProcessContent();
        
        XmlString xgetProcessContent();
        
        boolean isSetProcessContent();
        
        void setProcessContent(final String p0);
        
        void xsetProcessContent(final XmlString p0);
        
        void unsetProcessContent();
        
        public static final class Factory
        {
            private static SoftReference<SchemaTypeLoader> typeLoader;
            
            private static synchronized SchemaTypeLoader getTypeLoader() {
                SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
                if (typeLoaderForClassLoader == null) {
                    typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(AlternateContent.class.getClassLoader());
                    Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
                }
                return typeLoaderForClassLoader;
            }
            
            public static AlternateContent newInstance() {
                return (AlternateContent)getTypeLoader().newInstance(AlternateContent.type, (XmlOptions)null);
            }
            
            public static AlternateContent newInstance(final XmlOptions xmlOptions) {
                return (AlternateContent)getTypeLoader().newInstance(AlternateContent.type, xmlOptions);
            }
            
            private Factory() {
            }
        }
        
        public interface Fallback extends XmlObject
        {
            public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Fallback.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("fallback4cc7elemtype");
            
            String getIgnorable();
            
            XmlString xgetIgnorable();
            
            boolean isSetIgnorable();
            
            void setIgnorable(final String p0);
            
            void xsetIgnorable(final XmlString p0);
            
            void unsetIgnorable();
            
            String getMustUnderstand();
            
            XmlString xgetMustUnderstand();
            
            boolean isSetMustUnderstand();
            
            void setMustUnderstand(final String p0);
            
            void xsetMustUnderstand(final XmlString p0);
            
            void unsetMustUnderstand();
            
            String getProcessContent();
            
            XmlString xgetProcessContent();
            
            boolean isSetProcessContent();
            
            void setProcessContent(final String p0);
            
            void xsetProcessContent(final XmlString p0);
            
            void unsetProcessContent();
            
            public static final class Factory
            {
                private static SoftReference<SchemaTypeLoader> typeLoader;
                
                private static synchronized SchemaTypeLoader getTypeLoader() {
                    SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
                    if (typeLoaderForClassLoader == null) {
                        typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(Fallback.class.getClassLoader());
                        Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
                    }
                    return typeLoaderForClassLoader;
                }
                
                public static Fallback newInstance() {
                    return (Fallback)getTypeLoader().newInstance(Fallback.type, (XmlOptions)null);
                }
                
                public static Fallback newInstance(final XmlOptions xmlOptions) {
                    return (Fallback)getTypeLoader().newInstance(Fallback.type, xmlOptions);
                }
                
                private Factory() {
                }
            }
        }
        
        public interface Choice extends XmlObject
        {
            public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Choice.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("choice69c6elemtype");
            
            String getRequires();
            
            XmlString xgetRequires();
            
            void setRequires(final String p0);
            
            void xsetRequires(final XmlString p0);
            
            String getIgnorable();
            
            XmlString xgetIgnorable();
            
            boolean isSetIgnorable();
            
            void setIgnorable(final String p0);
            
            void xsetIgnorable(final XmlString p0);
            
            void unsetIgnorable();
            
            String getMustUnderstand();
            
            XmlString xgetMustUnderstand();
            
            boolean isSetMustUnderstand();
            
            void setMustUnderstand(final String p0);
            
            void xsetMustUnderstand(final XmlString p0);
            
            void unsetMustUnderstand();
            
            String getProcessContent();
            
            XmlString xgetProcessContent();
            
            boolean isSetProcessContent();
            
            void setProcessContent(final String p0);
            
            void xsetProcessContent(final XmlString p0);
            
            void unsetProcessContent();
            
            public static final class Factory
            {
                private static SoftReference<SchemaTypeLoader> typeLoader;
                
                private static synchronized SchemaTypeLoader getTypeLoader() {
                    SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
                    if (typeLoaderForClassLoader == null) {
                        typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(Choice.class.getClassLoader());
                        Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
                    }
                    return typeLoaderForClassLoader;
                }
                
                public static Choice newInstance() {
                    return (Choice)getTypeLoader().newInstance(Choice.type, (XmlOptions)null);
                }
                
                public static Choice newInstance(final XmlOptions xmlOptions) {
                    return (Choice)getTypeLoader().newInstance(Choice.type, xmlOptions);
                }
                
                private Factory() {
                }
            }
        }
    }
}
