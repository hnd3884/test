package org.apache.tika.detect;

import java.util.Collections;
import java.io.IOException;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.tika.mime.MediaTypeRegistry;

public class CompositeDetector implements Detector
{
    private static final long serialVersionUID = 5980683158436430252L;
    private final MediaTypeRegistry registry;
    private final List<Detector> detectors;
    
    public CompositeDetector(final MediaTypeRegistry registry, final List<Detector> detectors, final Collection<Class<? extends Detector>> excludeDetectors) {
        if (excludeDetectors == null || excludeDetectors.isEmpty()) {
            this.detectors = detectors;
        }
        else {
            this.detectors = new ArrayList<Detector>();
            for (final Detector d : detectors) {
                if (!this.isExcluded(excludeDetectors, d.getClass())) {
                    this.detectors.add(d);
                }
            }
        }
        this.registry = registry;
    }
    
    public CompositeDetector(final MediaTypeRegistry registry, final List<Detector> detectors) {
        this(registry, detectors, null);
    }
    
    public CompositeDetector(final List<Detector> detectors) {
        this(new MediaTypeRegistry(), detectors);
    }
    
    public CompositeDetector(final Detector... detectors) {
        this(Arrays.asList(detectors));
    }
    
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) throws IOException {
        MediaType type = MediaType.OCTET_STREAM;
        for (final Detector detector : this.getDetectors()) {
            if (detector instanceof OverrideDetector && (metadata.get(TikaCoreProperties.CONTENT_TYPE_USER_OVERRIDE) != null || metadata.get(TikaCoreProperties.CONTENT_TYPE_PARSER_OVERRIDE) != null)) {
                return detector.detect(input, metadata);
            }
            final MediaType detected = detector.detect(input, metadata);
            if (!this.registry.isSpecializationOf(detected, type)) {
                continue;
            }
            type = detected;
        }
        return type;
    }
    
    public List<Detector> getDetectors() {
        return Collections.unmodifiableList((List<? extends Detector>)this.detectors);
    }
    
    private boolean isExcluded(final Collection<Class<? extends Detector>> excludeDetectors, final Class<? extends Detector> d) {
        return excludeDetectors.contains(d) || this.assignableFrom(excludeDetectors, d);
    }
    
    private boolean assignableFrom(final Collection<Class<? extends Detector>> excludeDetectors, final Class<? extends Detector> d) {
        for (final Class<? extends Detector> e : excludeDetectors) {
            if (e.isAssignableFrom(d)) {
                return true;
            }
        }
        return false;
    }
}
