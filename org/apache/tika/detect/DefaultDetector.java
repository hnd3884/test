package org.apache.tika.detect;

import java.util.Iterator;
import org.apache.tika.utils.ServiceLoaderUtils;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.config.ServiceLoader;

public class DefaultDetector extends CompositeDetector
{
    private static final long serialVersionUID = -8170114575326908027L;
    private final transient ServiceLoader loader;
    
    public DefaultDetector(final MimeTypes types, final ServiceLoader loader, final Collection<Class<? extends Detector>> excludeDetectors) {
        super(types.getMediaTypeRegistry(), getDefaultDetectors(types, loader, excludeDetectors));
        this.loader = loader;
    }
    
    public DefaultDetector(final MimeTypes types, final ServiceLoader loader) {
        this(types, loader, Collections.EMPTY_SET);
    }
    
    public DefaultDetector(final MimeTypes types, final ClassLoader loader) {
        this(types, new ServiceLoader(loader));
    }
    
    public DefaultDetector(final ClassLoader loader) {
        this(MimeTypes.getDefaultMimeTypes(), loader);
    }
    
    public DefaultDetector(final MimeTypes types) {
        this(types, new ServiceLoader());
    }
    
    public DefaultDetector() {
        this(MimeTypes.getDefaultMimeTypes());
    }
    
    private static List<Detector> getDefaultDetectors(final MimeTypes types, final ServiceLoader loader, final Collection<Class<? extends Detector>> excludeDetectors) {
        final List<Detector> detectors = loader.loadStaticServiceProviders(Detector.class, excludeDetectors);
        ServiceLoaderUtils.sortLoadedClasses(detectors);
        int overrideIndex = -1;
        int i = 0;
        for (final Detector detector : detectors) {
            if (detector instanceof OverrideDetector) {
                overrideIndex = i;
                break;
            }
            ++i;
        }
        if (overrideIndex > -1) {
            final Detector detector2 = detectors.remove(overrideIndex);
            detectors.add(0, detector2);
        }
        detectors.add(types);
        return detectors;
    }
    
    @Override
    public List<Detector> getDetectors() {
        if (this.loader != null) {
            final List<Detector> detectors = this.loader.loadDynamicServiceProviders(Detector.class);
            detectors.addAll(super.getDetectors());
            return detectors;
        }
        return super.getDetectors();
    }
}
