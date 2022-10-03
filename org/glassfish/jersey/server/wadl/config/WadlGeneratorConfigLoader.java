package org.glassfish.jersey.server.wadl.config;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WadlGeneratorConfigLoader
{
    public static WadlGeneratorConfig loadWadlGeneratorsFromConfig(final Map<String, Object> properties) {
        final Object wadlGeneratorConfigProperty = properties.get("jersey.config.server.wadl.generatorConfig");
        if (wadlGeneratorConfigProperty == null) {
            return new WadlGeneratorConfig() {
                @Override
                public List<WadlGeneratorDescription> configure() {
                    return Collections.emptyList();
                }
            };
        }
        try {
            if (wadlGeneratorConfigProperty instanceof WadlGeneratorConfig) {
                return (WadlGeneratorConfig)wadlGeneratorConfigProperty;
            }
            Class<? extends WadlGeneratorConfig> configClazz;
            if (wadlGeneratorConfigProperty instanceof Class) {
                configClazz = ((Class)wadlGeneratorConfigProperty).asSubclass(WadlGeneratorConfig.class);
            }
            else {
                if (!(wadlGeneratorConfigProperty instanceof String)) {
                    throw new ProcessingException(LocalizationMessages.ERROR_WADL_GENERATOR_CONFIG_LOADER_PROPERTY("jersey.config.server.wadl.generatorConfig", wadlGeneratorConfigProperty.getClass().getName()));
                }
                configClazz = AccessController.doPrivileged((PrivilegedExceptionAction<Class>)ReflectionHelper.classForNameWithExceptionPEA((String)wadlGeneratorConfigProperty)).asSubclass(WadlGeneratorConfig.class);
            }
            return (WadlGeneratorConfig)configClazz.newInstance();
        }
        catch (final PrivilegedActionException pae) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_GENERATOR_CONFIG_LOADER("jersey.config.server.wadl.generatorConfig"), pae.getCause());
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_GENERATOR_CONFIG_LOADER("jersey.config.server.wadl.generatorConfig"), (Throwable)e);
        }
    }
}
