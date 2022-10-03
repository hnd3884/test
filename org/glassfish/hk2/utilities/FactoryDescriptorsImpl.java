package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.FactoryDescriptors;

public class FactoryDescriptorsImpl implements FactoryDescriptors
{
    private final Descriptor asService;
    private final Descriptor asProvideMethod;
    
    public FactoryDescriptorsImpl(final Descriptor asService, final Descriptor asProvideMethod) {
        if (asService == null || asProvideMethod == null) {
            throw new IllegalArgumentException();
        }
        if (!DescriptorType.CLASS.equals(asService.getDescriptorType())) {
            throw new IllegalArgumentException("Creation of FactoryDescriptors must have first argument of type CLASS");
        }
        if (!asService.getAdvertisedContracts().contains(Factory.class.getName())) {
            throw new IllegalArgumentException("Creation of FactoryDescriptors must have Factory as a contract of the first argument");
        }
        if (!DescriptorType.PROVIDE_METHOD.equals(asProvideMethod.getDescriptorType())) {
            throw new IllegalArgumentException("Creation of FactoryDescriptors must have second argument of type PROVIDE_METHOD");
        }
        this.asService = asService;
        this.asProvideMethod = asProvideMethod;
    }
    
    @Override
    public Descriptor getFactoryAsAService() {
        return this.asService;
    }
    
    @Override
    public Descriptor getFactoryAsAFactory() {
        return this.asProvideMethod;
    }
    
    @Override
    public int hashCode() {
        return this.asService.hashCode() ^ this.asProvideMethod.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof FactoryDescriptors)) {
            return false;
        }
        final FactoryDescriptors other = (FactoryDescriptors)o;
        final Descriptor otherService = other.getFactoryAsAService();
        final Descriptor otherFactory = other.getFactoryAsAFactory();
        return otherService != null && otherFactory != null && this.asService.equals(otherService) && this.asProvideMethod.equals(otherFactory);
    }
    
    @Override
    public String toString() {
        return "FactoryDescriptorsImpl(\n" + this.asService + ",\n" + this.asProvideMethod + ",\n\t" + System.identityHashCode(this) + ")";
    }
}
