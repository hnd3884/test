package io.opencensus.tags;

import java.util.HashMap;
import javax.annotation.Nullable;
import java.util.Iterator;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class TagContext
{
    protected abstract Iterator<Tag> getIterator();
    
    @Override
    public String toString() {
        return "TagContext";
    }
    
    @Override
    public boolean equals(@Nullable final Object other) {
        if (!(other instanceof TagContext)) {
            return false;
        }
        final TagContext otherTags = (TagContext)other;
        final Iterator<Tag> iter1 = this.getIterator();
        final Iterator<Tag> iter2 = otherTags.getIterator();
        final HashMap<Tag, Integer> tags = new HashMap<Tag, Integer>();
        while (iter1 != null && iter1.hasNext()) {
            final Tag tag = iter1.next();
            if (tags.containsKey(tag)) {
                tags.put(tag, tags.get(tag) + 1);
            }
            else {
                tags.put(tag, 1);
            }
        }
        while (iter2 != null && iter2.hasNext()) {
            final Tag tag = iter2.next();
            if (!tags.containsKey(tag)) {
                return false;
            }
            final int count = tags.get(tag);
            if (count > 1) {
                tags.put(tag, count - 1);
            }
            else {
                tags.remove(tag);
            }
        }
        return tags.isEmpty();
    }
    
    @Override
    public final int hashCode() {
        int hashCode = 0;
        final Iterator<Tag> i = this.getIterator();
        if (i == null) {
            return hashCode;
        }
        while (i.hasNext()) {
            final Tag tag = i.next();
            if (tag != null) {
                hashCode += tag.hashCode();
            }
        }
        return hashCode;
    }
}
