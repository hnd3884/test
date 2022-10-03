package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;
import javax.xml.bind.Marshaller;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.bind.v2.ContextFactory;
import java.util.List;
import com.sun.xml.internal.ws.developer.JAXBContextFactory;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.BindingInfo;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import java.util.Map;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;

public class JAXBRIContextFactory extends BindingContextFactory
{
    public BindingContext newContext(final JAXBContext context) {
        return new JAXBRIContextWrapper((JAXBRIContext)context, null);
    }
    
    public BindingContext newContext(final BindingInfo bi) {
        final Class[] classes = bi.contentClasses().toArray(new Class[bi.contentClasses().size()]);
        for (int i = 0; i < classes.length; ++i) {
            if (WrapperComposite.class.equals(classes[i])) {
                classes[i] = CompositeStructure.class;
            }
        }
        final Map<TypeInfo, TypeReference> typeInfoMappings = this.typeInfoMappings(bi.typeInfos());
        final Map<Class, Class> subclassReplacements = bi.subclassReplacements();
        final String defaultNamespaceRemap = bi.getDefaultNamespace();
        final Boolean c14nSupport = bi.properties().get("c14nSupport");
        final RuntimeAnnotationReader ar = bi.properties().get("com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader");
        final JAXBContextFactory jaxbContextFactory = bi.properties().get(JAXBContextFactory.class.getName());
        try {
            final JAXBRIContext context = (jaxbContextFactory != null) ? jaxbContextFactory.createJAXBContext(bi.getSEIModel(), (List<Class>)this.toList(classes), this.toList(typeInfoMappings.values())) : ContextFactory.createContext(classes, typeInfoMappings.values(), subclassReplacements, defaultNamespaceRemap, c14nSupport != null && c14nSupport, ar, false, false, false);
            return new JAXBRIContextWrapper(context, typeInfoMappings);
        }
        catch (final Exception e) {
            throw new DatabindingException(e);
        }
    }
    
    private <T> List<T> toList(final T[] a) {
        final List<T> l = new ArrayList<T>();
        l.addAll((Collection<? extends T>)Arrays.asList(a));
        return l;
    }
    
    private <T> List<T> toList(final Collection<T> col) {
        if (col instanceof List) {
            return (List)col;
        }
        final List<T> l = new ArrayList<T>();
        l.addAll((Collection<? extends T>)col);
        return l;
    }
    
    private Map<TypeInfo, TypeReference> typeInfoMappings(final Collection<TypeInfo> typeInfos) {
        final Map<TypeInfo, TypeReference> map = new HashMap<TypeInfo, TypeReference>();
        for (final TypeInfo ti : typeInfos) {
            final Type type = WrapperComposite.class.equals(ti.type) ? CompositeStructure.class : ti.type;
            final TypeReference tr = new TypeReference(ti.tagName, type, ti.annotations);
            map.put(ti, tr);
        }
        return map;
    }
    
    @Override
    protected BindingContext getContext(final Marshaller m) {
        return this.newContext(((MarshallerImpl)m).getContext());
    }
    
    @Override
    protected boolean isFor(final String str) {
        return str.equals("glassfish.jaxb") || str.equals(this.getClass().getName()) || str.equals("com.sun.xml.internal.bind.v2.runtime");
    }
}
