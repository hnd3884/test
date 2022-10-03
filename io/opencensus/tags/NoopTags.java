package io.opencensus.tags;

import io.opencensus.tags.propagation.TagContextDeserializationException;
import io.opencensus.tags.propagation.TagContextSerializationException;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import io.opencensus.internal.NoopScope;
import io.opencensus.common.Scope;
import javax.annotation.concurrent.Immutable;
import io.opencensus.internal.Utils;
import javax.annotation.concurrent.ThreadSafe;
import io.opencensus.tags.propagation.TagContextTextFormat;
import io.opencensus.tags.propagation.TagContextBinarySerializer;
import io.opencensus.tags.propagation.TagPropagationComponent;

final class NoopTags
{
    private NoopTags() {
    }
    
    static TagsComponent newNoopTagsComponent() {
        return new NoopTagsComponent();
    }
    
    static Tagger getNoopTagger() {
        return NoopTagger.INSTANCE;
    }
    
    static TagContextBuilder getNoopTagContextBuilder() {
        return NoopTagContextBuilder.INSTANCE;
    }
    
    static TagContext getNoopTagContext() {
        return NoopTagContext.INSTANCE;
    }
    
    static TagPropagationComponent getNoopTagPropagationComponent() {
        return NoopTagPropagationComponent.INSTANCE;
    }
    
    static TagContextBinarySerializer getNoopTagContextBinarySerializer() {
        return NoopTagContextBinarySerializer.INSTANCE;
    }
    
    static TagContextTextFormat getNoopTagContextTextSerializer() {
        return NoopTagContextTextFormat.INSTANCE;
    }
    
    @ThreadSafe
    private static final class NoopTagsComponent extends TagsComponent
    {
        private volatile boolean isRead;
        
        @Override
        public Tagger getTagger() {
            return NoopTags.getNoopTagger();
        }
        
        @Override
        public TagPropagationComponent getTagPropagationComponent() {
            return NoopTags.getNoopTagPropagationComponent();
        }
        
        @Override
        public TaggingState getState() {
            this.isRead = true;
            return TaggingState.DISABLED;
        }
        
        @Deprecated
        @Override
        public void setState(final TaggingState state) {
            Utils.checkNotNull(state, "state");
            Utils.checkState(!this.isRead, "State was already read, cannot set state.");
        }
    }
    
    @Immutable
    private static final class NoopTagger extends Tagger
    {
        static final Tagger INSTANCE;
        
        @Override
        public TagContext empty() {
            return NoopTags.getNoopTagContext();
        }
        
        @Override
        public TagContext getCurrentTagContext() {
            return NoopTags.getNoopTagContext();
        }
        
        @Override
        public TagContextBuilder emptyBuilder() {
            return NoopTags.getNoopTagContextBuilder();
        }
        
        @Override
        public TagContextBuilder toBuilder(final TagContext tags) {
            Utils.checkNotNull(tags, "tags");
            return NoopTags.getNoopTagContextBuilder();
        }
        
        @Override
        public TagContextBuilder currentBuilder() {
            return NoopTags.getNoopTagContextBuilder();
        }
        
        @Override
        public Scope withTagContext(final TagContext tags) {
            Utils.checkNotNull(tags, "tags");
            return NoopScope.getInstance();
        }
        
        static {
            INSTANCE = new NoopTagger();
        }
    }
    
    @Immutable
    private static final class NoopTagContextBuilder extends TagContextBuilder
    {
        static final TagContextBuilder INSTANCE;
        
        @Override
        public TagContextBuilder put(final TagKey key, final TagValue value) {
            Utils.checkNotNull(key, "key");
            Utils.checkNotNull(value, "value");
            return this;
        }
        
        @Override
        public TagContextBuilder put(final TagKey key, final TagValue value, final TagMetadata tagMetadata) {
            Utils.checkNotNull(key, "key");
            Utils.checkNotNull(value, "value");
            Utils.checkNotNull(tagMetadata, "tagMetadata");
            return this;
        }
        
        @Override
        public TagContextBuilder remove(final TagKey key) {
            Utils.checkNotNull(key, "key");
            return this;
        }
        
        @Override
        public TagContext build() {
            return NoopTags.getNoopTagContext();
        }
        
        @Override
        public Scope buildScoped() {
            return NoopScope.getInstance();
        }
        
        static {
            INSTANCE = new NoopTagContextBuilder();
        }
    }
    
    @Immutable
    private static final class NoopTagContext extends TagContext
    {
        static final TagContext INSTANCE;
        
        @Override
        protected Iterator<Tag> getIterator() {
            return Collections.emptySet().iterator();
        }
        
        static {
            INSTANCE = new NoopTagContext();
        }
    }
    
    @Immutable
    private static final class NoopTagPropagationComponent extends TagPropagationComponent
    {
        static final TagPropagationComponent INSTANCE;
        
        @Override
        public TagContextBinarySerializer getBinarySerializer() {
            return NoopTags.getNoopTagContextBinarySerializer();
        }
        
        @Override
        public TagContextTextFormat getCorrelationContextFormat() {
            return NoopTags.getNoopTagContextTextSerializer();
        }
        
        static {
            INSTANCE = new NoopTagPropagationComponent();
        }
    }
    
    @Immutable
    private static final class NoopTagContextBinarySerializer extends TagContextBinarySerializer
    {
        static final TagContextBinarySerializer INSTANCE;
        static final byte[] EMPTY_BYTE_ARRAY;
        
        @Override
        public byte[] toByteArray(final TagContext tags) {
            Utils.checkNotNull(tags, "tags");
            return NoopTagContextBinarySerializer.EMPTY_BYTE_ARRAY;
        }
        
        @Override
        public TagContext fromByteArray(final byte[] bytes) {
            Utils.checkNotNull(bytes, "bytes");
            return NoopTags.getNoopTagContext();
        }
        
        static {
            INSTANCE = new NoopTagContextBinarySerializer();
            EMPTY_BYTE_ARRAY = new byte[0];
        }
    }
    
    @Immutable
    private static final class NoopTagContextTextFormat extends TagContextTextFormat
    {
        static final NoopTagContextTextFormat INSTANCE;
        
        @Override
        public List<String> fields() {
            return Collections.emptyList();
        }
        
        @Override
        public <C> void inject(final TagContext tagContext, final C carrier, final Setter<C> setter) throws TagContextSerializationException {
            Utils.checkNotNull(tagContext, "tagContext");
            Utils.checkNotNull(carrier, "carrier");
            Utils.checkNotNull(setter, "setter");
        }
        
        @Override
        public <C> TagContext extract(final C carrier, final Getter<C> getter) throws TagContextDeserializationException {
            Utils.checkNotNull(carrier, "carrier");
            Utils.checkNotNull(getter, "getter");
            return NoopTags.getNoopTagContext();
        }
        
        static {
            INSTANCE = new NoopTagContextTextFormat();
        }
    }
}
