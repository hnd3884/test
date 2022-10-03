package org.glassfish.jersey.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import org.glassfish.jersey.server.model.Resource;
import java.util.List;
import java.util.Set;

final class ResourceBag
{
    final Set<Class<?>> classes;
    final Set<Object> instances;
    final List<Resource> models;
    
    private ResourceBag(final Set<Class<?>> classes, final Set<Object> instances, final List<Resource> models) {
        this.classes = classes;
        this.instances = instances;
        this.models = models;
    }
    
    List<Resource> getRootResources() {
        final List<Resource> rootResources = new ArrayList<Resource>();
        for (final Resource resource : this.models) {
            if (resource.getPath() != null) {
                rootResources.add(resource);
            }
        }
        return rootResources;
    }
    
    public static final class Builder
    {
        private final Set<Class<?>> classes;
        private final Set<Object> instances;
        private final List<Resource> models;
        private final Map<String, Resource> rootResourceMap;
        
        public Builder() {
            this.classes = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
            this.instances = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
            this.models = new LinkedList<Resource>();
            this.rootResourceMap = new HashMap<String, Resource>();
        }
        
        void registerResource(final Class<?> resourceClass, final Resource resourceModel) {
            this.registerModel(resourceModel);
            this.classes.add(resourceClass);
        }
        
        void registerResource(final Object resourceInstance, final Resource resourceModel) {
            this.registerModel(resourceModel);
            this.instances.add(resourceInstance);
        }
        
        void registerProgrammaticResource(final Resource resourceModel) {
            this.registerModel(resourceModel);
            this.classes.addAll(resourceModel.getHandlerClasses());
            this.instances.addAll(resourceModel.getHandlerInstances());
        }
        
        private void registerModel(final Resource resourceModel) {
            final String path = resourceModel.getPath();
            if (path != null) {
                Resource existing = this.rootResourceMap.get(path);
                if (existing != null) {
                    existing = Resource.builder(existing).mergeWith(resourceModel).build();
                    this.rootResourceMap.put(path, existing);
                }
                else {
                    this.rootResourceMap.put(path, resourceModel);
                }
            }
            else {
                this.models.add(resourceModel);
            }
        }
        
        ResourceBag build() {
            this.models.addAll(this.rootResourceMap.values());
            return new ResourceBag(this.classes, this.instances, this.models, null);
        }
    }
}
