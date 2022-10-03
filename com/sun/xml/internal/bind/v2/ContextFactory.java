package com.sun.xml.internal.bind.v2;

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.io.IOException;
import java.util.StringTokenizer;
import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.v2.util.TypeCast;
import java.util.Collection;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import java.util.logging.Level;
import com.sun.xml.internal.bind.Util;
import java.util.HashMap;
import java.util.Collections;
import javax.xml.bind.JAXBContext;
import java.util.Map;

public class ContextFactory
{
    public static final String USE_JAXB_PROPERTIES = "_useJAXBProperties";
    
    public static JAXBContext createContext(final Class[] classes, Map<String, Object> properties) throws JAXBException {
        if (properties == null) {
            properties = Collections.emptyMap();
        }
        else {
            properties = new HashMap<String, Object>(properties);
        }
        final String defaultNsUri = getPropertyValue(properties, "com.sun.xml.internal.bind.defaultNamespaceRemap", String.class);
        Boolean c14nSupport = getPropertyValue(properties, "com.sun.xml.internal.bind.c14n", Boolean.class);
        if (c14nSupport == null) {
            c14nSupport = false;
        }
        Boolean disablesecurityProcessing = getPropertyValue(properties, "com.sun.xml.internal.bind.disableXmlSecurity", Boolean.class);
        if (disablesecurityProcessing == null) {
            disablesecurityProcessing = false;
        }
        Boolean allNillable = getPropertyValue(properties, "com.sun.xml.internal.bind.treatEverythingNillable", Boolean.class);
        if (allNillable == null) {
            allNillable = false;
        }
        Boolean retainPropertyInfo = getPropertyValue(properties, "retainReferenceToInfo", Boolean.class);
        if (retainPropertyInfo == null) {
            retainPropertyInfo = false;
        }
        Boolean supressAccessorWarnings = getPropertyValue(properties, "supressAccessorWarnings", Boolean.class);
        if (supressAccessorWarnings == null) {
            supressAccessorWarnings = false;
        }
        Boolean improvedXsiTypeHandling = getPropertyValue(properties, "com.sun.xml.internal.bind.improvedXsiTypeHandling", Boolean.class);
        if (improvedXsiTypeHandling == null) {
            final String improvedXsiSystemProperty = Util.getSystemProperty("com.sun.xml.internal.bind.improvedXsiTypeHandling");
            if (improvedXsiSystemProperty == null) {
                improvedXsiTypeHandling = true;
            }
            else {
                improvedXsiTypeHandling = Boolean.valueOf(improvedXsiSystemProperty);
            }
        }
        Boolean xmlAccessorFactorySupport = getPropertyValue(properties, "com.sun.xml.internal.bind.XmlAccessorFactory", Boolean.class);
        if (xmlAccessorFactorySupport == null) {
            xmlAccessorFactorySupport = false;
            Util.getClassLogger().log(Level.FINE, "Property com.sun.xml.internal.bind.XmlAccessorFactoryis not active.  Using JAXB's implementation");
        }
        final RuntimeAnnotationReader ar = getPropertyValue(properties, JAXBRIContext.ANNOTATION_READER, RuntimeAnnotationReader.class);
        Collection<TypeReference> tr = getPropertyValue(properties, "com.sun.xml.internal.bind.typeReferences", Collection.class);
        if (tr == null) {
            tr = (Collection<TypeReference>)Collections.emptyList();
        }
        Map<Class, Class> subclassReplacements;
        try {
            subclassReplacements = (Map<Class, Class>)TypeCast.checkedCast(getPropertyValue(properties, "com.sun.xml.internal.bind.subclassReplacements", Map.class), Class.class, Class.class);
        }
        catch (final ClassCastException e) {
            throw new JAXBException(Messages.INVALID_TYPE_IN_MAP.format(new Object[0]), e);
        }
        if (!properties.isEmpty()) {
            throw new JAXBException(Messages.UNSUPPORTED_PROPERTY.format(properties.keySet().iterator().next()));
        }
        final JAXBContextImpl.JAXBContextBuilder builder = new JAXBContextImpl.JAXBContextBuilder();
        builder.setClasses(classes);
        builder.setTypeRefs(tr);
        builder.setSubclassReplacements(subclassReplacements);
        builder.setDefaultNsUri(defaultNsUri);
        builder.setC14NSupport(c14nSupport);
        builder.setAnnotationReader(ar);
        builder.setXmlAccessorFactorySupport(xmlAccessorFactorySupport);
        builder.setAllNillable(allNillable);
        builder.setRetainPropertyInfo(retainPropertyInfo);
        builder.setSupressAccessorWarnings(supressAccessorWarnings);
        builder.setImprovedXsiTypeHandling(improvedXsiTypeHandling);
        builder.setDisableSecurityProcessing(disablesecurityProcessing);
        return builder.build();
    }
    
    private static <T> T getPropertyValue(final Map<String, Object> properties, final String keyName, final Class<T> type) throws JAXBException {
        final Object o = properties.get(keyName);
        if (o == null) {
            return null;
        }
        properties.remove(keyName);
        if (!type.isInstance(o)) {
            throw new JAXBException(Messages.INVALID_PROPERTY_VALUE.format(keyName, o));
        }
        return type.cast(o);
    }
    
    @Deprecated
    public static JAXBRIContext createContext(final Class[] classes, final Collection<TypeReference> typeRefs, final Map<Class, Class> subclassReplacements, final String defaultNsUri, final boolean c14nSupport, final RuntimeAnnotationReader ar, final boolean xmlAccessorFactorySupport, final boolean allNillable, final boolean retainPropertyInfo) throws JAXBException {
        return createContext(classes, typeRefs, subclassReplacements, defaultNsUri, c14nSupport, ar, xmlAccessorFactorySupport, allNillable, retainPropertyInfo, false);
    }
    
    @Deprecated
    public static JAXBRIContext createContext(final Class[] classes, final Collection<TypeReference> typeRefs, final Map<Class, Class> subclassReplacements, final String defaultNsUri, final boolean c14nSupport, final RuntimeAnnotationReader ar, final boolean xmlAccessorFactorySupport, final boolean allNillable, final boolean retainPropertyInfo, final boolean improvedXsiTypeHandling) throws JAXBException {
        final JAXBContextImpl.JAXBContextBuilder builder = new JAXBContextImpl.JAXBContextBuilder();
        builder.setClasses(classes);
        builder.setTypeRefs(typeRefs);
        builder.setSubclassReplacements(subclassReplacements);
        builder.setDefaultNsUri(defaultNsUri);
        builder.setC14NSupport(c14nSupport);
        builder.setAnnotationReader(ar);
        builder.setXmlAccessorFactorySupport(xmlAccessorFactorySupport);
        builder.setAllNillable(allNillable);
        builder.setRetainPropertyInfo(retainPropertyInfo);
        builder.setImprovedXsiTypeHandling(improvedXsiTypeHandling);
        return builder.build();
    }
    
    public static JAXBContext createContext(final String contextPath, final ClassLoader classLoader, final Map<String, Object> properties) throws JAXBException {
        final FinalArrayList<Class> classes = new FinalArrayList<Class>();
        final StringTokenizer tokens = new StringTokenizer(contextPath, ":");
        while (tokens.hasMoreTokens()) {
            boolean foundObjectFactory;
            boolean foundJaxbIndex = foundObjectFactory = false;
            final String pkg = tokens.nextToken();
            try {
                final Class<?> o = classLoader.loadClass(pkg + ".ObjectFactory");
                classes.add(o);
                foundObjectFactory = true;
            }
            catch (final ClassNotFoundException ex) {}
            List<Class> indexedClasses;
            try {
                indexedClasses = loadIndexedClasses(pkg, classLoader);
            }
            catch (final IOException e) {
                throw new JAXBException(e);
            }
            if (indexedClasses != null) {
                classes.addAll((Collection<?>)indexedClasses);
                foundJaxbIndex = true;
            }
            if (!foundObjectFactory && !foundJaxbIndex) {
                throw new JAXBException(Messages.BROKEN_CONTEXTPATH.format(pkg));
            }
        }
        return createContext(classes.toArray(new Class[classes.size()]), properties);
    }
    
    private static List<Class> loadIndexedClasses(final String pkg, final ClassLoader classLoader) throws IOException, JAXBException {
        final String resource = pkg.replace('.', '/') + "/jaxb.index";
        final InputStream resourceAsStream = classLoader.getResourceAsStream(resource);
        if (resourceAsStream == null) {
            return null;
        }
        final BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"));
        try {
            final FinalArrayList<Class> classes = new FinalArrayList<Class>();
            for (String className = in.readLine(); className != null; className = in.readLine()) {
                className = className.trim();
                if (!className.startsWith("#") && className.length() != 0) {
                    if (className.endsWith(".class")) {
                        throw new JAXBException(Messages.ILLEGAL_ENTRY.format(className));
                    }
                    try {
                        classes.add(classLoader.loadClass(pkg + '.' + className));
                    }
                    catch (final ClassNotFoundException e) {
                        throw new JAXBException(Messages.ERROR_LOADING_CLASS.format(className, resource), e);
                    }
                }
            }
            return classes;
        }
        finally {
            in.close();
        }
    }
}
