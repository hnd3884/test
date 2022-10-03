package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.MessageTypeException;
import java.lang.reflect.Type;

public class TemplateReference<T> extends AbstractTemplate<T>
{
    private TemplateRegistry registry;
    private Type targetType;
    private Template<T> actualTemplate;
    
    public TemplateReference(final TemplateRegistry registry, final Type targetType) {
        this.registry = registry;
        this.targetType = targetType;
    }
    
    private void validateActualTemplate() {
        if (this.actualTemplate == null) {
            this.actualTemplate = (Template)this.registry.cache.get(this.targetType);
            if (this.actualTemplate == null) {
                throw new MessageTypeException("Actual template have not been created");
            }
        }
    }
    
    @Override
    public void write(final Packer pk, final T v, final boolean required) throws IOException {
        this.validateActualTemplate();
        this.actualTemplate.write(pk, v, required);
    }
    
    @Override
    public void write(final Packer pk, final T v) throws IOException {
        this.validateActualTemplate();
        this.actualTemplate.write(pk, v, false);
    }
    
    @Override
    public T read(final Unpacker u, final T to, final boolean required) throws IOException {
        this.validateActualTemplate();
        return this.actualTemplate.read(u, to, required);
    }
    
    @Override
    public T read(final Unpacker u, final T to) throws IOException {
        this.validateActualTemplate();
        return this.actualTemplate.read(u, to, false);
    }
}
