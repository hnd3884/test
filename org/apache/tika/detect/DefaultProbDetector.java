package org.apache.tika.detect;

import java.util.Collection;
import org.apache.tika.utils.ServiceLoaderUtils;
import java.util.List;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.ProbabilisticMimeDetectionSelector;
import org.apache.tika.config.ServiceLoader;

public class DefaultProbDetector extends CompositeDetector
{
    private static final long serialVersionUID = -8836240060532323352L;
    private final transient ServiceLoader loader;
    
    public DefaultProbDetector(final ProbabilisticMimeDetectionSelector sel, final ServiceLoader loader) {
        super(sel.getMediaTypeRegistry(), getDefaultDetectors(sel, loader));
        this.loader = loader;
    }
    
    public DefaultProbDetector(final ProbabilisticMimeDetectionSelector sel, final ClassLoader loader) {
        this(sel, new ServiceLoader(loader));
    }
    
    public DefaultProbDetector(final ClassLoader loader) {
        this(new ProbabilisticMimeDetectionSelector(), loader);
    }
    
    public DefaultProbDetector(final MimeTypes types) {
        this(new ProbabilisticMimeDetectionSelector(types), new ServiceLoader());
    }
    
    public DefaultProbDetector() {
        this(MimeTypes.getDefaultMimeTypes());
    }
    
    private static List<Detector> getDefaultDetectors(final ProbabilisticMimeDetectionSelector sel, final ServiceLoader loader) {
        final List<Detector> detectors = loader.loadStaticServiceProviders(Detector.class);
        ServiceLoaderUtils.sortLoadedClasses(detectors);
        detectors.add(sel);
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
