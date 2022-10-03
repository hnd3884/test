package com.sun.xml.internal.ws.model;

import org.xml.sax.SAXException;
import javax.xml.validation.SchemaFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Result;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.Unmarshaller;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.ObjectFactory;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.validation.Schema;
import org.w3c.dom.Element;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.ExistingAnnotationsType;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaParam;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaMethod;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.lang.annotation.Annotation;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import java.util.Iterator;
import javax.xml.transform.Source;
import java.util.HashMap;
import java.io.File;
import java.util.Collection;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaWsdlMappingType;
import java.util.Map;

public class ExternalMetadataReader extends ReflectAnnotationReader
{
    private static final String NAMESPACE_WEBLOGIC_WSEE_DATABINDING = "http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding";
    private static final String NAMESPACE_JAXWS_RI_EXTERNAL_METADATA = "http://xmlns.oracle.com/webservices/jaxws-databinding";
    private Map<String, JavaWsdlMappingType> readers;
    
    public ExternalMetadataReader(final Collection<File> files, final Collection<String> resourcePaths, final ClassLoader classLoader, final boolean xsdValidation, final boolean disableXmlSecurity) {
        this.readers = new HashMap<String, JavaWsdlMappingType>();
        if (files != null) {
            for (final File file : files) {
                try {
                    final String namespace = Util.documentRootNamespace(this.newSource(file), disableXmlSecurity);
                    final JavaWsdlMappingType externalMapping = this.parseMetadata(xsdValidation, this.newSource(file), namespace, disableXmlSecurity);
                    this.readers.put(externalMapping.getJavaTypeName(), externalMapping);
                }
                catch (final Exception e) {
                    throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[] { file.getAbsolutePath() });
                }
            }
        }
        if (resourcePaths != null) {
            for (final String resourcePath : resourcePaths) {
                try {
                    final String namespace = Util.documentRootNamespace(this.newSource(resourcePath, classLoader), disableXmlSecurity);
                    final JavaWsdlMappingType externalMapping = this.parseMetadata(xsdValidation, this.newSource(resourcePath, classLoader), namespace, disableXmlSecurity);
                    this.readers.put(externalMapping.getJavaTypeName(), externalMapping);
                }
                catch (final Exception e) {
                    throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[] { resourcePath });
                }
            }
        }
    }
    
    private StreamSource newSource(final String resourcePath, final ClassLoader classLoader) {
        final InputStream is = classLoader.getResourceAsStream(resourcePath);
        return new StreamSource(is);
    }
    
    private JavaWsdlMappingType parseMetadata(final boolean xsdValidation, final StreamSource source, final String namespace, final boolean disableXmlSecurity) throws JAXBException, IOException, TransformerException {
        if ("http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding".equals(namespace)) {
            return Util.transformAndRead(source, disableXmlSecurity);
        }
        if ("http://xmlns.oracle.com/webservices/jaxws-databinding".equals(namespace)) {
            return Util.read(source, xsdValidation, disableXmlSecurity);
        }
        throw new RuntimeModelerException("runtime.modeler.external.metadata.unsupported.schema", new Object[] { namespace, Arrays.asList("http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding", "http://xmlns.oracle.com/webservices/jaxws-databinding").toString() });
    }
    
    private StreamSource newSource(final File file) {
        try {
            return new StreamSource(new FileInputStream(file));
        }
        catch (final FileNotFoundException e) {
            throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[] { file.getAbsolutePath() });
        }
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annType, final Class<?> cls) {
        final JavaWsdlMappingType r = this.reader(cls);
        return (r == null) ? super.getAnnotation(annType, cls) : Util.annotation(r, annType);
    }
    
    private JavaWsdlMappingType reader(final Class<?> cls) {
        return this.readers.get(cls.getName());
    }
    
    Annotation[] getAnnotations(final List<Object> objects) {
        final ArrayList<Annotation> list = new ArrayList<Annotation>();
        for (final Object a : objects) {
            if (Annotation.class.isInstance(a)) {
                list.add(Annotation.class.cast(a));
            }
        }
        return list.toArray(new Annotation[list.size()]);
    }
    
    @Override
    public Annotation[] getAnnotations(final Class<?> c) {
        final Merger<Annotation[]> merger = new Merger<Annotation[]>(this.reader(c)) {
            @Override
            Annotation[] reflection() {
                return ReflectAnnotationReader.this.getAnnotations(c);
            }
            
            @Override
            Annotation[] external() {
                return ExternalMetadataReader.this.getAnnotations(this.reader.getClassAnnotation());
            }
        };
        return merger.merge();
    }
    
    @Override
    public Annotation[] getAnnotations(final Method m) {
        final Merger<Annotation[]> merger = new Merger<Annotation[]>(this.reader(m.getDeclaringClass())) {
            @Override
            Annotation[] reflection() {
                return ReflectAnnotationReader.this.getAnnotations(m);
            }
            
            @Override
            Annotation[] external() {
                final JavaMethod jm = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
                return (jm == null) ? new Annotation[0] : ExternalMetadataReader.this.getAnnotations(jm.getMethodAnnotation());
            }
        };
        return merger.merge();
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annType, final Method m) {
        final Merger<Annotation> merger = new Merger<Annotation>(this.reader(m.getDeclaringClass())) {
            @Override
            Annotation reflection() {
                return ReflectAnnotationReader.this.getAnnotation((Class<Annotation>)annType, m);
            }
            
            @Override
            Annotation external() {
                final JavaMethod jm = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
                return Util.annotation(jm, (Class<Annotation>)annType);
            }
        };
        return (A)merger.merge();
    }
    
    @Override
    public Annotation[][] getParameterAnnotations(final Method m) {
        final Merger<Annotation[][]> merger = new Merger<Annotation[][]>(this.reader(m.getDeclaringClass())) {
            @Override
            Annotation[][] reflection() {
                return ReflectAnnotationReader.this.getParameterAnnotations(m);
            }
            
            @Override
            Annotation[][] external() {
                final JavaMethod jm = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
                final Annotation[][] a = m.getParameterAnnotations();
                for (int i = 0; i < m.getParameterTypes().length; ++i) {
                    if (jm != null) {
                        final JavaParam jp = jm.getJavaParams().getJavaParam().get(i);
                        a[i] = ExternalMetadataReader.this.getAnnotations(jp.getParamAnnotation());
                    }
                }
                return a;
            }
        };
        return merger.merge();
    }
    
    @Override
    public void getProperties(final Map<String, Object> prop, final Class<?> cls) {
        final JavaWsdlMappingType r = this.reader(cls);
        if (r == null || ExistingAnnotationsType.MERGE.equals(r.getExistingAnnotations())) {
            super.getProperties(prop, cls);
        }
    }
    
    @Override
    public void getProperties(final Map<String, Object> prop, final Method m) {
        final JavaWsdlMappingType r = this.reader(m.getDeclaringClass());
        if (r == null || ExistingAnnotationsType.MERGE.equals(r.getExistingAnnotations())) {
            super.getProperties(prop, m);
        }
        if (r != null) {
            final JavaMethod jm = this.getJavaMethod(m, r);
            final Element[] e = Util.annotation(jm);
            prop.put("eclipselink-oxm-xml.xml-element", this.findXmlElement(e));
        }
    }
    
    @Override
    public void getProperties(final Map<String, Object> prop, final Method m, final int pos) {
        final JavaWsdlMappingType r = this.reader(m.getDeclaringClass());
        if (r == null || ExistingAnnotationsType.MERGE.equals(r.getExistingAnnotations())) {
            super.getProperties(prop, m, pos);
        }
        if (r != null) {
            final JavaMethod jm = this.getJavaMethod(m, r);
            if (jm == null) {
                return;
            }
            final JavaParam jp = jm.getJavaParams().getJavaParam().get(pos);
            final Element[] e = Util.annotation(jp);
            prop.put("eclipselink-oxm-xml.xml-element", this.findXmlElement(e));
        }
    }
    
    JavaMethod getJavaMethod(final Method method, final JavaWsdlMappingType r) {
        final JavaWsdlMappingType.JavaMethods javaMethods = r.getJavaMethods();
        if (javaMethods == null) {
            return null;
        }
        final List<JavaMethod> sameName = new ArrayList<JavaMethod>();
        for (final JavaMethod jm : javaMethods.getJavaMethod()) {
            if (method.getName().equals(jm.getName())) {
                sameName.add(jm);
            }
        }
        if (sameName.isEmpty()) {
            return null;
        }
        if (sameName.size() == 1) {
            return sameName.get(0);
        }
        final Class<?>[] argCls = method.getParameterTypes();
        for (final JavaMethod jm2 : sameName) {
            final JavaMethod.JavaParams params = jm2.getJavaParams();
            if (params != null && params.getJavaParam() != null && params.getJavaParam().size() == argCls.length) {
                int count = 0;
                for (int i = 0; i < argCls.length; ++i) {
                    final JavaParam jp = params.getJavaParam().get(i);
                    if (argCls[i].getName().equals(jp.getJavaType())) {
                        ++count;
                    }
                }
                if (count == argCls.length) {
                    return jm2;
                }
                continue;
            }
        }
        return null;
    }
    
    Element findXmlElement(final Element[] xa) {
        if (xa == null) {
            return null;
        }
        for (final Element e : xa) {
            if (e.getLocalName().equals("java-type")) {
                return e;
            }
            if (e.getLocalName().equals("xml-element")) {
                return e;
            }
        }
        return null;
    }
    
    abstract static class Merger<T>
    {
        JavaWsdlMappingType reader;
        
        Merger(final JavaWsdlMappingType r) {
            this.reader = r;
        }
        
        abstract T reflection();
        
        abstract T external();
        
        T merge() {
            final T reflection = this.reflection();
            if (this.reader == null) {
                return reflection;
            }
            final T external = this.external();
            if (!ExistingAnnotationsType.MERGE.equals(this.reader.getExistingAnnotations())) {
                return external;
            }
            if (reflection instanceof Annotation) {
                return (T)this.doMerge((Annotation)reflection, (Annotation)external);
            }
            if (reflection instanceof Annotation[][]) {
                return (T)(Object)this.doMerge((Annotation[][])(Object)reflection, (Annotation[][])(Object)external);
            }
            return (T)(Object)this.doMerge((Annotation[])(Object)reflection, (Annotation[])(Object)external);
        }
        
        private Annotation doMerge(final Annotation reflection, final Annotation external) {
            return (external != null) ? external : reflection;
        }
        
        private Annotation[][] doMerge(final Annotation[][] reflection, final Annotation[][] external) {
            for (int i = 0; i < reflection.length; ++i) {
                reflection[i] = this.doMerge(reflection[i], (Annotation[])((external.length > i) ? external[i] : null));
            }
            return reflection;
        }
        
        private Annotation[] doMerge(final Annotation[] annotations, final Annotation[] externalAnnotations) {
            final HashMap<String, Annotation> mergeMap = new HashMap<String, Annotation>();
            if (annotations != null) {
                for (final Annotation reflectionAnnotation : annotations) {
                    mergeMap.put(reflectionAnnotation.annotationType().getName(), reflectionAnnotation);
                }
            }
            if (externalAnnotations != null) {
                for (final Annotation externalAnnotation : externalAnnotations) {
                    mergeMap.put(externalAnnotation.annotationType().getName(), externalAnnotation);
                }
            }
            final Collection<Annotation> values = mergeMap.values();
            final int size = values.size();
            return (Annotation[])((size == 0) ? null : ((Annotation[])values.toArray(new Annotation[size])));
        }
    }
    
    static class Util
    {
        private static final String DATABINDING_XSD = "jaxws-databinding.xsd";
        private static final String TRANSLATE_NAMESPACES_XSL = "jaxws-databinding-translate-namespaces.xml";
        static Schema schema;
        static JAXBContext jaxbContext;
        
        private static URL getResource() {
            final ClassLoader classLoader = Util.class.getClassLoader();
            return (classLoader != null) ? classLoader.getResource("jaxws-databinding.xsd") : ClassLoader.getSystemResource("jaxws-databinding.xsd");
        }
        
        private static JAXBContext createJaxbContext(final boolean disableXmlSecurity) {
            final Class[] cls = { ObjectFactory.class };
            try {
                if (disableXmlSecurity) {
                    final Map<String, Object> properties = new HashMap<String, Object>();
                    properties.put("com.sun.xml.internal.bind.disableXmlSecurity", disableXmlSecurity);
                    return JAXBContext.newInstance(cls, properties);
                }
                return JAXBContext.newInstance(cls);
            }
            catch (final JAXBException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        public static JavaWsdlMappingType read(final Source src, final boolean xsdValidation, final boolean disableXmlSecurity) throws IOException, JAXBException {
            final JAXBContext ctx = jaxbContext(disableXmlSecurity);
            try {
                final Unmarshaller um = ctx.createUnmarshaller();
                if (xsdValidation) {
                    if (Util.schema == null) {}
                    um.setSchema(Util.schema);
                }
                final Object o = um.unmarshal(src);
                return getJavaWsdlMapping(o);
            }
            catch (final JAXBException e) {
                final URL url = new URL(src.getSystemId());
                final Source s = new StreamSource(url.openStream());
                final Unmarshaller um2 = ctx.createUnmarshaller();
                if (xsdValidation) {
                    if (Util.schema == null) {}
                    um2.setSchema(Util.schema);
                }
                final Object o2 = um2.unmarshal(s);
                return getJavaWsdlMapping(o2);
            }
        }
        
        private static JAXBContext jaxbContext(final boolean disableXmlSecurity) {
            return disableXmlSecurity ? createJaxbContext(true) : Util.jaxbContext;
        }
        
        public static JavaWsdlMappingType transformAndRead(final Source src, final boolean disableXmlSecurity) throws TransformerException, JAXBException {
            final Source xsl = new StreamSource(Util.class.getResourceAsStream("jaxws-databinding-translate-namespaces.xml"));
            final JAXBResult result = new JAXBResult(jaxbContext(disableXmlSecurity));
            final TransformerFactory tf = XmlUtil.newTransformerFactory(!disableXmlSecurity);
            final Transformer transformer = tf.newTemplates(xsl).newTransformer();
            transformer.transform(src, result);
            return getJavaWsdlMapping(result.getResult());
        }
        
        static JavaWsdlMappingType getJavaWsdlMapping(final Object o) {
            final Object val = (o instanceof JAXBElement) ? ((JAXBElement)o).getValue() : o;
            if (val instanceof JavaWsdlMappingType) {
                return (JavaWsdlMappingType)val;
            }
            return null;
        }
        
        static <T> T findInstanceOf(final Class<T> type, final List<Object> objects) {
            for (final Object o : objects) {
                if (type.isInstance(o)) {
                    return type.cast(o);
                }
            }
            return null;
        }
        
        public static <T> T annotation(final JavaWsdlMappingType jwse, final Class<T> anntype) {
            if (jwse == null || jwse.getClassAnnotation() == null) {
                return null;
            }
            return findInstanceOf(anntype, jwse.getClassAnnotation());
        }
        
        public static <T> T annotation(final JavaMethod jm, final Class<T> anntype) {
            if (jm == null || jm.getMethodAnnotation() == null) {
                return null;
            }
            return findInstanceOf(anntype, jm.getMethodAnnotation());
        }
        
        public static <T> T annotation(final JavaParam jp, final Class<T> anntype) {
            if (jp == null || jp.getParamAnnotation() == null) {
                return null;
            }
            return findInstanceOf(anntype, jp.getParamAnnotation());
        }
        
        public static Element[] annotation(final JavaMethod jm) {
            if (jm == null || jm.getMethodAnnotation() == null) {
                return null;
            }
            return findElements(jm.getMethodAnnotation());
        }
        
        public static Element[] annotation(final JavaParam jp) {
            if (jp == null || jp.getParamAnnotation() == null) {
                return null;
            }
            return findElements(jp.getParamAnnotation());
        }
        
        private static Element[] findElements(final List<Object> objects) {
            final List<Element> elems = new ArrayList<Element>();
            for (final Object o : objects) {
                if (o instanceof Element) {
                    elems.add((Element)o);
                }
            }
            return elems.toArray(new Element[elems.size()]);
        }
        
        static String documentRootNamespace(final Source src, final boolean disableXmlSecurity) throws XMLStreamException {
            final XMLInputFactory factory = XmlUtil.newXMLInputFactory(!disableXmlSecurity);
            final XMLStreamReader streamReader = factory.createXMLStreamReader(src);
            XMLStreamReaderUtil.nextElementContent(streamReader);
            final String namespaceURI = streamReader.getName().getNamespaceURI();
            XMLStreamReaderUtil.close(streamReader);
            return namespaceURI;
        }
        
        static {
            final SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            try {
                final URL xsdUrl = getResource();
                if (xsdUrl != null) {
                    Util.schema = sf.newSchema(xsdUrl);
                }
            }
            catch (final SAXException ex) {}
            Util.jaxbContext = createJaxbContext(false);
        }
    }
}
