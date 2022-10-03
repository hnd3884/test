package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.AnnotationLiteral;
import java.util.HashSet;
import org.glassfish.hk2.api.DuplicateServiceException;
import java.io.PrintStream;
import org.glassfish.hk2.api.Populator;
import java.io.IOException;
import java.util.Map;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.IndexedFilter;
import java.util.Set;
import org.glassfish.hk2.api.Filter;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Arrays;
import java.util.LinkedList;
import org.glassfish.hk2.api.FactoryDescriptors;
import java.util.List;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.internal.ImmediateHelper;
import org.glassfish.hk2.api.ImmediateController;
import org.glassfish.hk2.internal.InheritableThreadContext;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.internal.PerThreadContext;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.InheritableThread;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.api.PerLookup;
import javax.inject.Singleton;

public abstract class ServiceLocatorUtilities
{
    private static final String DEFAULT_LOCATOR_NAME = "default";
    private static final Singleton SINGLETON;
    private static final PerLookup PER_LOOKUP;
    private static final PerThread PER_THREAD;
    private static final InheritableThread INHERITABLE_THREAD;
    private static final Immediate IMMEDIATE;
    
    public static void enablePerThreadScope(final ServiceLocator locator) {
        try {
            addClasses(locator, true, PerThreadContext.class);
        }
        catch (final MultiException me) {
            if (!isDupException(me)) {
                throw me;
            }
        }
    }
    
    public static void enableInheritableThreadScope(final ServiceLocator locator) {
        try {
            addClasses(locator, true, InheritableThreadContext.class);
        }
        catch (final MultiException me) {
            if (!isDupException(me)) {
                throw me;
            }
        }
    }
    
    public static void enableImmediateScope(final ServiceLocator locator) {
        final ImmediateController controller = enableImmediateScopeSuspended(locator);
        controller.setImmediateState(ImmediateController.ImmediateServiceState.RUNNING);
    }
    
    public static ImmediateController enableImmediateScopeSuspended(final ServiceLocator locator) {
        try {
            addClasses(locator, true, ImmediateContext.class, ImmediateHelper.class);
        }
        catch (final MultiException me) {
            if (!isDupException(me)) {
                throw me;
            }
        }
        return locator.getService(ImmediateController.class, new Annotation[0]);
    }
    
    public static void bind(final ServiceLocator locator, final Binder... binders) {
        final DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class, new Annotation[0]);
        final DynamicConfiguration config = dcs.createDynamicConfiguration();
        for (final Binder binder : binders) {
            binder.bind(config);
        }
        config.commit();
    }
    
    public static ServiceLocator bind(final String name, final Binder... binders) {
        final ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
        final ServiceLocator locator = factory.create(name);
        bind(locator, binders);
        return locator;
    }
    
    public static ServiceLocator bind(final Binder... binders) {
        return bind("default", binders);
    }
    
    public static <T> ActiveDescriptor<T> addOneConstant(final ServiceLocator locator, final Object constant) {
        if (locator == null || constant == null) {
            throw new IllegalArgumentException();
        }
        return addOneDescriptor(locator, BuilderHelper.createConstantDescriptor(constant), false);
    }
    
    public static List<FactoryDescriptors> addFactoryConstants(final ServiceLocator locator, final Factory<?>... constants) {
        if (locator == null) {
            throw new IllegalArgumentException();
        }
        final DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class, new Annotation[0]);
        DynamicConfiguration cd = dcs.createDynamicConfiguration();
        final LinkedList<FactoryDescriptors> intermediateState = new LinkedList<FactoryDescriptors>();
        for (final Factory<?> factoryConstant : constants) {
            if (factoryConstant == null) {
                throw new IllegalArgumentException("One of the factories in " + Arrays.toString(constants) + " is null");
            }
            final FactoryDescriptors fds = cd.addActiveFactoryDescriptor((Class<? extends Factory<Object>>)factoryConstant.getClass());
            intermediateState.add(fds);
        }
        cd = dcs.createDynamicConfiguration();
        final LinkedList<FactoryDescriptors> retVal = new LinkedList<FactoryDescriptors>();
        int lcv = 0;
        for (final FactoryDescriptors fds2 : intermediateState) {
            final ActiveDescriptor<?> provideMethod = (ActiveDescriptor<?>)fds2.getFactoryAsAFactory();
            final Factory<?> constant = constants[lcv++];
            final Descriptor constantDescriptor = BuilderHelper.createConstantDescriptor(constant);
            final Descriptor addProvideMethod = new DescriptorImpl(provideMethod);
            final FactoryDescriptorsImpl fdi = new FactoryDescriptorsImpl(constantDescriptor, addProvideMethod);
            retVal.add(cd.bind(fdi));
        }
        cd.commit();
        return retVal;
    }
    
    public static <T> ActiveDescriptor<T> addOneConstant(final ServiceLocator locator, final Object constant, final String name, final Type... contracts) {
        if (locator == null || constant == null) {
            throw new IllegalArgumentException();
        }
        return addOneDescriptor(locator, BuilderHelper.createConstantDescriptor(constant, name, contracts), false);
    }
    
    public static <T> ActiveDescriptor<T> addOneDescriptor(final ServiceLocator locator, final Descriptor descriptor) {
        return addOneDescriptor(locator, descriptor, true);
    }
    
    public static <T> ActiveDescriptor<T> addOneDescriptor(final ServiceLocator locator, final Descriptor descriptor, final boolean requiresDeepCopy) {
        final DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class, new Annotation[0]);
        final DynamicConfiguration config = dcs.createDynamicConfiguration();
        ActiveDescriptor<T> retVal;
        if (descriptor instanceof ActiveDescriptor) {
            final ActiveDescriptor<T> active = (ActiveDescriptor<T>)descriptor;
            if (active.isReified()) {
                retVal = config.addActiveDescriptor(active, requiresDeepCopy);
            }
            else {
                retVal = config.bind(descriptor, requiresDeepCopy);
            }
        }
        else {
            retVal = config.bind(descriptor, requiresDeepCopy);
        }
        config.commit();
        return retVal;
    }
    
    public static List<FactoryDescriptors> addFactoryDescriptors(final ServiceLocator locator, final FactoryDescriptors... factories) {
        return addFactoryDescriptors(locator, true, factories);
    }
    
    public static List<FactoryDescriptors> addFactoryDescriptors(final ServiceLocator locator, final boolean requiresDeepCopy, final FactoryDescriptors... factories) {
        if (factories == null || locator == null) {
            throw new IllegalArgumentException();
        }
        final List<FactoryDescriptors> retVal = new ArrayList<FactoryDescriptors>(factories.length);
        final DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class, new Annotation[0]);
        final DynamicConfiguration config = dcs.createDynamicConfiguration();
        for (final FactoryDescriptors factory : factories) {
            final FactoryDescriptors addMe = config.bind(factory, requiresDeepCopy);
            retVal.add(addMe);
        }
        config.commit();
        return retVal;
    }
    
    public static List<ActiveDescriptor<?>> addClasses(final ServiceLocator locator, final boolean idempotent, final Class<?>... toAdd) {
        final DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class, new Annotation[0]);
        final DynamicConfiguration config = dcs.createDynamicConfiguration();
        final LinkedList<ActiveDescriptor<?>> retVal = new LinkedList<ActiveDescriptor<?>>();
        for (final Class<?> addMe : toAdd) {
            if (Factory.class.isAssignableFrom(addMe)) {
                final FactoryDescriptors fds = config.addActiveFactoryDescriptor((Class<? extends Factory<Object>>)addMe);
                if (idempotent) {
                    config.addIdempotentFilter(BuilderHelper.createDescriptorFilter(fds.getFactoryAsAService(), false));
                    config.addIdempotentFilter(BuilderHelper.createDescriptorFilter(fds.getFactoryAsAFactory(), false));
                }
                retVal.add((ActiveDescriptor)fds.getFactoryAsAService());
                retVal.add((ActiveDescriptor)fds.getFactoryAsAFactory());
            }
            else {
                final ActiveDescriptor<?> ad = config.addActiveDescriptor(addMe);
                if (idempotent) {
                    config.addIdempotentFilter(BuilderHelper.createDescriptorFilter(ad, false));
                }
                retVal.add(ad);
            }
        }
        config.commit();
        return retVal;
    }
    
    public static List<ActiveDescriptor<?>> addClasses(final ServiceLocator locator, final Class<?>... toAdd) {
        return addClasses(locator, false, toAdd);
    }
    
    static String getBestContract(final Descriptor d) {
        final String impl = d.getImplementation();
        final Set<String> contracts = d.getAdvertisedContracts();
        if (contracts.contains(impl)) {
            return impl;
        }
        final Iterator<String> iterator = contracts.iterator();
        if (iterator.hasNext()) {
            final String candidate = iterator.next();
            return candidate;
        }
        return impl;
    }
    
    public static <T> ActiveDescriptor<T> findOneDescriptor(final ServiceLocator locator, final Descriptor descriptor) {
        if (locator == null || descriptor == null) {
            throw new IllegalArgumentException();
        }
        if (descriptor.getServiceId() != null && descriptor.getLocatorId() != null) {
            final ActiveDescriptor<T> retVal = (ActiveDescriptor<T>)locator.getBestDescriptor(BuilderHelper.createSpecificDescriptorFilter(descriptor));
            if (retVal != null) {
                return retVal;
            }
        }
        DescriptorImpl di;
        if (descriptor instanceof DescriptorImpl) {
            di = (DescriptorImpl)descriptor;
        }
        else {
            di = new DescriptorImpl(descriptor);
        }
        final String contract = getBestContract(descriptor);
        final String name = descriptor.getName();
        final ActiveDescriptor<T> retVal2 = (ActiveDescriptor<T>)locator.getBestDescriptor(new IndexedFilter() {
            @Override
            public boolean matches(final Descriptor d) {
                return di.equals(d);
            }
            
            @Override
            public String getAdvertisedContract() {
                return contract;
            }
            
            @Override
            public String getName() {
                return name;
            }
        });
        return retVal2;
    }
    
    public static void removeOneDescriptor(final ServiceLocator locator, final Descriptor descriptor) {
        removeOneDescriptor(locator, descriptor, false);
    }
    
    public static void removeOneDescriptor(final ServiceLocator locator, final Descriptor descriptor, final boolean includeAliasDescriptors) {
        if (locator == null || descriptor == null) {
            throw new IllegalArgumentException();
        }
        final DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class, new Annotation[0]);
        final DynamicConfiguration config = dcs.createDynamicConfiguration();
        if (descriptor.getLocatorId() != null && descriptor.getServiceId() != null) {
            final Filter destructionFilter = BuilderHelper.createSpecificDescriptorFilter(descriptor);
            config.addUnbindFilter(destructionFilter);
            if (includeAliasDescriptors) {
                final List<ActiveDescriptor<?>> goingToDie = locator.getDescriptors(destructionFilter);
                if (!goingToDie.isEmpty()) {
                    final AliasFilter af = new AliasFilter((List)goingToDie);
                    config.addUnbindFilter(af);
                }
            }
            config.commit();
            return;
        }
        DescriptorImpl di;
        if (descriptor instanceof DescriptorImpl) {
            di = (DescriptorImpl)descriptor;
        }
        else {
            di = new DescriptorImpl(descriptor);
        }
        final Filter destructionFilter2 = new Filter() {
            @Override
            public boolean matches(final Descriptor d) {
                return di.equals(d);
            }
        };
        config.addUnbindFilter(destructionFilter2);
        if (includeAliasDescriptors) {
            final List<ActiveDescriptor<?>> goingToDie2 = locator.getDescriptors(destructionFilter2);
            if (!goingToDie2.isEmpty()) {
                final AliasFilter af2 = new AliasFilter((List)goingToDie2);
                config.addUnbindFilter(af2);
            }
        }
        config.commit();
    }
    
    public static void removeFilter(final ServiceLocator locator, final Filter filter) {
        removeFilter(locator, filter, false);
    }
    
    public static void removeFilter(final ServiceLocator locator, final Filter filter, final boolean includeAliasDescriptors) {
        if (locator == null || filter == null) {
            throw new IllegalArgumentException();
        }
        final DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class, new Annotation[0]);
        final DynamicConfiguration config = dcs.createDynamicConfiguration();
        config.addUnbindFilter(filter);
        if (includeAliasDescriptors) {
            final List<ActiveDescriptor<?>> goingToDie = locator.getDescriptors(filter);
            if (!goingToDie.isEmpty()) {
                final AliasFilter af = new AliasFilter((List)goingToDie);
                config.addUnbindFilter(af);
            }
        }
        config.commit();
    }
    
    public static <T> T getService(final ServiceLocator locator, final String className) {
        if (locator == null || className == null) {
            throw new IllegalArgumentException();
        }
        final ActiveDescriptor<T> ad = (ActiveDescriptor<T>)locator.getBestDescriptor(BuilderHelper.createContractFilter(className));
        if (ad == null) {
            return null;
        }
        return locator.getServiceHandle(ad).getService();
    }
    
    public static <T> T getService(final ServiceLocator locator, final Descriptor descriptor) {
        if (locator == null || descriptor == null) {
            throw new IllegalArgumentException();
        }
        final Long locatorId = descriptor.getLocatorId();
        if (locatorId != null && locatorId == locator.getLocatorId() && descriptor instanceof ActiveDescriptor) {
            return locator.getServiceHandle((ActiveDescriptor<T>)descriptor).getService();
        }
        final ActiveDescriptor<T> found = findOneDescriptor(locator, descriptor);
        if (found == null) {
            return null;
        }
        return locator.getServiceHandle(found).getService();
    }
    
    public static DynamicConfiguration createDynamicConfiguration(final ServiceLocator locator) throws IllegalStateException {
        if (locator == null) {
            throw new IllegalArgumentException();
        }
        final DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class, new Annotation[0]);
        if (dcs == null) {
            throw new IllegalStateException();
        }
        return dcs.createDynamicConfiguration();
    }
    
    public static <T> T findOrCreateService(final ServiceLocator locator, final Class<T> type, final Annotation... qualifiers) throws MultiException {
        if (locator == null || type == null) {
            throw new IllegalArgumentException();
        }
        final ServiceHandle<T> retVal = locator.getServiceHandle(type, qualifiers);
        if (retVal == null) {
            return locator.createAndInitialize(type);
        }
        return retVal.getService();
    }
    
    public static String getOneMetadataField(final Descriptor d, final String field) {
        final Map<String, List<String>> metadata = d.getMetadata();
        final List<String> values = metadata.get(field);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }
    
    public static String getOneMetadataField(final ServiceHandle<?> h, final String field) {
        return getOneMetadataField(h.getActiveDescriptor(), field);
    }
    
    public static ServiceLocator createAndPopulateServiceLocator(final String name) throws MultiException {
        final ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(name);
        final DynamicConfigurationService dcs = retVal.getService(DynamicConfigurationService.class, new Annotation[0]);
        final Populator populator = dcs.getPopulator();
        try {
            populator.populate();
        }
        catch (final IOException e) {
            throw new MultiException(e);
        }
        return retVal;
    }
    
    public static ServiceLocator createAndPopulateServiceLocator() {
        return createAndPopulateServiceLocator(null);
    }
    
    public static void enableLookupExceptions(final ServiceLocator locator) {
        if (locator == null) {
            throw new IllegalArgumentException();
        }
        try {
            addClasses(locator, true, RethrowErrorService.class);
        }
        catch (final MultiException me) {
            if (!isDupException(me)) {
                throw me;
            }
        }
    }
    
    public static void enableGreedyResolution(final ServiceLocator locator) {
        if (locator == null) {
            throw new IllegalArgumentException();
        }
        try {
            addClasses(locator, true, GreedyResolver.class);
        }
        catch (final MultiException me) {
            if (!isDupException(me)) {
                throw me;
            }
        }
    }
    
    @Deprecated
    public static void enableTopicDistribution(final ServiceLocator locator) {
        throw new AssertionError((Object)"ServiceLocatorUtilities.enableTopicDistribution method has been removed, use ExtrasUtilities.enableTopicDistribution");
    }
    
    public static void dumpAllDescriptors(final ServiceLocator locator) {
        dumpAllDescriptors(locator, System.err);
    }
    
    public static void dumpAllDescriptors(final ServiceLocator locator, final PrintStream output) {
        if (locator == null || output == null) {
            throw new IllegalArgumentException();
        }
        final List<ActiveDescriptor<?>> all = locator.getDescriptors(BuilderHelper.allFilter());
        for (final ActiveDescriptor<?> d : all) {
            output.println(d.toString());
        }
    }
    
    public static Singleton getSingletonAnnotation() {
        return ServiceLocatorUtilities.SINGLETON;
    }
    
    public static PerLookup getPerLookupAnnotation() {
        return ServiceLocatorUtilities.PER_LOOKUP;
    }
    
    public static PerThread getPerThreadAnnotation() {
        return ServiceLocatorUtilities.PER_THREAD;
    }
    
    public static InheritableThread getInheritableThreadAnnotation() {
        return ServiceLocatorUtilities.INHERITABLE_THREAD;
    }
    
    public static Immediate getImmediateAnnotation() {
        return ServiceLocatorUtilities.IMMEDIATE;
    }
    
    private static boolean isDupException(final MultiException me) {
        boolean atLeastOne = false;
        for (final Throwable error : me.getErrors()) {
            atLeastOne = true;
            if (!(error instanceof DuplicateServiceException)) {
                return false;
            }
        }
        return atLeastOne;
    }
    
    static {
        SINGLETON = (Singleton)new SingletonImpl();
        PER_LOOKUP = new PerLookupImpl();
        PER_THREAD = new PerThreadImpl();
        INHERITABLE_THREAD = new InheritableThreadImpl();
        IMMEDIATE = new ImmediateImpl();
    }
    
    private static class AliasFilter implements Filter
    {
        private final Set<String> values;
        
        private AliasFilter(final List<ActiveDescriptor<?>> bases) {
            this.values = new HashSet<String>();
            for (final ActiveDescriptor<?> base : bases) {
                final String val = base.getLocatorId() + "." + base.getServiceId();
                this.values.add(val);
            }
        }
        
        @Override
        public boolean matches(final Descriptor d) {
            final List<String> mAliasVals = d.getMetadata().get("__AliasOf");
            if (mAliasVals == null || mAliasVals.isEmpty()) {
                return false;
            }
            final String aliasVal = mAliasVals.get(0);
            return this.values.contains(aliasVal);
        }
    }
    
    private static class ImmediateImpl extends AnnotationLiteral<Immediate> implements Immediate
    {
        private static final long serialVersionUID = -4189466670823669605L;
    }
    
    private static class PerLookupImpl extends AnnotationLiteral<PerLookup> implements PerLookup
    {
        private static final long serialVersionUID = 6554011929159736762L;
    }
    
    private static class PerThreadImpl extends AnnotationLiteral<PerThread> implements PerThread
    {
        private static final long serialVersionUID = 521793185589873261L;
    }
    
    private static class InheritableThreadImpl extends AnnotationLiteral<InheritableThread> implements InheritableThread
    {
        private static final long serialVersionUID = -3955786566272090916L;
    }
    
    private static class SingletonImpl extends AnnotationLiteral<Singleton> implements Singleton
    {
        private static final long serialVersionUID = -2425625604832777314L;
    }
}
