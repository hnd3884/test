package org.msgpack.template.builder;

import org.msgpack.template.OrdinalEnumTemplate;
import org.msgpack.template.Template;
import java.util.logging.Level;
import java.lang.reflect.Type;
import org.msgpack.template.TemplateRegistry;
import java.util.logging.Logger;

public class OrdinalEnumTemplateBuilder extends AbstractTemplateBuilder
{
    private static final Logger LOG;
    
    public OrdinalEnumTemplateBuilder(final TemplateRegistry registry) {
        super(registry);
    }
    
    @Override
    public boolean matchType(final Type targetType, final boolean hasAnnotation) {
        final Class<?> targetClass = (Class<?>)targetType;
        final boolean matched = AbstractTemplateBuilder.matchAtOrdinalEnumTemplateBuilder(targetClass, hasAnnotation);
        if (matched && OrdinalEnumTemplateBuilder.LOG.isLoggable(Level.FINE)) {
            OrdinalEnumTemplateBuilder.LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }
    
    public <T> Template<T> buildTemplate(final Class<T> targetClass, final FieldEntry[] entries) {
        throw new UnsupportedOperationException("fatal error: " + targetClass.getName());
    }
    
    @Override
    public <T> Template<T> buildTemplate(final Type targetType) throws TemplateBuildException {
        final Class<T> targetClass = (Class<T>)targetType;
        this.checkOrdinalEnumValidation(targetClass);
        return new OrdinalEnumTemplate<T>(targetClass);
    }
    
    protected void checkOrdinalEnumValidation(final Class<?> targetClass) {
        if (!targetClass.isEnum()) {
            throw new TemplateBuildException("tried to build ordinal enum template of non-enum class: " + targetClass.getName());
        }
    }
    
    static {
        LOG = Logger.getLogger(OrdinalEnumTemplateBuilder.class.getName());
    }
}
