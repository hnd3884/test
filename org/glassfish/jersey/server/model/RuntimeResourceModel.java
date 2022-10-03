package org.glassfish.jersey.server.model;

import java.util.Collection;
import java.util.Map;
import org.glassfish.jersey.uri.PathTemplate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class RuntimeResourceModel
{
    private final List<RuntimeResource> runtimeResources;
    
    public RuntimeResourceModel(final List<Resource> resources) {
        this.runtimeResources = new ArrayList<RuntimeResource>();
        for (final RuntimeResource.Builder builder : this.getRuntimeResources(resources)) {
            this.runtimeResources.add(builder.build(null));
        }
        Collections.sort(this.runtimeResources, RuntimeResource.COMPARATOR);
    }
    
    private List<RuntimeResource.Builder> getRuntimeResources(final List<Resource> resources) {
        final Map<String, List<Resource>> regexMap = new HashMap<String, List<Resource>>();
        for (final Resource resource : resources) {
            String path = resource.getPath();
            String regex = null;
            if (path != null) {
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                regex = new PathTemplate(path).getPattern().getRegex();
            }
            List<Resource> listFromMap = regexMap.get(regex);
            if (listFromMap == null) {
                listFromMap = new ArrayList<Resource>();
                regexMap.put(regex, listFromMap);
            }
            listFromMap.add(resource);
        }
        final List<RuntimeResource.Builder> runtimeResources = new ArrayList<RuntimeResource.Builder>();
        for (final Map.Entry<String, List<Resource>> entry : regexMap.entrySet()) {
            final List<Resource> resourcesWithSameRegex = entry.getValue();
            final List<Resource> childResources = new ArrayList<Resource>();
            for (final Resource res : resourcesWithSameRegex) {
                childResources.addAll(res.getChildResources());
            }
            final List<RuntimeResource.Builder> childRuntimeResources = this.getRuntimeResources(childResources);
            runtimeResources.add(new RuntimeResource.Builder(resourcesWithSameRegex, childRuntimeResources, entry.getKey()));
        }
        return runtimeResources;
    }
    
    public List<RuntimeResource> getRuntimeResources() {
        return this.runtimeResources;
    }
}
