package org.glassfish.jersey.model.internal;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import java.util.Map;
import org.glassfish.jersey.internal.LocalizationMessages;

public class ImmutableCommonConfig extends CommonConfig
{
    private final String errorMessage;
    
    public ImmutableCommonConfig(final CommonConfig config, final String modificationErrorMessage) {
        super(config);
        this.errorMessage = modificationErrorMessage;
    }
    
    public ImmutableCommonConfig(final CommonConfig config) {
        this(config, LocalizationMessages.CONFIGURATION_NOT_MODIFIABLE());
    }
    
    @Override
    public ImmutableCommonConfig property(final String name, final Object value) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public ImmutableCommonConfig setProperties(final Map<String, ?> properties) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public ImmutableCommonConfig register(final Class<?> componentClass) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public ImmutableCommonConfig register(final Class<?> componentClass, final int bindingPriority) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public ImmutableCommonConfig register(final Class<?> componentClass, final Class<?>... contracts) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public CommonConfig register(final Class<?> componentClass, final Map<Class<?>, Integer> contracts) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public ImmutableCommonConfig register(final Object component) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public ImmutableCommonConfig register(final Object component, final int bindingPriority) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public ImmutableCommonConfig register(final Object component, final Class<?>... contracts) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public CommonConfig register(final Object component, final Map<Class<?>, Integer> contracts) {
        throw new IllegalStateException(this.errorMessage);
    }
    
    @Override
    public CommonConfig loadFrom(final Configuration config) {
        throw new IllegalStateException(this.errorMessage);
    }
}
