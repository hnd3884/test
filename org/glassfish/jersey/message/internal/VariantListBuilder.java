package org.glassfish.jersey.message.internal;

import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import java.util.List;
import javax.ws.rs.core.Variant;

public class VariantListBuilder extends Variant.VariantListBuilder
{
    private List<Variant> variants;
    private final List<MediaType> mediaTypes;
    private final List<Locale> languages;
    private final List<String> encodings;
    
    public VariantListBuilder() {
        this.mediaTypes = new ArrayList<MediaType>();
        this.languages = new ArrayList<Locale>();
        this.encodings = new ArrayList<String>();
    }
    
    public List<Variant> build() {
        if (!this.mediaTypes.isEmpty() || !this.languages.isEmpty() || !this.encodings.isEmpty()) {
            this.add();
        }
        if (this.variants == null) {
            this.variants = new ArrayList<Variant>();
        }
        return this.variants;
    }
    
    public VariantListBuilder add() {
        if (this.variants == null) {
            this.variants = new ArrayList<Variant>();
        }
        this.addMediaTypes();
        this.languages.clear();
        this.encodings.clear();
        this.mediaTypes.clear();
        return this;
    }
    
    private void addMediaTypes() {
        if (this.mediaTypes.isEmpty()) {
            this.addLanguages(null);
        }
        else {
            for (final MediaType mediaType : this.mediaTypes) {
                this.addLanguages(mediaType);
            }
        }
    }
    
    private void addLanguages(final MediaType mediaType) {
        if (this.languages.isEmpty()) {
            this.addEncodings(mediaType, null);
        }
        else {
            for (final Locale language : this.languages) {
                this.addEncodings(mediaType, language);
            }
        }
    }
    
    private void addEncodings(final MediaType mediaType, final Locale language) {
        if (this.encodings.isEmpty()) {
            this.addVariant(mediaType, language, null);
        }
        else {
            for (final String encoding : this.encodings) {
                this.addVariant(mediaType, language, encoding);
            }
        }
    }
    
    private void addVariant(final MediaType mediaType, final Locale language, final String encoding) {
        this.variants.add(new Variant(mediaType, language, encoding));
    }
    
    public VariantListBuilder languages(final Locale... languages) {
        this.languages.addAll(Arrays.asList(languages));
        return this;
    }
    
    public VariantListBuilder encodings(final String... encodings) {
        this.encodings.addAll(Arrays.asList(encodings));
        return this;
    }
    
    public VariantListBuilder mediaTypes(final MediaType... mediaTypes) {
        this.mediaTypes.addAll(Arrays.asList(mediaTypes));
        return this;
    }
}
