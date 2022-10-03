package org.msgpack.template.builder;

import java.util.Iterator;
import java.lang.reflect.Type;
import java.util.ArrayList;
import org.msgpack.template.TemplateRegistry;
import java.util.List;

public class TemplateBuilderChain
{
    protected List<TemplateBuilder> templateBuilders;
    protected TemplateBuilder forceBuilder;
    
    private static boolean enableDynamicCodeGeneration() {
        try {
            return !System.getProperty("java.vm.name").equals("Dalvik");
        }
        catch (final Exception e) {
            return true;
        }
    }
    
    public TemplateBuilderChain(final TemplateRegistry registry) {
        this(registry, null);
    }
    
    public TemplateBuilderChain(final TemplateRegistry registry, final ClassLoader cl) {
        this.templateBuilders = new ArrayList<TemplateBuilder>();
        this.reset(registry, cl);
    }
    
    protected void reset(final TemplateRegistry registry, final ClassLoader cl) {
        if (registry == null) {
            throw new NullPointerException("registry is null");
        }
        this.forceBuilder = new JavassistTemplateBuilder(registry);
        if (cl != null) {
            ((JavassistTemplateBuilder)this.forceBuilder).addClassLoader(cl);
        }
        this.templateBuilders.add(new ArrayTemplateBuilder(registry));
        this.templateBuilders.add(new OrdinalEnumTemplateBuilder(registry));
        if (enableDynamicCodeGeneration()) {
            final TemplateBuilder builder = this.forceBuilder;
            this.templateBuilders.add(builder);
            this.templateBuilders.add(new ReflectionBeansTemplateBuilder(registry));
        }
        else {
            final TemplateBuilder builder = new ReflectionTemplateBuilder(registry);
            this.templateBuilders.add(builder);
            this.templateBuilders.add(new ReflectionBeansTemplateBuilder(registry));
        }
    }
    
    public TemplateBuilder getForceBuilder() {
        return this.forceBuilder;
    }
    
    public TemplateBuilder select(final Type targetType, final boolean hasAnnotation) {
        for (final TemplateBuilder tb : this.templateBuilders) {
            if (tb.matchType(targetType, hasAnnotation)) {
                return tb;
            }
        }
        return null;
    }
}
