package io.opencensus.tags.unsafe;

import java.util.Collections;
import io.opencensus.tags.Tag;
import java.util.Iterator;
import javax.annotation.concurrent.Immutable;
import io.opencensus.internal.Utils;
import javax.annotation.Nullable;
import io.grpc.Context;
import io.opencensus.tags.TagContext;

public final class ContextUtils
{
    private static final TagContext EMPTY_TAG_CONTEXT;
    private static final Context.Key<TagContext> TAG_CONTEXT_KEY;
    
    private ContextUtils() {
    }
    
    public static Context withValue(final Context context, @Nullable final TagContext tagContext) {
        return Utils.checkNotNull(context, "context").withValue((Context.Key)ContextUtils.TAG_CONTEXT_KEY, (Object)tagContext);
    }
    
    public static TagContext getValue(final Context context) {
        final TagContext tags = (TagContext)ContextUtils.TAG_CONTEXT_KEY.get(context);
        return (tags == null) ? ContextUtils.EMPTY_TAG_CONTEXT : tags;
    }
    
    static {
        EMPTY_TAG_CONTEXT = new EmptyTagContext();
        TAG_CONTEXT_KEY = Context.keyWithDefault("opencensus-tag-context-key", (Object)ContextUtils.EMPTY_TAG_CONTEXT);
    }
    
    @Immutable
    private static final class EmptyTagContext extends TagContext
    {
        @Override
        protected Iterator<Tag> getIterator() {
            return Collections.emptySet().iterator();
        }
    }
}
