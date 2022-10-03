package org.msgpack.template.builder;

import org.msgpack.MessageTypeException;
import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.msgpack.template.Template;
import java.util.logging.Level;
import java.lang.reflect.Type;
import org.msgpack.template.TemplateRegistry;
import java.util.logging.Logger;

public class ReflectionTemplateBuilder extends AbstractTemplateBuilder
{
    private static Logger LOG;
    
    public ReflectionTemplateBuilder(final TemplateRegistry registry) {
        super(registry);
    }
    
    @Override
    public boolean matchType(final Type targetType, final boolean hasAnnotation) {
        final Class<?> targetClass = (Class<?>)targetType;
        final boolean matched = AbstractTemplateBuilder.matchAtClassTemplateBuilder(targetClass, hasAnnotation);
        if (matched && ReflectionTemplateBuilder.LOG.isLoggable(Level.FINE)) {
            ReflectionTemplateBuilder.LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }
    
    public <T> Template<T> buildTemplate(final Class<T> targetClass, final FieldEntry[] entries) {
        if (entries == null) {
            throw new NullPointerException("entries is null: " + targetClass);
        }
        final ReflectionFieldTemplate[] tmpls = this.toTemplates(entries);
        return new ReflectionClassTemplate<T>(targetClass, tmpls);
    }
    
    protected ReflectionFieldTemplate[] toTemplates(final FieldEntry[] entries) {
        for (final FieldEntry entry : entries) {
            final Field field = ((DefaultFieldEntry)entry).getField();
            final int mod = field.getModifiers();
            if (!Modifier.isPublic(mod)) {
                field.setAccessible(true);
            }
        }
        final ReflectionFieldTemplate[] templates = new ReflectionFieldTemplate[entries.length];
        for (int i = 0; i < entries.length; ++i) {
            final FieldEntry entry2 = entries[i];
            final Template template = this.registry.lookup(entry2.getGenericType());
            templates[i] = new FieldTemplateImpl(entry2, template);
        }
        return templates;
    }
    
    static {
        ReflectionTemplateBuilder.LOG = Logger.getLogger(ReflectionBeansTemplateBuilder.class.getName());
    }
    
    protected abstract static class ReflectionFieldTemplate extends AbstractTemplate<Object>
    {
        protected FieldEntry entry;
        
        ReflectionFieldTemplate(final FieldEntry entry) {
            this.entry = entry;
        }
        
        void setNil(final Object v) {
            this.entry.set(v, null);
        }
    }
    
    static final class FieldTemplateImpl extends ReflectionFieldTemplate
    {
        private Template template;
        
        public FieldTemplateImpl(final FieldEntry entry, final Template template) {
            super(entry);
            this.template = template;
        }
        
        @Override
        public void write(final Packer packer, final Object v, final boolean required) throws IOException {
            this.template.write(packer, v, required);
        }
        
        @Override
        public Object read(final Unpacker unpacker, final Object to, final boolean required) throws IOException {
            final Object f = this.entry.get(to);
            final Object o = this.template.read(unpacker, f, required);
            if (o != f) {
                this.entry.set(to, o);
            }
            return o;
        }
    }
    
    protected static class ReflectionClassTemplate<T> extends AbstractTemplate<T>
    {
        protected Class<T> targetClass;
        protected ReflectionFieldTemplate[] templates;
        
        protected ReflectionClassTemplate(final Class<T> targetClass, final ReflectionFieldTemplate[] templates) {
            this.targetClass = targetClass;
            this.templates = templates;
        }
        
        @Override
        public void write(final Packer packer, final T target, final boolean required) throws IOException {
            if (target != null) {
                try {
                    packer.writeArrayBegin(this.templates.length);
                    for (final ReflectionFieldTemplate tmpl : this.templates) {
                        if (!tmpl.entry.isAvailable()) {
                            packer.writeNil();
                        }
                        else {
                            final Object obj = tmpl.entry.get(target);
                            if (obj == null) {
                                if (tmpl.entry.isNotNullable()) {
                                    throw new MessageTypeException(tmpl.entry.getName() + " cannot be null by @NotNullable");
                                }
                                packer.writeNil();
                            }
                            else {
                                tmpl.write(packer, obj, true);
                            }
                        }
                    }
                    packer.writeArrayEnd();
                }
                catch (final IOException e) {
                    throw e;
                }
                catch (final Exception e2) {
                    throw new MessageTypeException(e2);
                }
                return;
            }
            if (required) {
                throw new MessageTypeException("attempted to write null");
            }
            packer.writeNil();
        }
        
        @Override
        public T read(final Unpacker unpacker, T to, final boolean required) throws IOException {
            if (!required && unpacker.trySkipNil()) {
                return null;
            }
            try {
                if (to == null) {
                    to = this.targetClass.newInstance();
                }
                unpacker.readArrayBegin();
                for (int i = 0; i < this.templates.length; ++i) {
                    final ReflectionFieldTemplate tmpl = this.templates[i];
                    if (!tmpl.entry.isAvailable()) {
                        unpacker.skip();
                    }
                    else if (!tmpl.entry.isOptional() || !unpacker.trySkipNil()) {
                        tmpl.read(unpacker, to, false);
                    }
                }
                unpacker.readArrayEnd();
                return to;
            }
            catch (final IOException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new MessageTypeException(e2);
            }
        }
    }
}
