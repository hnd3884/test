package org.glassfish.hk2.utilities;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import org.glassfish.hk2.api.Injectee;
import javax.inject.Inject;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Visibility;
import javax.inject.Singleton;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;

@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class GreedyResolver implements JustInTimeInjectionResolver
{
    private final ServiceLocator locator;
    
    @Inject
    private GreedyResolver(final ServiceLocator locator) {
        this.locator = locator;
    }
    
    @Override
    public boolean justInTimeResolution(final Injectee failedInjectionPoint) {
        final Type type = failedInjectionPoint.getRequiredType();
        if (type == null) {
            return false;
        }
        Class<?> clazzToAdd = null;
        if (type instanceof Class) {
            clazzToAdd = (Class)type;
        }
        else if (type instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType)type).getRawType();
            if (rawType instanceof Class) {
                clazzToAdd = (Class)rawType;
            }
        }
        if (clazzToAdd == null) {
            return false;
        }
        if (clazzToAdd.isInterface()) {
            final GreedyDefaultImplementation gdi = clazzToAdd.getAnnotation(GreedyDefaultImplementation.class);
            if (gdi == null) {
                return false;
            }
            clazzToAdd = gdi.value();
        }
        ServiceLocatorUtilities.addClasses(this.locator, clazzToAdd);
        return true;
    }
}
