package com.sun.beans.finder;

import com.sun.beans.editors.EnumEditor;
import com.sun.beans.editors.DoubleEditor;
import com.sun.beans.editors.FloatEditor;
import com.sun.beans.editors.BooleanEditor;
import com.sun.beans.editors.LongEditor;
import com.sun.beans.editors.IntegerEditor;
import com.sun.beans.editors.ShortEditor;
import com.sun.beans.editors.ByteEditor;
import com.sun.beans.WeakCache;
import java.beans.PropertyEditor;

public final class PropertyEditorFinder extends InstanceFinder<PropertyEditor>
{
    private static final String DEFAULT = "sun.beans.editors";
    private static final String DEFAULT_NEW = "com.sun.beans.editors";
    private final WeakCache<Class<?>, Class<?>> registry;
    
    public PropertyEditorFinder() {
        super(PropertyEditor.class, false, "Editor", new String[] { "sun.beans.editors" });
        (this.registry = new WeakCache<Class<?>, Class<?>>()).put(Byte.TYPE, ByteEditor.class);
        this.registry.put(Short.TYPE, ShortEditor.class);
        this.registry.put(Integer.TYPE, IntegerEditor.class);
        this.registry.put(Long.TYPE, LongEditor.class);
        this.registry.put(Boolean.TYPE, BooleanEditor.class);
        this.registry.put(Float.TYPE, FloatEditor.class);
        this.registry.put(Double.TYPE, DoubleEditor.class);
    }
    
    public void register(final Class<?> clazz, final Class<?> clazz2) {
        synchronized (this.registry) {
            this.registry.put(clazz, clazz2);
        }
    }
    
    @Override
    public PropertyEditor find(final Class<?> clazz) {
        final Class clazz2;
        synchronized (this.registry) {
            clazz2 = this.registry.get(clazz);
        }
        PropertyEditor propertyEditor = this.instantiate(clazz2, null);
        if (propertyEditor == null) {
            propertyEditor = super.find(clazz);
            if (propertyEditor == null && null != clazz.getEnumConstants()) {
                propertyEditor = new EnumEditor(clazz);
            }
        }
        return propertyEditor;
    }
    
    @Override
    protected PropertyEditor instantiate(final Class<?> clazz, final String s, final String s2) {
        return super.instantiate(clazz, "sun.beans.editors".equals(s) ? "com.sun.beans.editors" : s, s2);
    }
}
