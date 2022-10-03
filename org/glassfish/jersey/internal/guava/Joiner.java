package org.glassfish.jersey.internal.guava;

import java.util.Map;
import java.io.IOException;
import java.util.Iterator;

public class Joiner
{
    private final String separator;
    
    private Joiner(final String separator) {
        this.separator = Preconditions.checkNotNull(separator);
    }
    
    public static Joiner on() {
        return new Joiner(", ");
    }
    
    private <A extends Appendable> A appendTo(final A appendable, final Iterator<?> parts) throws IOException {
        Preconditions.checkNotNull(appendable);
        if (parts.hasNext()) {
            appendable.append(this.toString(parts.next()));
            while (parts.hasNext()) {
                appendable.append(this.separator);
                appendable.append(this.toString(parts.next()));
            }
        }
        return appendable;
    }
    
    private StringBuilder appendTo(final StringBuilder builder, final Iterator<?> parts) {
        try {
            this.appendTo(builder, parts);
        }
        catch (final IOException impossible) {
            throw new AssertionError((Object)impossible);
        }
        return builder;
    }
    
    public MapJoiner withKeyValueSeparator() {
        return new MapJoiner(this, "=");
    }
    
    private CharSequence toString(final Object part) {
        Preconditions.checkNotNull(part);
        return (part instanceof CharSequence) ? ((CharSequence)part) : part.toString();
    }
    
    public static final class MapJoiner
    {
        private final Joiner joiner;
        private final String keyValueSeparator;
        
        private MapJoiner(final Joiner joiner, final String keyValueSeparator) {
            this.joiner = joiner;
            this.keyValueSeparator = Preconditions.checkNotNull(keyValueSeparator);
        }
        
        public StringBuilder appendTo(final StringBuilder builder, final Map<?, ?> map) {
            return this.appendTo(builder, map.entrySet());
        }
        
        public <A extends Appendable> A appendTo(final A appendable, final Iterator<? extends Map.Entry<?, ?>> parts) throws IOException {
            Preconditions.checkNotNull(appendable);
            if (parts.hasNext()) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)parts.next();
                appendable.append(this.joiner.toString(entry.getKey()));
                appendable.append(this.keyValueSeparator);
                appendable.append(this.joiner.toString(entry.getValue()));
                while (parts.hasNext()) {
                    appendable.append(this.joiner.separator);
                    final Map.Entry<?, ?> e = (Map.Entry<?, ?>)parts.next();
                    appendable.append(this.joiner.toString(e.getKey()));
                    appendable.append(this.keyValueSeparator);
                    appendable.append(this.joiner.toString(e.getValue()));
                }
            }
            return appendable;
        }
        
        public StringBuilder appendTo(final StringBuilder builder, final Iterable<? extends Map.Entry<?, ?>> entries) {
            return this.appendTo(builder, entries.iterator());
        }
        
        public StringBuilder appendTo(final StringBuilder builder, final Iterator<? extends Map.Entry<?, ?>> entries) {
            try {
                this.appendTo(builder, entries);
            }
            catch (final IOException impossible) {
                throw new AssertionError((Object)impossible);
            }
            return builder;
        }
    }
}
