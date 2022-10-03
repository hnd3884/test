package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.DescriptorType;
import java.util.Iterator;
import java.util.Set;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ServiceLocator;
import java.util.HashSet;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.PopulatorPostProcessor;

@PerLookup
public class DuplicatePostProcessor implements PopulatorPostProcessor
{
    private final DuplicatePostProcessorMode mode;
    private final HashSet<DescriptorImpl> strictDupSet;
    private final HashSet<ImplOnlyKey> implOnlyDupSet;
    
    public DuplicatePostProcessor() {
        this(DuplicatePostProcessorMode.STRICT);
    }
    
    public DuplicatePostProcessor(final DuplicatePostProcessorMode mode) {
        this.strictDupSet = new HashSet<DescriptorImpl>();
        this.implOnlyDupSet = new HashSet<ImplOnlyKey>();
        this.mode = mode;
    }
    
    public DuplicatePostProcessorMode getMode() {
        return this.mode;
    }
    
    @Override
    public DescriptorImpl process(final ServiceLocator serviceLocator, final DescriptorImpl descriptorImpl) {
        switch (this.mode) {
            case STRICT: {
                return this.strict(serviceLocator, descriptorImpl);
            }
            case IMPLEMENTATION_ONLY: {
                return this.implementationOnly(serviceLocator, descriptorImpl);
            }
            default: {
                throw new AssertionError((Object)("UnkownMode: " + this.mode));
            }
        }
    }
    
    private DescriptorImpl implementationOnly(final ServiceLocator serviceLocator, final DescriptorImpl descriptorImpl) {
        final String impl = descriptorImpl.getImplementation();
        if (impl == null) {
            return descriptorImpl;
        }
        final ImplOnlyKey key = new ImplOnlyKey((Descriptor)descriptorImpl);
        if (this.implOnlyDupSet.contains(key)) {
            return null;
        }
        this.implOnlyDupSet.add(key);
        if (serviceLocator.getBestDescriptor(new Filter() {
            @Override
            public boolean matches(final Descriptor d) {
                return d.getImplementation().equals(impl) && d.getDescriptorType().equals(descriptorImpl.getDescriptorType());
            }
        }) != null) {
            return null;
        }
        return descriptorImpl;
    }
    
    private DescriptorImpl strict(final ServiceLocator serviceLocator, final DescriptorImpl descriptorImpl) {
        if (this.strictDupSet.contains(descriptorImpl)) {
            return null;
        }
        this.strictDupSet.add(descriptorImpl);
        final Set<String> contracts = descriptorImpl.getAdvertisedContracts();
        String contract = null;
        for (final String candidate : contracts) {
            if (candidate.equals(descriptorImpl.getImplementation())) {
                contract = candidate;
                break;
            }
            contract = candidate;
        }
        final String fContract = contract;
        final String fName = descriptorImpl.getName();
        final DescriptorImpl fDescriptorImpl = descriptorImpl;
        if (serviceLocator.getBestDescriptor(new IndexedFilter() {
            @Override
            public boolean matches(final Descriptor d) {
                return fDescriptorImpl.equals(d);
            }
            
            @Override
            public String getAdvertisedContract() {
                return fContract;
            }
            
            @Override
            public String getName() {
                return fName;
            }
        }) != null) {
            return null;
        }
        return descriptorImpl;
    }
    
    @Override
    public String toString() {
        return "DuplicateCodeProcessor(" + this.mode + "," + System.identityHashCode(this) + ")";
    }
    
    private static final class ImplOnlyKey
    {
        private final String impl;
        private final DescriptorType type;
        private final int hash;
        
        private ImplOnlyKey(final Descriptor desc) {
            this.impl = desc.getImplementation();
            this.type = desc.getDescriptorType();
            this.hash = (this.impl.hashCode() ^ this.type.hashCode());
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ImplOnlyKey)) {
                return false;
            }
            final ImplOnlyKey other = (ImplOnlyKey)o;
            return other.impl.equals(this.impl) && other.type.equals(this.type);
        }
        
        @Override
        public String toString() {
            return "ImplOnlyKey(" + this.impl + "," + this.type + "," + System.identityHashCode(this) + ")";
        }
    }
}
