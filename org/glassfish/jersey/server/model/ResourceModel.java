package org.glassfish.jersey.server.model;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.internal.util.collection.Value;
import java.util.List;

public class ResourceModel implements ResourceModelComponent
{
    private final List<Resource> rootResources;
    private final List<Resource> resources;
    private final Value<RuntimeResourceModel> runtimeRootResourceModelValue;
    
    private ResourceModel(final List<Resource> rootResources, final List<Resource> allResources) {
        this.resources = allResources;
        this.rootResources = rootResources;
        this.runtimeRootResourceModelValue = (Value<RuntimeResourceModel>)Values.lazy((Value)new Value<RuntimeResourceModel>() {
            public RuntimeResourceModel get() {
                return new RuntimeResourceModel(ResourceModel.this.resources);
            }
        });
    }
    
    public List<Resource> getRootResources() {
        return this.rootResources;
    }
    
    public List<Resource> getResources() {
        return this.resources;
    }
    
    @Override
    public void accept(final ResourceModelVisitor visitor) {
        visitor.visitResourceModel(this);
    }
    
    @Override
    public List<? extends ResourceModelComponent> getComponents() {
        final List<ResourceModelComponent> components = new ArrayList<ResourceModelComponent>();
        components.addAll(this.resources);
        components.addAll(this.getRuntimeResourceModel().getRuntimeResources());
        return components;
    }
    
    public RuntimeResourceModel getRuntimeResourceModel() {
        return (RuntimeResourceModel)this.runtimeRootResourceModelValue.get();
    }
    
    public static class Builder
    {
        private final List<Resource> resources;
        private final boolean subResourceModel;
        
        public Builder(final ResourceModel resourceModel, final boolean subResourceModel) {
            this.resources = resourceModel.getResources();
            this.subResourceModel = subResourceModel;
        }
        
        public Builder(final List<Resource> resources, final boolean subResourceModel) {
            this.resources = resources;
            this.subResourceModel = subResourceModel;
        }
        
        public Builder(final boolean subResourceModel) {
            this.resources = new ArrayList<Resource>();
            this.subResourceModel = subResourceModel;
        }
        
        public Builder addResource(final Resource resource) {
            this.resources.add(resource);
            return this;
        }
        
        public ResourceModel build() {
            final Map<String, Resource> resourceMap = new LinkedHashMap<String, Resource>();
            final Set<Resource> separateResources = Collections.newSetFromMap(new IdentityHashMap<Resource, Boolean>());
            for (final Resource resource : this.resources) {
                final String path = resource.getPath();
                if (path == null && !this.subResourceModel) {
                    separateResources.add(resource);
                }
                else {
                    final Resource fromMap = resourceMap.get(path);
                    if (fromMap == null) {
                        resourceMap.put(path, resource);
                    }
                    else {
                        resourceMap.put(path, Resource.builder(fromMap).mergeWith(resource).build());
                    }
                }
            }
            final List<Resource> rootResources = new ArrayList<Resource>();
            final List<Resource> allResources = new ArrayList<Resource>();
            for (final Map.Entry<String, Resource> entry : resourceMap.entrySet()) {
                if (entry.getKey() != null) {
                    rootResources.add(entry.getValue());
                }
                allResources.add(entry.getValue());
            }
            if (!this.subResourceModel) {
                allResources.addAll(separateResources);
            }
            return new ResourceModel(rootResources, allResources, null);
        }
    }
}
