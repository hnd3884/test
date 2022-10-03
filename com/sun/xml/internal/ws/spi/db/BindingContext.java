package com.sun.xml.internal.ws.spi.db;

import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import java.util.List;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public interface BindingContext
{
    public static final String DEFAULT_NAMESPACE_REMAP = "com.sun.xml.internal.bind.defaultNamespaceRemap";
    public static final String TYPE_REFERENCES = "com.sun.xml.internal.bind.typeReferences";
    public static final String CANONICALIZATION_SUPPORT = "com.sun.xml.internal.bind.c14n";
    public static final String TREAT_EVERYTHING_NILLABLE = "com.sun.xml.internal.bind.treatEverythingNillable";
    public static final String ENABLE_XOP = "com.sun.xml.internal.bind.XOP";
    public static final String SUBCLASS_REPLACEMENTS = "com.sun.xml.internal.bind.subclassReplacements";
    public static final String XMLACCESSORFACTORY_SUPPORT = "com.sun.xml.internal.bind.XmlAccessorFactory";
    public static final String RETAIN_REFERENCE_TO_INFO = "retainReferenceToInfo";
    
    Marshaller createMarshaller() throws JAXBException;
    
    Unmarshaller createUnmarshaller() throws JAXBException;
    
    JAXBContext getJAXBContext();
    
    Object newWrapperInstace(final Class<?> p0) throws InstantiationException, IllegalAccessException;
    
    boolean hasSwaRef();
    
    @Nullable
    QName getElementName(@NotNull final Object p0) throws JAXBException;
    
    @Nullable
    QName getElementName(@NotNull final Class p0) throws JAXBException;
    
    XMLBridge createBridge(@NotNull final TypeInfo p0);
    
    XMLBridge createFragmentBridge();
    
     <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(final Class<B> p0, final String p1, final String p2) throws JAXBException;
    
    @NotNull
    List<String> getKnownNamespaceURIs();
    
    void generateSchema(@NotNull final SchemaOutputResolver p0) throws IOException;
    
    QName getTypeName(@NotNull final TypeInfo p0);
    
    @NotNull
    String getBuildId();
}
