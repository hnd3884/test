package org.glassfish.jersey.message.internal;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.HashSet;
import org.glassfish.jersey.internal.util.collection.Ref;
import javax.ws.rs.core.Variant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.core.MediaType;

public final class VariantSelector
{
    private static final DimensionChecker<AcceptableMediaType, MediaType> MEDIA_TYPE_DC;
    private static final DimensionChecker<AcceptableLanguageTag, Locale> LANGUAGE_TAG_DC;
    private static final DimensionChecker<AcceptableToken, String> CHARSET_DC;
    private static final DimensionChecker<AcceptableToken, String> ENCODING_DC;
    
    private VariantSelector() {
    }
    
    private static <T extends Qualified, U> LinkedList<VariantHolder> selectVariants(final List<VariantHolder> variantHolders, final List<T> acceptableValues, final DimensionChecker<T, U> dimensionChecker, final Set<String> vary) {
        int cq = 0;
        int cqs = 0;
        final LinkedList<VariantHolder> selected = new LinkedList<VariantHolder>();
        for (final T a : acceptableValues) {
            final int q = a.getQuality();
            final Iterator<VariantHolder> iv = variantHolders.iterator();
            while (iv.hasNext()) {
                final VariantHolder v = iv.next();
                final U d = dimensionChecker.getDimension(v);
                if (d != null) {
                    vary.add(dimensionChecker.getVaryHeaderValue());
                    final int qs = dimensionChecker.getQualitySource(v, d);
                    if (qs < cqs || !dimensionChecker.isCompatible(a, d)) {
                        continue;
                    }
                    if (qs > cqs) {
                        cqs = qs;
                        cq = q;
                        selected.clear();
                        selected.add(v);
                    }
                    else if (q > cq) {
                        cq = q;
                        selected.addFirst(v);
                    }
                    else if (q == cq) {
                        selected.add(v);
                    }
                    iv.remove();
                }
            }
        }
        for (final VariantHolder v2 : variantHolders) {
            if (dimensionChecker.getDimension(v2) == null) {
                selected.add(v2);
            }
        }
        return selected;
    }
    
    private static LinkedList<VariantHolder> getVariantHolderList(final List<Variant> variants) {
        final LinkedList<VariantHolder> l = new LinkedList<VariantHolder>();
        for (final Variant v : variants) {
            final MediaType mt = v.getMediaType();
            if (mt != null) {
                if (mt instanceof QualitySourceMediaType || mt.getParameters().containsKey("qs")) {
                    final int qs = QualitySourceMediaType.getQualitySource(mt);
                    l.add(new VariantHolder(v, qs));
                }
                else {
                    l.add(new VariantHolder(v));
                }
            }
            else {
                l.add(new VariantHolder(v));
            }
        }
        return l;
    }
    
    public static Variant selectVariant(final InboundMessageContext context, final List<Variant> variants, final Ref<String> varyHeaderValue) {
        final List<Variant> selectedVariants = selectVariants(context, variants, varyHeaderValue);
        return selectedVariants.isEmpty() ? null : selectedVariants.get(0);
    }
    
    public static List<Variant> selectVariants(final InboundMessageContext context, final List<Variant> variants, final Ref<String> varyHeaderValue) {
        LinkedList<VariantHolder> vhs = getVariantHolderList(variants);
        final Set<String> vary = new HashSet<String>();
        vhs = selectVariants(vhs, context.getQualifiedAcceptableMediaTypes(), VariantSelector.MEDIA_TYPE_DC, vary);
        vhs = selectVariants(vhs, context.getQualifiedAcceptableLanguages(), VariantSelector.LANGUAGE_TAG_DC, vary);
        vhs = selectVariants(vhs, context.getQualifiedAcceptCharset(), VariantSelector.CHARSET_DC, vary);
        vhs = selectVariants(vhs, context.getQualifiedAcceptEncoding(), VariantSelector.ENCODING_DC, vary);
        if (vhs.isEmpty()) {
            return Collections.emptyList();
        }
        final StringBuilder varyHeader = new StringBuilder();
        for (final String v : vary) {
            if (varyHeader.length() > 0) {
                varyHeader.append(',');
            }
            varyHeader.append(v);
        }
        final String varyValue = varyHeader.toString();
        if (!varyValue.isEmpty()) {
            varyHeaderValue.set(varyValue);
        }
        return vhs.stream().map(variantHolder -> variantHolder.v).collect((Collector<? super Object, ?, List<Variant>>)Collectors.toList());
    }
    
    static {
        MEDIA_TYPE_DC = new DimensionChecker<AcceptableMediaType, MediaType>() {
            @Override
            public MediaType getDimension(final VariantHolder v) {
                return v.v.getMediaType();
            }
            
            @Override
            public boolean isCompatible(final AcceptableMediaType t, final MediaType u) {
                return t.isCompatible(u);
            }
            
            @Override
            public int getQualitySource(final VariantHolder v, final MediaType u) {
                return v.mediaTypeQs;
            }
            
            @Override
            public String getVaryHeaderValue() {
                return "Accept";
            }
        };
        LANGUAGE_TAG_DC = new DimensionChecker<AcceptableLanguageTag, Locale>() {
            @Override
            public Locale getDimension(final VariantHolder v) {
                return v.v.getLanguage();
            }
            
            @Override
            public boolean isCompatible(final AcceptableLanguageTag t, final Locale u) {
                return t.isCompatible(u);
            }
            
            @Override
            public int getQualitySource(final VariantHolder qsv, final Locale u) {
                return 0;
            }
            
            @Override
            public String getVaryHeaderValue() {
                return "Accept-Language";
            }
        };
        CHARSET_DC = new DimensionChecker<AcceptableToken, String>() {
            @Override
            public String getDimension(final VariantHolder v) {
                final MediaType m = v.v.getMediaType();
                return (m != null) ? m.getParameters().get("charset") : null;
            }
            
            @Override
            public boolean isCompatible(final AcceptableToken t, final String u) {
                return t.isCompatible(u);
            }
            
            @Override
            public int getQualitySource(final VariantHolder qsv, final String u) {
                return 0;
            }
            
            @Override
            public String getVaryHeaderValue() {
                return "Accept-Charset";
            }
        };
        ENCODING_DC = new DimensionChecker<AcceptableToken, String>() {
            @Override
            public String getDimension(final VariantHolder v) {
                return v.v.getEncoding();
            }
            
            @Override
            public boolean isCompatible(final AcceptableToken t, final String u) {
                return t.isCompatible(u);
            }
            
            @Override
            public int getQualitySource(final VariantHolder qsv, final String u) {
                return 0;
            }
            
            @Override
            public String getVaryHeaderValue() {
                return "Accept-Encoding";
            }
        };
    }
    
    private static class VariantHolder
    {
        private final Variant v;
        private final int mediaTypeQs;
        
        VariantHolder(final Variant v) {
            this(v, 1000);
        }
        
        VariantHolder(final Variant v, final int mediaTypeQs) {
            this.v = v;
            this.mediaTypeQs = mediaTypeQs;
        }
    }
    
    private interface DimensionChecker<T, U>
    {
        U getDimension(final VariantHolder p0);
        
        int getQualitySource(final VariantHolder p0, final U p1);
        
        boolean isCompatible(final T p0, final U p1);
        
        String getVaryHeaderValue();
    }
}
