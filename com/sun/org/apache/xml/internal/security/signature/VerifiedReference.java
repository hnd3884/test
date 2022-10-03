package com.sun.org.apache.xml.internal.security.signature;

import java.util.Collections;
import java.util.List;

public class VerifiedReference
{
    private final boolean valid;
    private final String uri;
    private final List<VerifiedReference> manifestReferences;
    
    public VerifiedReference(final boolean valid, final String uri, final List<VerifiedReference> manifestReferences) {
        this.valid = valid;
        this.uri = uri;
        if (manifestReferences != null) {
            this.manifestReferences = manifestReferences;
        }
        else {
            this.manifestReferences = Collections.emptyList();
        }
    }
    
    public boolean isValid() {
        return this.valid;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public List<VerifiedReference> getManifestReferences() {
        return Collections.unmodifiableList((List<? extends VerifiedReference>)this.manifestReferences);
    }
}
