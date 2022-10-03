package java.beans;

import java.util.Iterator;
import com.sun.beans.TypeResolver;
import java.lang.reflect.Method;
import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Enumeration;
import java.util.Hashtable;
import java.lang.ref.Reference;

public class FeatureDescriptor
{
    private static final String TRANSIENT = "transient";
    private Reference<? extends Class<?>> classRef;
    private boolean expert;
    private boolean hidden;
    private boolean preferred;
    private String shortDescription;
    private String name;
    private String displayName;
    private Hashtable<String, Object> table;
    
    public FeatureDescriptor() {
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        if (this.displayName == null) {
            return this.getName();
        }
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public boolean isExpert() {
        return this.expert;
    }
    
    public void setExpert(final boolean expert) {
        this.expert = expert;
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
    
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }
    
    public boolean isPreferred() {
        return this.preferred;
    }
    
    public void setPreferred(final boolean preferred) {
        this.preferred = preferred;
    }
    
    public String getShortDescription() {
        if (this.shortDescription == null) {
            return this.getDisplayName();
        }
        return this.shortDescription;
    }
    
    public void setShortDescription(final String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    public void setValue(final String s, final Object o) {
        this.getTable().put(s, o);
    }
    
    public Object getValue(final String s) {
        return (this.table != null) ? this.table.get(s) : null;
    }
    
    public Enumeration<String> attributeNames() {
        return this.getTable().keys();
    }
    
    FeatureDescriptor(final FeatureDescriptor featureDescriptor, final FeatureDescriptor featureDescriptor2) {
        this.expert = (featureDescriptor.expert | featureDescriptor2.expert);
        this.hidden = (featureDescriptor.hidden | featureDescriptor2.hidden);
        this.preferred = (featureDescriptor.preferred | featureDescriptor2.preferred);
        this.name = featureDescriptor2.name;
        this.shortDescription = featureDescriptor.shortDescription;
        if (featureDescriptor2.shortDescription != null) {
            this.shortDescription = featureDescriptor2.shortDescription;
        }
        this.displayName = featureDescriptor.displayName;
        if (featureDescriptor2.displayName != null) {
            this.displayName = featureDescriptor2.displayName;
        }
        this.classRef = featureDescriptor.classRef;
        if (featureDescriptor2.classRef != null) {
            this.classRef = featureDescriptor2.classRef;
        }
        this.addTable(featureDescriptor.table);
        this.addTable(featureDescriptor2.table);
    }
    
    FeatureDescriptor(final FeatureDescriptor featureDescriptor) {
        this.expert = featureDescriptor.expert;
        this.hidden = featureDescriptor.hidden;
        this.preferred = featureDescriptor.preferred;
        this.name = featureDescriptor.name;
        this.shortDescription = featureDescriptor.shortDescription;
        this.displayName = featureDescriptor.displayName;
        this.classRef = featureDescriptor.classRef;
        this.addTable(featureDescriptor.table);
    }
    
    private void addTable(final Hashtable<String, Object> hashtable) {
        if (hashtable != null && !hashtable.isEmpty()) {
            this.getTable().putAll(hashtable);
        }
    }
    
    private Hashtable<String, Object> getTable() {
        if (this.table == null) {
            this.table = new Hashtable<String, Object>();
        }
        return this.table;
    }
    
    void setTransient(final Transient transient1) {
        if (transient1 != null && null == this.getValue("transient")) {
            this.setValue("transient", transient1.value());
        }
    }
    
    boolean isTransient() {
        final Object value = this.getValue("transient");
        return value instanceof Boolean && (boolean)value;
    }
    
    void setClass0(final Class<?> clazz) {
        this.classRef = getWeakReference(clazz);
    }
    
    Class<?> getClass0() {
        return (this.classRef != null) ? ((Class)this.classRef.get()) : null;
    }
    
    static <T> Reference<T> getSoftReference(final T t) {
        return (t != null) ? new SoftReference<T>(t) : null;
    }
    
    static <T> Reference<T> getWeakReference(final T t) {
        return (t != null) ? new WeakReference<T>(t) : null;
    }
    
    static Class<?> getReturnType(Class<?> declaringClass, final Method method) {
        if (declaringClass == null) {
            declaringClass = method.getDeclaringClass();
        }
        return TypeResolver.erase(TypeResolver.resolveInClass(declaringClass, method.getGenericReturnType()));
    }
    
    static Class<?>[] getParameterTypes(Class<?> declaringClass, final Method method) {
        if (declaringClass == null) {
            declaringClass = method.getDeclaringClass();
        }
        return TypeResolver.erase(TypeResolver.resolveInClass(declaringClass, method.getGenericParameterTypes()));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append("[name=").append(this.name);
        appendTo(sb, "displayName", this.displayName);
        appendTo(sb, "shortDescription", this.shortDescription);
        appendTo(sb, "preferred", this.preferred);
        appendTo(sb, "hidden", this.hidden);
        appendTo(sb, "expert", this.expert);
        if (this.table != null && !this.table.isEmpty()) {
            sb.append("; values={");
            for (final Map.Entry entry : this.table.entrySet()) {
                sb.append((String)entry.getKey()).append("=").append(entry.getValue()).append("; ");
            }
            sb.setLength(sb.length() - 2);
            sb.append("}");
        }
        this.appendTo(sb);
        return sb.append("]").toString();
    }
    
    void appendTo(final StringBuilder sb) {
    }
    
    static void appendTo(final StringBuilder sb, final String s, final Reference<?> reference) {
        if (reference != null) {
            appendTo(sb, s, reference.get());
        }
    }
    
    static void appendTo(final StringBuilder sb, final String s, final Object o) {
        if (o != null) {
            sb.append("; ").append(s).append("=").append(o);
        }
    }
    
    static void appendTo(final StringBuilder sb, final String s, final boolean b) {
        if (b) {
            sb.append("; ").append(s);
        }
    }
}
