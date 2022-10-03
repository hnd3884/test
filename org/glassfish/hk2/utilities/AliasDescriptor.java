package org.glassfish.hk2.utilities;

import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Injectee;
import java.util.List;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashSet;
import org.glassfish.hk2.api.ServiceHandle;
import java.util.Map;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;

public class AliasDescriptor<T> extends AbstractActiveDescriptor<T>
{
    public static final String ALIAS_METADATA_MARKER = "__AliasOf";
    public static final String ALIAS_FREE_DESCRIPTOR = "FreeDescriptor";
    private static final long serialVersionUID = 2609895430798803508L;
    private ServiceLocator locator;
    private ActiveDescriptor<T> descriptor;
    private String contract;
    private Set<Annotation> qualifiers;
    private Set<String> qualifierNames;
    private boolean initialized;
    private static final Set<Type> EMPTY_CONTRACT_SET;
    private static final Set<Annotation> EMPTY_ANNOTATION_SET;
    
    public AliasDescriptor() {
        this.initialized = false;
    }
    
    public AliasDescriptor(final ServiceLocator locator, final ActiveDescriptor<T> descriptor, final String contract, final String name) {
        super(AliasDescriptor.EMPTY_CONTRACT_SET, null, name, AliasDescriptor.EMPTY_ANNOTATION_SET, descriptor.getDescriptorType(), descriptor.getDescriptorVisibility(), descriptor.getRanking(), descriptor.isProxiable(), descriptor.isProxyForSameScope(), descriptor.getClassAnalysisName(), descriptor.getMetadata());
        this.initialized = false;
        this.locator = locator;
        this.descriptor = descriptor;
        this.addAdvertisedContract(this.contract = contract);
        super.setScope(descriptor.getScope());
        super.addMetadata("__AliasOf", getAliasMetadataValue(descriptor));
    }
    
    private static String getAliasMetadataValue(final ActiveDescriptor<?> descriptor) {
        final Long locatorId = descriptor.getLocatorId();
        final Long serviceId = descriptor.getServiceId();
        if (locatorId == null || serviceId == null) {
            return "FreeDescriptor";
        }
        return locatorId + "." + serviceId;
    }
    
    @Override
    public Class<?> getImplementationClass() {
        this.ensureInitialized();
        return this.descriptor.getImplementationClass();
    }
    
    @Override
    public Type getImplementationType() {
        this.ensureInitialized();
        return this.descriptor.getImplementationType();
    }
    
    @Override
    public T create(final ServiceHandle<?> root) {
        this.ensureInitialized();
        return this.locator.getServiceHandle(this.descriptor).getService();
    }
    
    @Override
    public boolean isReified() {
        return true;
    }
    
    @Override
    public String getImplementation() {
        return this.descriptor.getImplementation();
    }
    
    @Override
    public Set<Type> getContractTypes() {
        this.ensureInitialized();
        return super.getContractTypes();
    }
    
    @Override
    public Class<? extends Annotation> getScopeAnnotation() {
        this.ensureInitialized();
        return this.descriptor.getScopeAnnotation();
    }
    
    @Override
    public synchronized Set<Annotation> getQualifierAnnotations() {
        this.ensureInitialized();
        if (this.qualifiers == null) {
            this.qualifiers = new HashSet<Annotation>(this.descriptor.getQualifierAnnotations());
            if (this.getName() != null) {
                this.qualifiers.add(new NamedImpl(this.getName()));
            }
        }
        return this.qualifiers;
    }
    
    @Override
    public synchronized Set<String> getQualifiers() {
        if (this.qualifierNames != null) {
            return this.qualifierNames;
        }
        this.qualifierNames = new HashSet<String>(this.descriptor.getQualifiers());
        if (this.getName() != null) {
            this.qualifierNames.add(Named.class.getName());
        }
        return this.qualifierNames;
    }
    
    @Override
    public List<Injectee> getInjectees() {
        this.ensureInitialized();
        return this.descriptor.getInjectees();
    }
    
    @Override
    public void dispose(final T instance) {
        this.ensureInitialized();
        this.descriptor.dispose(instance);
    }
    
    public ActiveDescriptor<T> getDescriptor() {
        return this.descriptor;
    }
    
    private synchronized void ensureInitialized() {
        if (!this.initialized) {
            if (!this.descriptor.isReified()) {
                this.descriptor = (ActiveDescriptor<T>)this.locator.reifyDescriptor(this.descriptor);
            }
            if (this.contract == null) {
                this.initialized = true;
                return;
            }
            final HK2Loader loader = this.descriptor.getLoader();
            Type contractType = null;
            try {
                if (loader != null) {
                    contractType = loader.loadClass(this.contract);
                }
                else {
                    final Class<?> ic = this.descriptor.getImplementationClass();
                    ClassLoader cl = null;
                    if (ic != null) {
                        cl = ic.getClassLoader();
                    }
                    if (cl == null) {
                        cl = ClassLoader.getSystemClassLoader();
                    }
                    contractType = cl.loadClass(this.contract);
                }
            }
            catch (final ClassNotFoundException ex) {}
            super.addContractType(contractType);
            this.initialized = true;
        }
    }
    
    @Override
    public int hashCode() {
        int retVal;
        synchronized (this) {
            retVal = this.descriptor.hashCode();
        }
        if (this.getName() != null) {
            retVal ^= this.getName().hashCode();
        }
        if (this.contract != null) {
            retVal ^= this.contract.hashCode();
        }
        return retVal;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof AliasDescriptor)) {
            return false;
        }
        final AliasDescriptor<?> other = (AliasDescriptor<?>)o;
        return other.descriptor.equals(this.descriptor) && GeneralUtilities.safeEquals((Object)other.getName(), (Object)this.getName()) && GeneralUtilities.safeEquals((Object)other.contract, (Object)this.contract);
    }
    
    static {
        EMPTY_CONTRACT_SET = new HashSet<Type>();
        EMPTY_ANNOTATION_SET = new HashSet<Annotation>();
    }
}
