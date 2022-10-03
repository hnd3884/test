package com.sun.xml.internal.ws.developer;

import java.util.Iterator;
import com.sun.istack.internal.NotNull;
import java.util.ArrayList;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.api.message.Header;
import java.util.List;

public final class EPRRecipe
{
    private final List<Header> referenceParameters;
    private final List<Source> metadata;
    
    public EPRRecipe() {
        this.referenceParameters = new ArrayList<Header>();
        this.metadata = new ArrayList<Source>();
    }
    
    @NotNull
    public List<Header> getReferenceParameters() {
        return this.referenceParameters;
    }
    
    @NotNull
    public List<Source> getMetadata() {
        return this.metadata;
    }
    
    public EPRRecipe addReferenceParameter(final Header h) {
        if (h == null) {
            throw new IllegalArgumentException();
        }
        this.referenceParameters.add(h);
        return this;
    }
    
    public EPRRecipe addReferenceParameters(final Header... headers) {
        for (final Header h : headers) {
            this.addReferenceParameter(h);
        }
        return this;
    }
    
    public EPRRecipe addReferenceParameters(final Iterable<? extends Header> headers) {
        for (final Header h : headers) {
            this.addReferenceParameter(h);
        }
        return this;
    }
    
    public EPRRecipe addMetadata(final Source source) {
        if (source == null) {
            throw new IllegalArgumentException();
        }
        this.metadata.add(source);
        return this;
    }
    
    public EPRRecipe addMetadata(final Source... sources) {
        for (final Source s : sources) {
            this.addMetadata(s);
        }
        return this;
    }
    
    public EPRRecipe addMetadata(final Iterable<? extends Source> sources) {
        for (final Source s : sources) {
            this.addMetadata(s);
        }
        return this;
    }
}
