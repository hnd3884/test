package org.glassfish.jersey.server.model;

import java.util.HashSet;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.Severity;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.glassfish.jersey.uri.PathPattern;
import org.glassfish.jersey.internal.util.collection.Values;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import org.glassfish.jersey.server.model.internal.ModelHelper;
import javax.ws.rs.Path;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.List;
import org.glassfish.jersey.internal.util.collection.Value;

public final class Resource implements Routed, ResourceModelComponent
{
    private final Resource parent;
    private final Data data;
    private final Value<String> name;
    private final List<ResourceMethod> resourceMethods;
    private final ResourceMethod locator;
    private final List<Resource> childResources;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder builder(final String path) {
        return new Builder(path);
    }
    
    public static Builder builder(final List<Resource> resources) {
        if (resources == null || resources.isEmpty()) {
            return builder();
        }
        final Iterator<Resource> it = resources.iterator();
        Data resourceData = it.next().data;
        final Builder builder = builder(resourceData);
        final String path = resourceData.path;
        while (it.hasNext()) {
            resourceData = it.next().data;
            if ((resourceData.path != null || path != null) && (path == null || !path.equals(resourceData.path))) {
                throw new IllegalArgumentException(LocalizationMessages.ERROR_RESOURCES_CANNOT_MERGE());
            }
            builder.mergeWith(resourceData);
        }
        return builder;
    }
    
    public static Builder builder(final Class<?> resourceClass) {
        return builder(resourceClass, false);
    }
    
    public static Builder builder(final Class<?> resourceClass, final boolean disableValidation) {
        final Builder builder = new IntrospectionModeller(resourceClass, disableValidation).createResourceBuilder();
        return builder.isEmpty() ? null : builder;
    }
    
    public static Resource from(final Class<?> resourceClass) {
        return from(resourceClass, false);
    }
    
    public static Resource from(final Class<?> resourceClass, final boolean disableValidation) {
        final Builder builder = new IntrospectionModeller(resourceClass, disableValidation).createResourceBuilder();
        return builder.isEmpty() ? null : builder.build();
    }
    
    public static boolean isAcceptable(final Class<?> c) {
        return (c.getModifiers() & 0x400) == 0x0 && !c.isPrimitive() && !c.isAnnotation() && !c.isInterface() && !c.isLocalClass() && (!c.isMemberClass() || (c.getModifiers() & 0x8) != 0x0);
    }
    
    public static Path getPath(final Class<?> resourceClass) {
        return ModelHelper.getAnnotatedResourceClass(resourceClass).getAnnotation(Path.class);
    }
    
    public static Builder builder(final Resource resource) {
        return builder(resource.data);
    }
    
    private static Builder builder(final Data resourceData) {
        Builder b;
        if (resourceData.path == null) {
            b = new Builder();
        }
        else {
            b = new Builder(resourceData.path);
        }
        b.resourceMethods.addAll(resourceData.resourceMethods);
        b.childResources.addAll(resourceData.childResources);
        b.subResourceLocator = resourceData.subResourceLocator;
        b.handlerClasses.addAll(resourceData.handlerClasses);
        b.handlerInstances.addAll(resourceData.handlerInstances);
        b.names.addAll(resourceData.names);
        return b;
    }
    
    private static List<Resource> transform(final Resource parent, final List<Data> list) {
        return list.stream().map(data -> new Resource(parent, data)).collect((Collector<? super Object, ?, List<Resource>>)Collectors.toList());
    }
    
    private static <T> List<T> immutableCopy(final List<T> list) {
        return list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList((List<? extends T>)list);
    }
    
    private static <T> Set<T> immutableCopy(final Set<T> set) {
        if (set.isEmpty()) {
            return Collections.emptySet();
        }
        final Set<T> result = Collections.newSetFromMap(new IdentityHashMap<T, Boolean>());
        result.addAll((Collection<? extends T>)set);
        return set;
    }
    
    private Resource(final Resource parent, final Data data) {
        this.parent = parent;
        this.data = data;
        this.name = (Value<String>)Values.lazy((Value)new Value<String>() {
            public String get() {
                if (data.names.size() == 1) {
                    return data.names.get(0);
                }
                return "Merge of " + data.names.toString();
            }
        });
        this.resourceMethods = immutableCopy(ResourceMethod.transform(this, data.resourceMethods));
        this.locator = ((data.subResourceLocator == null) ? null : new ResourceMethod(this, data.subResourceLocator));
        this.childResources = immutableCopy(transform(this, data.childResources));
    }
    
    @Override
    public String getPath() {
        return this.data.path;
    }
    
    @Override
    public PathPattern getPathPattern() {
        return this.data.pathPattern;
    }
    
    public Resource getParent() {
        return this.parent;
    }
    
    public String getName() {
        return (String)this.name.get();
    }
    
    public List<String> getNames() {
        return this.data.names;
    }
    
    public List<ResourceMethod> getResourceMethods() {
        return this.resourceMethods;
    }
    
    public ResourceMethod getResourceLocator() {
        return this.locator;
    }
    
    public List<ResourceMethod> getAllMethods() {
        final LinkedList<ResourceMethod> methodsAndLocators = new LinkedList<ResourceMethod>(this.getResourceMethods());
        final ResourceMethod loc = this.getResourceLocator();
        if (loc != null) {
            methodsAndLocators.add(loc);
        }
        return methodsAndLocators;
    }
    
    public List<Resource> getChildResources() {
        return this.childResources;
    }
    
    public Set<Class<?>> getHandlerClasses() {
        return this.data.handlerClasses;
    }
    
    public Set<Object> getHandlerInstances() {
        return this.data.handlerInstances;
    }
    
    @Override
    public void accept(final ResourceModelVisitor visitor) {
        if (this.getParent() == null) {
            visitor.visitResource(this);
        }
        else {
            visitor.visitChildResource(this);
        }
    }
    
    public boolean isExtended() {
        return this.data.extended;
    }
    
    @Override
    public String toString() {
        return this.data.toString();
    }
    
    @Override
    public List<? extends ResourceModelComponent> getComponents() {
        final List<ResourceModelComponent> components = new LinkedList<ResourceModelComponent>();
        components.addAll(this.getChildResources());
        components.addAll(this.getResourceMethods());
        final ResourceMethod resourceLocator = this.getResourceLocator();
        if (resourceLocator != null) {
            components.add(resourceLocator);
        }
        return components;
    }
    
    private static class Data
    {
        private final List<String> names;
        private final String path;
        private final PathPattern pathPattern;
        private final List<ResourceMethod.Data> resourceMethods;
        private final ResourceMethod.Data subResourceLocator;
        private final List<Data> childResources;
        private final Set<Class<?>> handlerClasses;
        private final Set<Object> handlerInstances;
        private final boolean extended;
        
        private Data(final List<String> names, final String path, final List<ResourceMethod.Data> resourceMethods, final ResourceMethod.Data subResourceLocator, final List<Data> childResources, final Set<Class<?>> handlerClasses, final Set<Object> handlerInstances, final boolean extended) {
            this.extended = extended;
            this.names = (List<String>)immutableCopy((List<Object>)names);
            this.path = path;
            this.pathPattern = ((path == null || path.isEmpty()) ? PathPattern.OPEN_ROOT_PATH_PATTERN : new PathPattern(path, PathPattern.RightHandPath.capturingZeroOrMoreSegments));
            this.resourceMethods = (List<ResourceMethod.Data>)immutableCopy((List<Object>)resourceMethods);
            this.subResourceLocator = subResourceLocator;
            this.childResources = Collections.unmodifiableList((List<? extends Data>)childResources);
            this.handlerClasses = (Set<Class<?>>)immutableCopy((Set<Object>)handlerClasses);
            this.handlerInstances = immutableCopy(handlerInstances);
        }
        
        @Override
        public String toString() {
            return "Resource{" + ((this.path == null) ? "[unbound], " : ("\"" + this.path + "\", ")) + this.childResources.size() + " child resources, " + this.resourceMethods.size() + " resource methods, " + ((this.subResourceLocator == null) ? "0" : "1") + " sub-resource locator, " + this.handlerClasses.size() + " method handler classes, " + this.handlerInstances.size() + " method handler instances" + '}';
        }
    }
    
    public static final class Builder
    {
        private List<String> names;
        private String path;
        private final Set<ResourceMethod.Builder> methodBuilders;
        private final Set<Builder> childResourceBuilders;
        private final List<Data> childResources;
        private final List<ResourceMethod.Data> resourceMethods;
        private ResourceMethod.Data subResourceLocator;
        private final Set<Class<?>> handlerClasses;
        private final Set<Object> handlerInstances;
        private final Builder parentResource;
        private boolean extended;
        
        private Builder(final Builder parentResource) {
            this.methodBuilders = new LinkedHashSet<ResourceMethod.Builder>();
            this.childResourceBuilders = new LinkedHashSet<Builder>();
            this.childResources = new LinkedList<Data>();
            this.resourceMethods = new LinkedList<ResourceMethod.Data>();
            this.handlerClasses = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
            this.handlerInstances = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
            this.parentResource = parentResource;
            this.name("[unnamed]");
        }
        
        private Builder(final String path) {
            this((Builder)null);
            this.path(path);
        }
        
        private Builder(final String path, final Builder parentResource) {
            this(parentResource);
            this.path = path;
        }
        
        private Builder() {
            this((Builder)null);
        }
        
        private boolean isEmpty() {
            return this.path == null && this.methodBuilders.isEmpty() && this.childResourceBuilders.isEmpty() && this.resourceMethods.isEmpty() && this.childResources.isEmpty() && this.subResourceLocator == null;
        }
        
        public Builder name(final String name) {
            (this.names = new ArrayList<String>()).add(name);
            return this;
        }
        
        public Builder path(final String path) {
            this.path = path;
            return this;
        }
        
        public ResourceMethod.Builder addMethod(final String httpMethod) {
            final ResourceMethod.Builder builder = new ResourceMethod.Builder(this);
            this.methodBuilders.add(builder);
            return builder.httpMethod(httpMethod);
        }
        
        public ResourceMethod.Builder addMethod() {
            final ResourceMethod.Builder builder = new ResourceMethod.Builder(this);
            this.methodBuilders.add(builder);
            return builder;
        }
        
        public ResourceMethod.Builder addMethod(final ResourceMethod resourceMethod) {
            final ResourceMethod.Builder builder = new ResourceMethod.Builder(this, resourceMethod);
            this.methodBuilders.add(builder);
            return builder;
        }
        
        public ResourceMethod.Builder updateMethod(final ResourceMethod resourceMethod) {
            final boolean removed = this.resourceMethods.remove(resourceMethod.getData());
            if (!removed) {
                throw new IllegalArgumentException(LocalizationMessages.RESOURCE_UPDATED_METHOD_DOES_NOT_EXIST(resourceMethod.toString()));
            }
            final ResourceMethod.Builder builder = new ResourceMethod.Builder(this, resourceMethod);
            this.methodBuilders.add(builder);
            return builder;
        }
        
        public Builder addChildResource(final String relativePath) {
            if (this.parentResource != null) {
                throw new IllegalStateException(LocalizationMessages.RESOURCE_ADD_CHILD_ALREADY_CHILD());
            }
            final Builder resourceBuilder = new Builder(relativePath, this);
            this.childResourceBuilders.add(resourceBuilder);
            return resourceBuilder;
        }
        
        public void addChildResource(final Resource resource) {
            this.childResources.add(resource.data);
        }
        
        public void replaceChildResource(final Resource replacedResource, final Resource newResource) {
            final boolean removed = this.childResources.remove(replacedResource.data);
            if (!removed) {
                throw new IllegalArgumentException(LocalizationMessages.RESOURCE_REPLACED_CHILD_DOES_NOT_EXIST(replacedResource.toString()));
            }
            this.addChildResource(newResource);
        }
        
        public Builder mergeWith(final Resource resource) {
            this.mergeWith(resource.data);
            return this;
        }
        
        public Builder extended(final boolean extended) {
            this.extended = extended;
            return this;
        }
        
        boolean isExtended() {
            return this.extended;
        }
        
        private Builder mergeWith(final Data resourceData) {
            this.resourceMethods.addAll(resourceData.resourceMethods);
            this.childResources.addAll(resourceData.childResources);
            if (this.subResourceLocator != null && resourceData.subResourceLocator != null) {
                Errors.processWithException((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        Errors.error((Object)this, LocalizationMessages.RESOURCE_MERGE_CONFLICT_LOCATORS(Builder.this, resourceData, Builder.this.path), Severity.FATAL);
                    }
                });
            }
            else if (resourceData.subResourceLocator != null) {
                this.subResourceLocator = resourceData.subResourceLocator;
            }
            this.handlerClasses.addAll(resourceData.handlerClasses);
            this.handlerInstances.addAll(resourceData.handlerInstances);
            this.names.addAll(resourceData.names);
            return this;
        }
        
        public Builder mergeWith(final Builder resourceBuilder) {
            resourceBuilder.processMethodBuilders();
            this.resourceMethods.addAll(resourceBuilder.resourceMethods);
            this.childResources.addAll(resourceBuilder.childResources);
            if (this.subResourceLocator != null && resourceBuilder.subResourceLocator != null) {
                Errors.processWithException((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        Errors.warning((Object)this, LocalizationMessages.RESOURCE_MERGE_CONFLICT_LOCATORS(Builder.this, resourceBuilder, Builder.this.path));
                    }
                });
            }
            else if (resourceBuilder.subResourceLocator != null) {
                this.subResourceLocator = resourceBuilder.subResourceLocator;
            }
            this.handlerClasses.addAll(resourceBuilder.handlerClasses);
            this.handlerInstances.addAll(resourceBuilder.handlerInstances);
            this.names.addAll(resourceBuilder.names);
            return this;
        }
        
        void onBuildMethod(final ResourceMethod.Builder builder, final ResourceMethod.Data methodData) {
            Preconditions.checkState(this.methodBuilders.remove(builder), (Object)"Resource.Builder.onBuildMethod() invoked from a resource method builder that is not registered in the resource builder instance.");
            switch (methodData.getType()) {
                case RESOURCE_METHOD: {
                    this.resourceMethods.add(methodData);
                    break;
                }
                case SUB_RESOURCE_LOCATOR: {
                    if (this.subResourceLocator != null) {
                        Errors.processWithException((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                Errors.error((Object)this, LocalizationMessages.AMBIGUOUS_SRLS(this, Builder.this.path), Severity.FATAL);
                            }
                        });
                    }
                    this.subResourceLocator = methodData;
                    break;
                }
            }
            final MethodHandler methodHandler = methodData.getInvocable().getHandler();
            if (methodHandler.isClassBased()) {
                this.handlerClasses.add(methodHandler.getHandlerClass());
            }
            else {
                this.handlerInstances.add(methodHandler.getHandlerInstance());
            }
        }
        
        private void onBuildChildResource(final Builder childResourceBuilder, final Data childResourceData) {
            Preconditions.checkState(this.childResourceBuilders.remove(childResourceBuilder), (Object)"Resource.Builder.onBuildChildResource() invoked from a resource builder that is not registered in the resource builder instance as a child resource builder.");
            this.childResources.add(childResourceData);
        }
        
        private List<Data> mergeResources(final List<Data> resources) {
            final List<Data> mergedResources = new ArrayList<Data>();
            for (int i = 0; i < resources.size(); ++i) {
                final Data outer = resources.get(i);
                Builder builder = null;
                for (int j = i + 1; j < resources.size(); ++j) {
                    final Data inner = resources.get(j);
                    if (outer.path.equals(inner.path)) {
                        if (builder == null) {
                            builder = builder(outer);
                        }
                        builder.mergeWith(inner);
                        resources.remove(j);
                        --j;
                    }
                }
                if (builder == null) {
                    mergedResources.add(outer);
                }
                else {
                    mergedResources.add(builder.buildResourceData());
                }
            }
            return mergedResources;
        }
        
        private Data buildResourceData() {
            if (this.parentResource != null && this.parentResource.isExtended()) {
                this.extended = true;
            }
            this.processMethodBuilders();
            this.processChildResourceBuilders();
            final List<Data> mergedChildResources = this.mergeResources(this.childResources);
            final Set<Class<?>> classes = new HashSet<Class<?>>(this.handlerClasses);
            final Set<Object> instances = new HashSet<Object>(this.handlerInstances);
            for (final Data childResource : mergedChildResources) {
                classes.addAll(childResource.handlerClasses);
                instances.addAll(childResource.handlerInstances);
            }
            if (this.areAllMembersExtended(mergedChildResources)) {
                this.extended = true;
            }
            final Data resourceData = new Data((List)this.names, this.path, (List)this.resourceMethods, this.subResourceLocator, (List)mergedChildResources, (Set)classes, (Set)instances, this.extended);
            if (this.parentResource != null) {
                this.parentResource.onBuildChildResource(this, resourceData);
            }
            return resourceData;
        }
        
        private boolean areAllMembersExtended(final List<Data> mergedChildResources) {
            boolean allExtended = true;
            for (final ResourceMethod.Data resourceMethod : this.resourceMethods) {
                if (!resourceMethod.isExtended()) {
                    allExtended = false;
                }
            }
            if (this.subResourceLocator != null && !this.subResourceLocator.isExtended()) {
                allExtended = false;
            }
            for (final Data childResource : mergedChildResources) {
                if (!childResource.extended) {
                    allExtended = false;
                }
            }
            return allExtended;
        }
        
        public Resource build() {
            final Data resourceData = this.buildResourceData();
            return new Resource(null, resourceData, null);
        }
        
        private void processMethodBuilders() {
            while (!this.methodBuilders.isEmpty()) {
                this.methodBuilders.iterator().next().build();
            }
        }
        
        private void processChildResourceBuilders() {
            while (!this.childResourceBuilders.isEmpty()) {
                this.childResourceBuilders.iterator().next().build();
            }
        }
        
        @Override
        public String toString() {
            return "Builder{names=" + this.names + ", path='" + this.path + '\'' + ", methodBuilders=" + this.methodBuilders + ", childResourceBuilders=" + this.childResourceBuilders + ", childResources=" + this.childResources + ", resourceMethods=" + this.resourceMethods + ", subResourceLocator=" + this.subResourceLocator + ", handlerClasses=" + this.handlerClasses + ", handlerInstances=" + this.handlerInstances + ", parentResource=" + ((this.parentResource == null) ? "<no parent>" : this.parentResource.shortToString()) + ", extended=" + this.extended + '}';
        }
        
        private String shortToString() {
            return "Builder{names=" + this.names + ", path='" + this.path + "'}";
        }
    }
}
