package com.sun.xml.internal.bind.api;

import java.lang.reflect.Type;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import javax.xml.transform.Result;
import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import java.util.List;
import javax.xml.namespace.QName;
import java.util.Collections;
import com.sun.xml.internal.bind.v2.ContextFactory;
import java.util.HashMap;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import java.util.Map;
import com.sun.istack.internal.Nullable;
import java.util.Collection;
import com.sun.istack.internal.NotNull;
import javax.xml.bind.JAXBContext;

public abstract class JAXBRIContext extends JAXBContext
{
    public static final String DEFAULT_NAMESPACE_REMAP = "com.sun.xml.internal.bind.defaultNamespaceRemap";
    public static final String TYPE_REFERENCES = "com.sun.xml.internal.bind.typeReferences";
    public static final String CANONICALIZATION_SUPPORT = "com.sun.xml.internal.bind.c14n";
    public static final String TREAT_EVERYTHING_NILLABLE = "com.sun.xml.internal.bind.treatEverythingNillable";
    public static final String ANNOTATION_READER;
    public static final String ENABLE_XOP = "com.sun.xml.internal.bind.XOP";
    public static final String SUBCLASS_REPLACEMENTS = "com.sun.xml.internal.bind.subclassReplacements";
    public static final String XMLACCESSORFACTORY_SUPPORT = "com.sun.xml.internal.bind.XmlAccessorFactory";
    public static final String RETAIN_REFERENCE_TO_INFO = "retainReferenceToInfo";
    public static final String SUPRESS_ACCESSOR_WARNINGS = "supressAccessorWarnings";
    public static final String IMPROVED_XSI_TYPE_HANDLING = "com.sun.xml.internal.bind.improvedXsiTypeHandling";
    public static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.bind.disableXmlSecurity";
    
    protected JAXBRIContext() {
    }
    
    public static JAXBRIContext newInstance(@NotNull final Class[] classes, @Nullable final Collection<TypeReference> typeRefs, @Nullable final Map<Class, Class> subclassReplacements, @Nullable final String defaultNamespaceRemap, final boolean c14nSupport, @Nullable final RuntimeAnnotationReader ar) throws JAXBException {
        return newInstance(classes, typeRefs, subclassReplacements, defaultNamespaceRemap, c14nSupport, ar, false, false, false, false);
    }
    
    public static JAXBRIContext newInstance(@NotNull final Class[] classes, @Nullable final Collection<TypeReference> typeRefs, @Nullable final Map<Class, Class> subclassReplacements, @Nullable final String defaultNamespaceRemap, final boolean c14nSupport, @Nullable final RuntimeAnnotationReader ar, final boolean xmlAccessorFactorySupport, final boolean allNillable, final boolean retainPropertyInfo, final boolean supressAccessorWarnings) throws JAXBException {
        final Map<String, Object> properties = new HashMap<String, Object>();
        if (typeRefs != null) {
            properties.put("com.sun.xml.internal.bind.typeReferences", typeRefs);
        }
        if (subclassReplacements != null) {
            properties.put("com.sun.xml.internal.bind.subclassReplacements", subclassReplacements);
        }
        if (defaultNamespaceRemap != null) {
            properties.put("com.sun.xml.internal.bind.defaultNamespaceRemap", defaultNamespaceRemap);
        }
        if (ar != null) {
            properties.put(JAXBRIContext.ANNOTATION_READER, ar);
        }
        properties.put("com.sun.xml.internal.bind.c14n", c14nSupport);
        properties.put("com.sun.xml.internal.bind.XmlAccessorFactory", xmlAccessorFactorySupport);
        properties.put("com.sun.xml.internal.bind.treatEverythingNillable", allNillable);
        properties.put("retainReferenceToInfo", retainPropertyInfo);
        properties.put("supressAccessorWarnings", supressAccessorWarnings);
        return (JAXBRIContext)ContextFactory.createContext(classes, properties);
    }
    
    @Deprecated
    public static JAXBRIContext newInstance(@NotNull final Class[] classes, @Nullable final Collection<TypeReference> typeRefs, @Nullable final String defaultNamespaceRemap, final boolean c14nSupport) throws JAXBException {
        return newInstance(classes, typeRefs, (Map<Class, Class>)Collections.emptyMap(), defaultNamespaceRemap, c14nSupport, null);
    }
    
    public abstract boolean hasSwaRef();
    
    @Nullable
    public abstract QName getElementName(@NotNull final Object p0) throws JAXBException;
    
    @Nullable
    public abstract QName getElementName(@NotNull final Class p0) throws JAXBException;
    
    public abstract Bridge createBridge(@NotNull final TypeReference p0);
    
    @NotNull
    public abstract BridgeContext createBridgeContext();
    
    public abstract <B, V> RawAccessor<B, V> getElementPropertyAccessor(final Class<B> p0, final String p1, final String p2) throws JAXBException;
    
    @NotNull
    public abstract List<String> getKnownNamespaceURIs();
    
    @Override
    public abstract void generateSchema(@NotNull final SchemaOutputResolver p0) throws IOException;
    
    public abstract QName getTypeName(@NotNull final TypeReference p0);
    
    @NotNull
    public abstract String getBuildId();
    
    public abstract void generateEpisode(final Result p0);
    
    public abstract RuntimeTypeInfoSet getRuntimeTypeInfoSet();
    
    @NotNull
    public static String mangleNameToVariableName(@NotNull final String localName) {
        return NameConverter.standard.toVariableName(localName);
    }
    
    @NotNull
    public static String mangleNameToClassName(@NotNull final String localName) {
        return NameConverter.standard.toClassName(localName);
    }
    
    @NotNull
    public static String mangleNameToPropertyName(@NotNull final String localName) {
        return NameConverter.standard.toPropertyName(localName);
    }
    
    @Nullable
    public static Type getBaseType(@NotNull final Type type, @NotNull final Class baseType) {
        return Utils.REFLECTION_NAVIGATOR.getBaseClass(type, baseType);
    }
    
    static {
        ANNOTATION_READER = RuntimeAnnotationReader.class.getName();
    }
}
