package javax.servlet.jsp.el;

import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.ImportHandler;
import javax.el.ELClass;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import java.util.Objects;
import javax.el.ELContext;
import javax.el.ELResolver;

public class ScopedAttributeELResolver extends ELResolver
{
    private static final Class<?> AST_IDENTIFIER_KEY;
    
    public Object getValue(final ELContext context, final Object base, final Object property) {
        Objects.requireNonNull(context);
        Object result = null;
        if (base == null) {
            context.setPropertyResolved(base, property);
            if (property != null) {
                final String key = property.toString();
                final PageContext page = (PageContext)context.getContext((Class)JspContext.class);
                result = page.findAttribute(key);
                if (result == null) {
                    boolean resolveClass = true;
                    if (ScopedAttributeELResolver.AST_IDENTIFIER_KEY != null) {
                        final Boolean value = (Boolean)context.getContext((Class)ScopedAttributeELResolver.AST_IDENTIFIER_KEY);
                        if (value != null && value) {
                            resolveClass = false;
                        }
                    }
                    final ImportHandler importHandler = context.getImportHandler();
                    if (importHandler != null) {
                        Class<?> clazz = null;
                        if (resolveClass) {
                            clazz = importHandler.resolveClass(key);
                        }
                        if (clazz != null) {
                            result = new ELClass((Class)clazz);
                        }
                        if (result == null) {
                            clazz = importHandler.resolveStatic(key);
                            if (clazz != null) {
                                try {
                                    result = clazz.getField(key).get(null);
                                }
                                catch (final IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {}
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public Class<Object> getType(final ELContext context, final Object base, final Object property) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
            return Object.class;
        }
        return null;
    }
    
    public void setValue(final ELContext context, final Object base, final Object property, final Object value) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
            if (property != null) {
                final String key = property.toString();
                final PageContext page = (PageContext)context.getContext((Class)JspContext.class);
                final int scope = page.getAttributesScope(key);
                if (scope != 0) {
                    page.setAttribute(key, value, scope);
                }
                else {
                    page.setAttribute(key, value);
                }
            }
        }
    }
    
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
        }
        return false;
    }
    
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        final PageContext ctxt = (PageContext)context.getContext((Class)JspContext.class);
        final List<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>();
        Enumeration<String> e = ctxt.getAttributeNamesInScope(1);
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            final Object value = ctxt.getAttribute(name, 1);
            final FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setShortDescription("page scoped attribute");
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
            list.add(descriptor);
        }
        e = ctxt.getAttributeNamesInScope(2);
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            final Object value = ctxt.getAttribute(name, 2);
            final FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setShortDescription("request scope attribute");
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
            list.add(descriptor);
        }
        if (ctxt.getSession() != null) {
            e = ctxt.getAttributeNamesInScope(3);
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                final Object value = ctxt.getAttribute(name, 3);
                final FeatureDescriptor descriptor = new FeatureDescriptor();
                descriptor.setName(name);
                descriptor.setDisplayName(name);
                descriptor.setExpert(false);
                descriptor.setHidden(false);
                descriptor.setPreferred(true);
                descriptor.setShortDescription("session scoped attribute");
                descriptor.setValue("type", value.getClass());
                descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
                list.add(descriptor);
            }
        }
        e = ctxt.getAttributeNamesInScope(4);
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            final Object value = ctxt.getAttribute(name, 4);
            final FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setShortDescription("application scoped attribute");
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
            list.add(descriptor);
        }
        return list.iterator();
    }
    
    public Class<String> getCommonPropertyType(final ELContext context, final Object base) {
        if (base == null) {
            return String.class;
        }
        return null;
    }
    
    static {
        Class<?> key = null;
        try {
            key = Class.forName("org.apache.el.parser.AstIdentifier");
        }
        catch (final Exception ex) {}
        AST_IDENTIFIER_KEY = key;
    }
}
