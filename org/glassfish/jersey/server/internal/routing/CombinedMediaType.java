package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.message.internal.Quality;
import org.glassfish.jersey.message.internal.QualitySourceMediaType;
import org.glassfish.jersey.message.internal.MediaTypes;
import java.util.Comparator;
import javax.ws.rs.core.MediaType;

final class CombinedMediaType
{
    static final CombinedMediaType NO_MATCH;
    final MediaType combinedType;
    final int q;
    final int qs;
    final int d;
    static final Comparator<CombinedMediaType> COMPARATOR;
    
    private static int matchedWildcards(final MediaType clientMt, final EffectiveMediaType serverMt) {
        return b2i(clientMt.isWildcardType() ^ serverMt.isWildcardType()) + b2i(clientMt.isWildcardSubtype() ^ serverMt.isWildcardSubType());
    }
    
    private static int b2i(final boolean b) {
        return b ? 1 : 0;
    }
    
    private CombinedMediaType(final MediaType combinedType, final int q, final int qs, final int d) {
        this.combinedType = combinedType;
        this.q = q;
        this.qs = qs;
        this.d = d;
    }
    
    static CombinedMediaType create(final MediaType clientType, final EffectiveMediaType serverType) {
        if (!clientType.isCompatible(serverType.getMediaType())) {
            return CombinedMediaType.NO_MATCH;
        }
        final MediaType strippedClientType = MediaTypes.stripQualityParams(clientType);
        final MediaType strippedServerType = MediaTypes.stripQualityParams(serverType.getMediaType());
        return new CombinedMediaType(MediaTypes.mostSpecific(strippedClientType, strippedServerType), MediaTypes.getQuality(clientType), QualitySourceMediaType.getQualitySource(serverType.getMediaType()), matchedWildcards(clientType, serverType));
    }
    
    @Override
    public String toString() {
        return String.format("%s;q=%d;qs=%d;d=%d", this.combinedType, this.q, this.qs, this.d);
    }
    
    static {
        NO_MATCH = new CombinedMediaType(null, 0, 0, 0);
        COMPARATOR = new Comparator<CombinedMediaType>() {
            @Override
            public int compare(final CombinedMediaType c1, final CombinedMediaType c2) {
                int delta = MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(c1.combinedType, c2.combinedType);
                if (delta != 0) {
                    return delta;
                }
                delta = Quality.QUALITY_VALUE_COMPARATOR.compare(c1.q, c2.q);
                if (delta != 0) {
                    return delta;
                }
                delta = Quality.QUALITY_VALUE_COMPARATOR.compare(c1.qs, c2.qs);
                if (delta != 0) {
                    return delta;
                }
                return Integer.compare(c1.d, c2.d);
            }
        };
    }
    
    static class EffectiveMediaType
    {
        private final boolean derived;
        private final MediaType mediaType;
        
        public EffectiveMediaType(final MediaType mediaType, final boolean fromMessageBodyProviders) {
            this.derived = fromMessageBodyProviders;
            this.mediaType = mediaType;
        }
        
        public EffectiveMediaType(final String mediaTypeValue) {
            this(MediaType.valueOf(mediaTypeValue), false);
        }
        
        public EffectiveMediaType(final MediaType mediaType) {
            this(mediaType, false);
        }
        
        public boolean isWildcardType() {
            return this.mediaType.isWildcardType();
        }
        
        public boolean isWildcardSubType() {
            return this.mediaType.isWildcardSubtype();
        }
        
        public MediaType getMediaType() {
            return this.mediaType;
        }
        
        boolean isDerived() {
            return this.derived;
        }
        
        @Override
        public String toString() {
            return String.format("mediaType=[%s], fromProviders=%b", this.mediaType, this.derived);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof EffectiveMediaType)) {
                return false;
            }
            final EffectiveMediaType that = (EffectiveMediaType)o;
            return this.derived == that.derived && this.mediaType.equals((Object)that.mediaType);
        }
        
        @Override
        public int hashCode() {
            int result = this.derived ? 1 : 0;
            result = 31 * result + this.mediaType.hashCode();
            return result;
        }
    }
}
