package org.msgpack.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

public class GenericMapTemplate implements GenericTemplate
{
    Constructor<? extends Template> constructor;
    
    public GenericMapTemplate(final TemplateRegistry registry, final Class<? extends Template> tmpl) {
        try {
            (this.constructor = tmpl.getConstructor(Template.class, Template.class)).newInstance(new AnyTemplate(registry), new AnyTemplate(registry));
        }
        catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        catch (final InvocationTargetException e2) {
            throw new IllegalArgumentException(e2);
        }
        catch (final IllegalAccessException e3) {
            throw new IllegalArgumentException(e3);
        }
        catch (final InstantiationException e4) {
            throw new IllegalArgumentException(e4);
        }
    }
    
    @Override
    public Template build(final Template[] params) {
        try {
            return (Template)this.constructor.newInstance((Object[])params);
        }
        catch (final InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        catch (final IllegalAccessException e2) {
            throw new IllegalArgumentException(e2);
        }
        catch (final InstantiationException e3) {
            throw new IllegalArgumentException(e3);
        }
    }
}
