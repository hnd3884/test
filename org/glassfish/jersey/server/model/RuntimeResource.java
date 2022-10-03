package org.glassfish.jersey.server.model;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import org.glassfish.jersey.uri.PathPattern;
import java.util.List;
import java.util.Comparator;

public class RuntimeResource implements ResourceModelComponent
{
    public static final Comparator<RuntimeResource> COMPARATOR;
    private final String regex;
    private final List<ResourceMethod> resourceMethods;
    private final List<ResourceMethod> resourceLocators;
    private final List<RuntimeResource> childRuntimeResources;
    private final List<Resource> resources;
    private final RuntimeResource parent;
    private final PathPattern pathPattern;
    
    private RuntimeResource(final List<Resource> resources, final List<Builder> childRuntimeResourceBuilders, final RuntimeResource parent, final String regex) {
        this.parent = parent;
        this.pathPattern = resources.get(0).getPathPattern();
        this.resources = new ArrayList<Resource>(resources);
        this.regex = regex;
        this.resourceMethods = new ArrayList<ResourceMethod>();
        this.resourceLocators = new ArrayList<ResourceMethod>();
        this.childRuntimeResources = new ArrayList<RuntimeResource>();
        for (final Builder childRuntimeResourceBuilder : childRuntimeResourceBuilders) {
            this.childRuntimeResources.add(childRuntimeResourceBuilder.build(this));
        }
        Collections.sort(this.childRuntimeResources, RuntimeResource.COMPARATOR);
        for (final Resource res : this.resources) {
            this.resourceMethods.addAll(res.getResourceMethods());
            final ResourceMethod resourceLocator = res.getResourceLocator();
            if (resourceLocator != null) {
                this.resourceLocators.add(resourceLocator);
            }
        }
    }
    
    public List<RuntimeResource> getChildRuntimeResources() {
        return this.childRuntimeResources;
    }
    
    public String getRegex() {
        return this.regex;
    }
    
    public List<ResourceMethod> getResourceMethods() {
        return this.resourceMethods;
    }
    
    public List<ResourceMethod> getResourceLocators() {
        return this.resourceLocators;
    }
    
    public ResourceMethod getResourceLocator() {
        if (this.resourceLocators.size() >= 1) {
            return this.resourceLocators.get(0);
        }
        return null;
    }
    
    public RuntimeResource getParent() {
        return this.parent;
    }
    
    public PathPattern getPathPattern() {
        return this.pathPattern;
    }
    
    public String getFullPathRegex() {
        if (this.parent == null) {
            return this.regex;
        }
        return this.parent.getRegex() + this.regex;
    }
    
    public List<Resource> getParentResources() {
        return this.resources.stream().map(child -> (child == null) ? null : child.getParent()).collect((Collector<? super Object, ?, List<Resource>>)Collectors.toList());
    }
    
    public List<Resource> getResources() {
        return this.resources;
    }
    
    @Override
    public void accept(final ResourceModelVisitor visitor) {
        visitor.visitRuntimeResource(this);
    }
    
    @Override
    public List<? extends ResourceModelComponent> getComponents() {
        return this.getChildRuntimeResources();
    }
    
    static {
        COMPARATOR = new Comparator<RuntimeResource>() {
            @Override
            public int compare(final RuntimeResource o1, final RuntimeResource o2) {
                return PathPattern.COMPARATOR.compare(o1.getPathPattern(), o2.getPathPattern());
            }
        };
    }
    
    static class Builder
    {
        private final List<Resource> resources;
        private final String regex;
        private final List<Builder> childRuntimeResourceBuilders;
        
        public Builder(final List<Resource> resources, final List<Builder> childRuntimeResourceBuilders, final String regex) {
            this.childRuntimeResourceBuilders = childRuntimeResourceBuilders;
            this.resources = resources;
            this.regex = regex;
        }
        
        public RuntimeResource build(final RuntimeResource parent) {
            return new RuntimeResource(this.resources, this.childRuntimeResourceBuilders, parent, this.regex, null);
        }
    }
}
