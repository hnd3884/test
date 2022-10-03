package org.apache.tika.detect;

import java.util.Collections;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collection;
import java.util.List;
import java.io.Serializable;

public class CompositeEncodingDetector implements EncodingDetector, Serializable
{
    private static final long serialVersionUID = 5980683158436430252L;
    private final List<EncodingDetector> detectors;
    
    public CompositeEncodingDetector(final List<EncodingDetector> detectors, final Collection<Class<? extends EncodingDetector>> excludeEncodingDetectors) {
        this.detectors = new LinkedList<EncodingDetector>();
        for (final EncodingDetector encodingDetector : detectors) {
            if (!this.isExcluded(excludeEncodingDetectors, encodingDetector.getClass())) {
                this.detectors.add(encodingDetector);
            }
        }
    }
    
    public CompositeEncodingDetector(final List<EncodingDetector> detectors) {
        (this.detectors = new LinkedList<EncodingDetector>()).addAll(detectors);
    }
    
    @Override
    public Charset detect(final InputStream input, final Metadata metadata) throws IOException {
        for (final EncodingDetector detector : this.getDetectors()) {
            final Charset detected = detector.detect(input, metadata);
            if (detected != null) {
                return detected;
            }
        }
        return null;
    }
    
    public List<EncodingDetector> getDetectors() {
        return Collections.unmodifiableList((List<? extends EncodingDetector>)this.detectors);
    }
    
    private boolean isExcluded(final Collection<Class<? extends EncodingDetector>> excludeEncodingDetectors, final Class<? extends EncodingDetector> encodingDetector) {
        return excludeEncodingDetectors.contains(encodingDetector) || this.assignableFrom(excludeEncodingDetectors, encodingDetector);
    }
    
    private boolean assignableFrom(final Collection<Class<? extends EncodingDetector>> excludeEncodingDetectors, final Class<? extends EncodingDetector> encodingDetector) {
        for (final Class<? extends EncodingDetector> e : excludeEncodingDetectors) {
            if (e.isAssignableFrom(encodingDetector)) {
                return true;
            }
        }
        return false;
    }
}
