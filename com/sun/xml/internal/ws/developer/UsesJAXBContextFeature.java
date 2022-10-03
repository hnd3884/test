package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.api.TypeReference;
import java.util.List;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.lang.reflect.InvocationTargetException;
import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class UsesJAXBContextFeature extends WebServiceFeature
{
    public static final String ID = "http://jax-ws.dev.java.net/features/uses-jaxb-context";
    private final JAXBContextFactory factory;
    
    @FeatureConstructor({ "value" })
    public UsesJAXBContextFeature(@NotNull final Class<? extends JAXBContextFactory> factoryClass) {
        try {
            this.factory = (JAXBContextFactory)factoryClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final InstantiationException e) {
            final Error x = new InstantiationError(e.getMessage());
            x.initCause(e);
            throw x;
        }
        catch (final IllegalAccessException e2) {
            final Error x = new IllegalAccessError(e2.getMessage());
            x.initCause(e2);
            throw x;
        }
        catch (final InvocationTargetException e3) {
            final Error x = new InstantiationError(e3.getMessage());
            x.initCause(e3);
            throw x;
        }
        catch (final NoSuchMethodException e4) {
            final Error x = new NoSuchMethodError(e4.getMessage());
            x.initCause(e4);
            throw x;
        }
    }
    
    public UsesJAXBContextFeature(@Nullable final JAXBContextFactory factory) {
        this.factory = factory;
    }
    
    public UsesJAXBContextFeature(@Nullable final JAXBRIContext context) {
        this.factory = new JAXBContextFactory() {
            @NotNull
            @Override
            public JAXBRIContext createJAXBContext(@NotNull final SEIModel sei, @NotNull final List<Class> classesToBind, @NotNull final List<TypeReference> typeReferences) throws JAXBException {
                return context;
            }
        };
    }
    
    @ManagedAttribute
    @Nullable
    public JAXBContextFactory getFactory() {
        return this.factory;
    }
    
    @ManagedAttribute
    @Override
    public String getID() {
        return "http://jax-ws.dev.java.net/features/uses-jaxb-context";
    }
}
