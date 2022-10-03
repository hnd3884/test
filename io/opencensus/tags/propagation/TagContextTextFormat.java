package io.opencensus.tags.propagation;

import javax.annotation.Nullable;
import io.opencensus.tags.TagContext;
import java.util.List;

public abstract class TagContextTextFormat
{
    public abstract List<String> fields();
    
    public abstract <C> void inject(final TagContext p0, final C p1, final Setter<C> p2) throws TagContextSerializationException;
    
    public abstract <C> TagContext extract(final C p0, final Getter<C> p1) throws TagContextDeserializationException;
    
    public abstract static class Setter<C>
    {
        public abstract void put(final C p0, final String p1, final String p2);
    }
    
    public abstract static class Getter<C>
    {
        @Nullable
        public abstract String get(final C p0, final String p1);
    }
}
