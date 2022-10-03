package io.opencensus.trace.export;

import io.opencensus.trace.Link;
import java.util.List;

final class AutoValue_SpanData_Links extends SpanData.Links
{
    private final List<Link> links;
    private final int droppedLinksCount;
    
    AutoValue_SpanData_Links(final List<Link> links, final int droppedLinksCount) {
        if (links == null) {
            throw new NullPointerException("Null links");
        }
        this.links = links;
        this.droppedLinksCount = droppedLinksCount;
    }
    
    @Override
    public List<Link> getLinks() {
        return this.links;
    }
    
    @Override
    public int getDroppedLinksCount() {
        return this.droppedLinksCount;
    }
    
    @Override
    public String toString() {
        return "Links{links=" + this.links + ", droppedLinksCount=" + this.droppedLinksCount + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SpanData.Links) {
            final SpanData.Links that = (SpanData.Links)o;
            return this.links.equals(that.getLinks()) && this.droppedLinksCount == that.getDroppedLinksCount();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.links.hashCode();
        h *= 1000003;
        h ^= this.droppedLinksCount;
        return h;
    }
}
