package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import java.util.Map;
import java.util.Collection;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import java.util.List;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.SEIModel;

public interface JAXBContextFactory
{
    public static final JAXBContextFactory DEFAULT = new JAXBContextFactory() {
        @NotNull
        @Override
        public JAXBRIContext createJAXBContext(@NotNull final SEIModel sei, @NotNull final List<Class> classesToBind, @NotNull final List<TypeReference> typeReferences) throws JAXBException {
            return JAXBRIContext.newInstance(classesToBind.toArray(new Class[classesToBind.size()]), typeReferences, null, sei.getTargetNamespace(), false, null);
        }
    };
    
    @NotNull
    JAXBRIContext createJAXBContext(@NotNull final SEIModel p0, @NotNull final List<Class> p1, @NotNull final List<TypeReference> p2) throws JAXBException;
}
