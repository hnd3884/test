package org.apache.xmlbeans.impl.tool;

import org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;

public abstract class SchemaImportResolver
{
    public abstract SchemaResource lookupResource(final String p0, final String p1);
    
    public abstract void reportActualNamespace(final SchemaResource p0, final String p1);
    
    protected final void resolveImports(final SchemaResource[] resources) {
        final LinkedList queueOfResources = new LinkedList((Collection<? extends E>)Arrays.asList(resources));
        final LinkedList queueOfLocators = new LinkedList();
        final Set seenResources = new HashSet();
        while (true) {
            SchemaResource nextResource;
            if (!queueOfResources.isEmpty()) {
                nextResource = queueOfResources.removeFirst();
            }
            else {
                if (queueOfLocators.isEmpty()) {
                    break;
                }
                final SchemaLocator locator = queueOfLocators.removeFirst();
                nextResource = this.lookupResource(locator.namespace, locator.schemaLocation);
                if (nextResource == null) {
                    continue;
                }
            }
            if (seenResources.contains(nextResource)) {
                continue;
            }
            seenResources.add(nextResource);
            final SchemaDocument.Schema schema = nextResource.getSchema();
            if (schema == null) {
                continue;
            }
            String actualTargetNamespace = schema.getTargetNamespace();
            if (actualTargetNamespace == null) {
                actualTargetNamespace = "";
            }
            final String expectedTargetNamespace = nextResource.getNamespace();
            if (expectedTargetNamespace == null || !actualTargetNamespace.equals(expectedTargetNamespace)) {
                this.reportActualNamespace(nextResource, actualTargetNamespace);
            }
            final ImportDocument.Import[] schemaImports = schema.getImportArray();
            for (int i = 0; i < schemaImports.length; ++i) {
                queueOfLocators.add(new SchemaLocator((schemaImports[i].getNamespace() == null) ? "" : schemaImports[i].getNamespace(), schemaImports[i].getSchemaLocation()));
            }
            final IncludeDocument.Include[] schemaIncludes = schema.getIncludeArray();
            for (int j = 0; j < schemaIncludes.length; ++j) {
                queueOfLocators.add(new SchemaLocator(null, schemaIncludes[j].getSchemaLocation()));
            }
        }
    }
    
    private static class SchemaLocator
    {
        public final String namespace;
        public final String schemaLocation;
        
        public SchemaLocator(final String namespace, final String schemaLocation) {
            this.namespace = namespace;
            this.schemaLocation = schemaLocation;
        }
    }
    
    public interface SchemaResource
    {
        SchemaDocument.Schema getSchema();
        
        String getNamespace();
        
        String getSchemaLocation();
    }
}
