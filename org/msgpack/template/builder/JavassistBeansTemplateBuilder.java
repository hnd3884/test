package org.msgpack.template.builder;

import java.util.logging.Level;
import java.lang.reflect.Type;
import org.msgpack.template.TemplateRegistry;
import java.util.logging.Logger;

public class JavassistBeansTemplateBuilder extends JavassistTemplateBuilder
{
    private static final Logger LOG;
    
    public JavassistBeansTemplateBuilder(final TemplateRegistry registry) {
        super(registry);
    }
    
    @Override
    public boolean matchType(final Type targetType, final boolean hasAnnotation) {
        final Class<?> targetClass = (Class<?>)targetType;
        final boolean matched = AbstractTemplateBuilder.matchAtClassTemplateBuilder(targetClass, hasAnnotation);
        if (matched && JavassistBeansTemplateBuilder.LOG.isLoggable(Level.FINE)) {
            JavassistBeansTemplateBuilder.LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }
    
    @Override
    protected BuildContext createBuildContext() {
        return new BeansBuildContext(this);
    }
    
    static {
        LOG = Logger.getLogger(JavassistBeansTemplateBuilder.class.getName());
    }
}
