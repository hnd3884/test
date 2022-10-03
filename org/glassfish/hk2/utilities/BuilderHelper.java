package org.glassfish.hk2.utilities;

import java.util.Collections;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import java.lang.reflect.Array;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Metadata;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.DescriptorType;
import java.util.Map;
import org.glassfish.hk2.internal.ConstantActiveDescriptor;
import org.jvnet.hk2.annotations.Service;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ProxyForSameScope;
import org.glassfish.hk2.api.UseProxy;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.HashMap;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.jvnet.hk2.annotations.Contract;
import java.lang.reflect.Type;
import java.util.HashSet;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.glassfish.hk2.internal.ActiveDescriptorBuilderImpl;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.internal.DescriptorBuilderImpl;
import org.glassfish.hk2.internal.StarFilter;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.internal.SpecificFilterImpl;
import java.util.Iterator;
import java.util.Collection;
import org.glassfish.hk2.api.Descriptor;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import org.glassfish.hk2.internal.IndexedFilterImpl;
import org.glassfish.hk2.api.IndexedFilter;

public class BuilderHelper
{
    public static final String NAME_KEY = "name";
    public static final String QUALIFIER_KEY = "qualifier";
    public static final String TOKEN_SEPARATOR = ";";
    
    public static IndexedFilter createContractFilter(final String contract) {
        return new IndexedFilterImpl(contract, null);
    }
    
    public static IndexedFilter createNameFilter(final String name) {
        return new IndexedFilterImpl(null, name);
    }
    
    public static IndexedFilter createNameAndContractFilter(final String contract, final String name) {
        return new IndexedFilterImpl(contract, name);
    }
    
    public static IndexedFilter createTokenizedFilter(final String tokenString) throws IllegalArgumentException {
        if (tokenString == null) {
            throw new IllegalArgumentException("null passed to createTokenizedFilter");
        }
        final StringTokenizer st = new StringTokenizer(tokenString, ";");
        String contract = null;
        String name = null;
        final Set<String> qualifiers = new LinkedHashSet<String>();
        boolean firstToken = true;
        if (tokenString.startsWith(";")) {
            firstToken = false;
        }
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (firstToken) {
                firstToken = false;
                if (token.length() <= 0) {
                    continue;
                }
                contract = token;
            }
            else {
                final int index = token.indexOf(61);
                if (index < 0) {
                    throw new IllegalArgumentException("No = character found in token " + token);
                }
                final String leftHandSide = token.substring(0, index);
                final String rightHandSide = token.substring(index + 1);
                if (rightHandSide.length() <= 0) {
                    throw new IllegalArgumentException("No value found in token " + token);
                }
                if ("name".equals(leftHandSide)) {
                    name = rightHandSide;
                }
                else {
                    if (!"qualifier".equals(leftHandSide)) {
                        throw new IllegalArgumentException("Unknown key: " + leftHandSide);
                    }
                    qualifiers.add(rightHandSide);
                }
            }
        }
        final String fContract = contract;
        final String fName = name;
        return new IndexedFilter() {
            @Override
            public boolean matches(final Descriptor d) {
                return qualifiers.isEmpty() || d.getQualifiers().containsAll(qualifiers);
            }
            
            @Override
            public String getAdvertisedContract() {
                return fContract;
            }
            
            @Override
            public String getName() {
                return fName;
            }
            
            @Override
            public String toString() {
                final String cField = (fContract == null) ? "" : fContract;
                final String nField = (fName == null) ? "" : (";name=" + fName);
                final StringBuffer sb = new StringBuffer();
                for (final String q : qualifiers) {
                    sb.append(";qualifier=" + q);
                }
                return "TokenizedFilter(" + cField + nField + sb.toString() + ")";
            }
        };
    }
    
    public static IndexedFilter createSpecificDescriptorFilter(final Descriptor descriptor) {
        final String contract = ServiceLocatorUtilities.getBestContract(descriptor);
        final String name = descriptor.getName();
        if (descriptor.getServiceId() == null) {
            throw new IllegalArgumentException("The descriptor must have a specific service ID");
        }
        if (descriptor.getLocatorId() == null) {
            throw new IllegalArgumentException("The descriptor must have a specific locator ID");
        }
        return new SpecificFilterImpl(contract, name, descriptor.getServiceId(), descriptor.getLocatorId());
    }
    
    public static IndexedFilter createDescriptorFilter(final Descriptor descriptorImpl, final boolean deepCopy) {
        final Descriptor filterDescriptor = deepCopy ? new DescriptorImpl(descriptorImpl) : descriptorImpl;
        return new IndexedFilter() {
            @Override
            public boolean matches(final Descriptor d) {
                return DescriptorImpl.descriptorEquals(filterDescriptor, d);
            }
            
            @Override
            public String getAdvertisedContract() {
                final Set<String> contracts = filterDescriptor.getAdvertisedContracts();
                if (contracts == null || contracts.isEmpty()) {
                    return null;
                }
                return contracts.iterator().next();
            }
            
            @Override
            public String getName() {
                return filterDescriptor.getName();
            }
        };
    }
    
    public static IndexedFilter createDescriptorFilter(final Descriptor descriptorImpl) {
        return createDescriptorFilter(descriptorImpl, true);
    }
    
    public static Filter allFilter() {
        return StarFilter.getDescriptorFilter();
    }
    
    public static DescriptorBuilder link(final String implementationClass, final boolean addToContracts) throws IllegalArgumentException {
        if (implementationClass == null) {
            throw new IllegalArgumentException();
        }
        return new DescriptorBuilderImpl(implementationClass, addToContracts);
    }
    
    public static DescriptorBuilder link(final String implementationClass) throws IllegalArgumentException {
        return link(implementationClass, true);
    }
    
    public static DescriptorBuilder link(final Class<?> implementationClass, final boolean addToContracts) throws IllegalArgumentException {
        if (implementationClass == null) {
            throw new IllegalArgumentException();
        }
        final DescriptorBuilder builder = link(implementationClass.getName(), addToContracts);
        return builder;
    }
    
    public static DescriptorBuilder link(final Class<?> implementationClass) throws IllegalArgumentException {
        if (implementationClass == null) {
            throw new IllegalArgumentException();
        }
        final boolean isFactory = Factory.class.isAssignableFrom(implementationClass);
        final DescriptorBuilder db = link(implementationClass, !isFactory);
        return db;
    }
    
    public static ActiveDescriptorBuilder activeLink(final Class<?> implementationClass) throws IllegalArgumentException {
        if (implementationClass == null) {
            throw new IllegalArgumentException();
        }
        return new ActiveDescriptorBuilderImpl(implementationClass);
    }
    
    public static <T> AbstractActiveDescriptor<T> createConstantDescriptor(final T constant) {
        if (constant == null) {
            throw new IllegalArgumentException();
        }
        final Class<?> cClass = constant.getClass();
        final ContractsProvided provided = cClass.getAnnotation(ContractsProvided.class);
        Set<Type> contracts;
        if (provided != null) {
            contracts = new HashSet<Type>();
            for (final Class<?> specified : provided.value()) {
                contracts.add(specified);
            }
        }
        else {
            contracts = ReflectionHelper.getAdvertisedTypesFromObject((Object)constant, (Class)Contract.class);
        }
        return createConstantDescriptor(constant, ReflectionHelper.getName((Class)constant.getClass()), (Type[])contracts.toArray(new Type[contracts.size()]));
    }
    
    public static int getRank(Class<?> fromClass) {
        while (fromClass != null && !Object.class.equals(fromClass)) {
            final Rank rank = fromClass.getAnnotation(Rank.class);
            if (rank != null) {
                return rank.value();
            }
            fromClass = fromClass.getSuperclass();
        }
        return 0;
    }
    
    public static <T> AbstractActiveDescriptor<T> createConstantDescriptor(final T constant, final String name, final Type... contracts) {
        if (constant == null) {
            throw new IllegalArgumentException();
        }
        final Annotation scope = ReflectionHelper.getScopeAnnotationFromObject((Object)constant);
        final Class<? extends Annotation> scopeClass = (scope == null) ? PerLookup.class : scope.annotationType();
        final Set<Annotation> qualifiers = ReflectionHelper.getQualifiersFromObject((Object)constant);
        final Map<String, List<String>> metadata = new HashMap<String, List<String>>();
        if (scope != null) {
            getMetadataValues(scope, metadata);
        }
        for (final Annotation qualifier : qualifiers) {
            getMetadataValues(qualifier, metadata);
        }
        Set<Type> contractsAsSet;
        if (contracts.length <= 0) {
            contractsAsSet = ReflectionHelper.getAdvertisedTypesFromObject((Object)constant, (Class)Contract.class);
        }
        else {
            contractsAsSet = new LinkedHashSet<Type>();
            for (final Type cType : contracts) {
                contractsAsSet.add(cType);
            }
        }
        Boolean proxy = null;
        final UseProxy up = constant.getClass().getAnnotation(UseProxy.class);
        if (up != null) {
            proxy = up.value();
        }
        Boolean proxyForSameScope = null;
        final ProxyForSameScope pfss = constant.getClass().getAnnotation(ProxyForSameScope.class);
        if (pfss != null) {
            proxyForSameScope = pfss.value();
        }
        DescriptorVisibility visibility = DescriptorVisibility.NORMAL;
        final Visibility vi = constant.getClass().getAnnotation(Visibility.class);
        if (vi != null) {
            visibility = vi.value();
        }
        String classAnalysisName = null;
        final Service service = constant.getClass().getAnnotation(Service.class);
        if (service != null) {
            classAnalysisName = service.analyzer();
        }
        final int rank = getRank(constant.getClass());
        return new ConstantActiveDescriptor<T>(constant, contractsAsSet, scopeClass, name, qualifiers, visibility, proxy, proxyForSameScope, classAnalysisName, metadata, rank);
    }
    
    public static DescriptorImpl createDescriptorFromClass(final Class<?> clazz) {
        if (clazz == null) {
            return new DescriptorImpl();
        }
        final Set<String> contracts = ReflectionHelper.getContractsFromClass((Class)clazz, (Class)Contract.class);
        final String name = ReflectionHelper.getName((Class)clazz);
        final String scope = ReflectionHelper.getScopeFromClass((Class)clazz, (Annotation)ServiceLocatorUtilities.getPerLookupAnnotation()).annotationType().getName();
        final Set<String> qualifiers = ReflectionHelper.getQualifiersFromClass((Class)clazz);
        DescriptorType type = DescriptorType.CLASS;
        if (Factory.class.isAssignableFrom(clazz)) {
            type = DescriptorType.PROVIDE_METHOD;
        }
        Boolean proxy = null;
        final UseProxy up = clazz.getAnnotation(UseProxy.class);
        if (up != null) {
            proxy = up.value();
        }
        Boolean proxyForSameScope = null;
        final ProxyForSameScope pfss = clazz.getAnnotation(ProxyForSameScope.class);
        if (pfss != null) {
            proxyForSameScope = pfss.value();
        }
        DescriptorVisibility visibility = DescriptorVisibility.NORMAL;
        final Visibility vi = clazz.getAnnotation(Visibility.class);
        if (vi != null) {
            visibility = vi.value();
        }
        final int rank = getRank(clazz);
        return new DescriptorImpl(contracts, name, scope, clazz.getName(), new HashMap<String, List<String>>(), qualifiers, type, visibility, null, rank, proxy, proxyForSameScope, null, null, null);
    }
    
    public static DescriptorImpl deepCopyDescriptor(final Descriptor copyMe) {
        return new DescriptorImpl(copyMe);
    }
    
    public static void getMetadataValues(final Annotation annotation, final Map<String, List<String>> metadata) {
        if (annotation == null || metadata == null) {
            throw new IllegalArgumentException();
        }
        final Class<? extends Annotation> annotationClass = annotation.annotationType();
        final Method[] array;
        final Method[] annotationMethods = array = AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction<Method[]>() {
            @Override
            public Method[] run() {
                return annotationClass.getDeclaredMethods();
            }
        });
        for (final Method annotationMethod : array) {
            final Metadata metadataAnno = annotationMethod.getAnnotation(Metadata.class);
            if (metadataAnno != null) {
                final String key = metadataAnno.value();
                Object addMe;
                try {
                    addMe = ReflectionHelper.invoke((Object)annotation, annotationMethod, new Object[0], false);
                }
                catch (final Throwable th) {
                    throw new MultiException(th);
                }
                if (addMe != null) {
                    String addMeString;
                    if (addMe instanceof Class) {
                        addMeString = ((Class)addMe).getName();
                    }
                    else if (addMe.getClass().isArray()) {
                        for (int length = Array.getLength(addMe), lcv = 0; lcv < length; ++lcv) {
                            final Object iValue = Array.get(addMe, lcv);
                            if (iValue != null) {
                                if (iValue instanceof Class) {
                                    final String cName = ((Class)iValue).getName();
                                    ReflectionHelper.addMetadata((Map)metadata, key, cName);
                                }
                                else {
                                    ReflectionHelper.addMetadata((Map)metadata, key, iValue.toString());
                                }
                            }
                        }
                        addMeString = null;
                    }
                    else {
                        addMeString = addMe.toString();
                    }
                    if (addMeString != null) {
                        ReflectionHelper.addMetadata((Map)metadata, key, addMeString);
                    }
                }
            }
        }
    }
    
    public static <T> ServiceHandle<T> createConstantServiceHandle(final T obj) {
        return new ServiceHandle<T>() {
            private Object serviceData;
            
            @Override
            public T getService() {
                return obj;
            }
            
            @Override
            public ActiveDescriptor<T> getActiveDescriptor() {
                return null;
            }
            
            @Override
            public boolean isActive() {
                return true;
            }
            
            @Override
            public void destroy() {
            }
            
            @Override
            public synchronized void setServiceData(final Object serviceData) {
                this.serviceData = serviceData;
            }
            
            @Override
            public synchronized Object getServiceData() {
                return this.serviceData;
            }
            
            @Override
            public List<ServiceHandle<?>> getSubHandles() {
                return Collections.emptyList();
            }
        };
    }
    
    public static boolean filterMatches(final Descriptor baseDescriptor, final Filter filter) {
        if (baseDescriptor == null) {
            throw new IllegalArgumentException();
        }
        if (filter == null) {
            return true;
        }
        if (filter instanceof IndexedFilter) {
            final IndexedFilter indexedFilter = (IndexedFilter)filter;
            final String indexContract = indexedFilter.getAdvertisedContract();
            if (indexContract != null && !baseDescriptor.getAdvertisedContracts().contains(indexContract)) {
                return false;
            }
            final String indexName = indexedFilter.getName();
            if (indexName != null) {
                if (baseDescriptor.getName() == null) {
                    return false;
                }
                if (!indexName.equals(baseDescriptor.getName())) {
                    return false;
                }
            }
        }
        return filter.matches(baseDescriptor);
    }
}
