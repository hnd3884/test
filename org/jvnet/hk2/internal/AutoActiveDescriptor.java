package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorType;
import java.util.List;
import java.util.Map;
import org.glassfish.hk2.api.DescriptorVisibility;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

public class AutoActiveDescriptor<T> extends AbstractActiveDescriptor<T>
{
    private static final long serialVersionUID = -7921574114250721537L;
    private Class<?> implClass;
    private Creator<T> creator;
    private SystemDescriptor<?> hk2Parent;
    private Type implType;
    
    public AutoActiveDescriptor() {
    }
    
    public AutoActiveDescriptor(final Class<?> clazz, final Creator<T> creator, final Set<Type> advertisedContracts, final Class<? extends Annotation> scope, final String name, final Set<Annotation> qualifiers, final DescriptorVisibility descriptorVisibility, final int ranking, final Boolean proxy, final Boolean proxyForSameScope, final String classAnalysisName, final Map<String, List<String>> metadata, final DescriptorType descriptorType, final Type clazzType) {
        super((Set)advertisedContracts, (Class)scope, name, (Set)qualifiers, DescriptorType.CLASS, descriptorVisibility, ranking, proxy, proxyForSameScope, classAnalysisName, (Map)metadata);
        this.implClass = clazz;
        this.creator = creator;
        this.setImplementation(this.implClass.getName());
        this.setDescriptorType(descriptorType);
        if (clazzType == null) {
            this.implType = clazz;
        }
        else {
            this.implType = clazzType;
        }
    }
    
    void resetSelfDescriptor(final ActiveDescriptor<?> toMe) {
        if (!(this.creator instanceof ClazzCreator)) {
            return;
        }
        final ClazzCreator<?> cc = (ClazzCreator)this.creator;
        cc.resetSelfDescriptor(toMe);
    }
    
    void setHK2Parent(final SystemDescriptor<?> hk2Parent) {
        this.hk2Parent = hk2Parent;
    }
    
    public Class<?> getImplementationClass() {
        return this.implClass;
    }
    
    public Type getImplementationType() {
        return this.implType;
    }
    
    public void setImplementationType(final Type t) {
        this.implType = t;
    }
    
    public T create(final ServiceHandle<?> root) {
        return this.creator.create(root, this.hk2Parent);
    }
    
    public void dispose(final T instance) {
        this.creator.dispose(instance);
    }
    
    public List<Injectee> getInjectees() {
        return this.creator.getInjectees();
    }
}
