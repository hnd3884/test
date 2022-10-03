package org.apache.tika.metadata.filter;

import org.apache.tika.utils.ServiceLoaderUtils;
import java.util.List;
import org.apache.tika.config.ServiceLoader;

public class DefaultMetadataFilter extends CompositeMetadataFilter
{
    public DefaultMetadataFilter(final ServiceLoader serviceLoader) {
        super(getDefaultFilters(serviceLoader));
    }
    
    public DefaultMetadataFilter(final List<MetadataFilter> metadataFilters) {
        super(metadataFilters);
    }
    
    public DefaultMetadataFilter() {
        this(new ServiceLoader());
    }
    
    private static List<MetadataFilter> getDefaultFilters(final ServiceLoader loader) {
        final List<MetadataFilter> detectors = loader.loadStaticServiceProviders(MetadataFilter.class);
        ServiceLoaderUtils.sortLoadedClasses(detectors);
        return detectors;
    }
}
