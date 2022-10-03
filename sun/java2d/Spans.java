package sun.java2d;

import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.Vector;
import java.util.List;

public class Spans
{
    private static final int kMaxAddsSinceSort = 256;
    private List mSpans;
    private int mAddsSinceSort;
    
    public Spans() {
        this.mSpans = new Vector(256);
        this.mAddsSinceSort = 0;
    }
    
    public void add(final float n, final float n2) {
        if (this.mSpans != null) {
            this.mSpans.add(new Span(n, n2));
            if (++this.mAddsSinceSort >= 256) {
                this.sortAndCollapse();
            }
        }
    }
    
    public void addInfinite() {
        this.mSpans = null;
    }
    
    public boolean intersects(final float n, final float n2) {
        boolean b;
        if (this.mSpans != null) {
            if (this.mAddsSinceSort > 0) {
                this.sortAndCollapse();
            }
            b = (Collections.binarySearch(this.mSpans, new Span(n, n2), SpanIntersection.instance) >= 0);
        }
        else {
            b = true;
        }
        return b;
    }
    
    private void sortAndCollapse() {
        Collections.sort((List<Comparable>)this.mSpans);
        this.mAddsSinceSort = 0;
        final Iterator iterator = this.mSpans.iterator();
        Span span = null;
        if (iterator.hasNext()) {
            span = (Span)iterator.next();
        }
        while (iterator.hasNext()) {
            final Span span2 = (Span)iterator.next();
            if (span.subsume(span2)) {
                iterator.remove();
            }
            else {
                span = span2;
            }
        }
    }
    
    static class Span implements Comparable
    {
        private float mStart;
        private float mEnd;
        
        Span(final float mStart, final float mEnd) {
            this.mStart = mStart;
            this.mEnd = mEnd;
        }
        
        final float getStart() {
            return this.mStart;
        }
        
        final float getEnd() {
            return this.mEnd;
        }
        
        final void setStart(final float mStart) {
            this.mStart = mStart;
        }
        
        final void setEnd(final float mEnd) {
            this.mEnd = mEnd;
        }
        
        boolean subsume(final Span span) {
            final boolean contains = this.contains(span.mStart);
            if (contains && span.mEnd > this.mEnd) {
                this.mEnd = span.mEnd;
            }
            return contains;
        }
        
        boolean contains(final float n) {
            return this.mStart <= n && n < this.mEnd;
        }
        
        @Override
        public int compareTo(final Object o) {
            final float start = ((Span)o).getStart();
            int n;
            if (this.mStart < start) {
                n = -1;
            }
            else if (this.mStart > start) {
                n = 1;
            }
            else {
                n = 0;
            }
            return n;
        }
        
        @Override
        public String toString() {
            return "Span: " + this.mStart + " to " + this.mEnd;
        }
    }
    
    static class SpanIntersection implements Comparator
    {
        static final SpanIntersection instance;
        
        private SpanIntersection() {
        }
        
        @Override
        public int compare(final Object o, final Object o2) {
            final Span span = (Span)o;
            final Span span2 = (Span)o2;
            int n;
            if (span.getEnd() <= span2.getStart()) {
                n = -1;
            }
            else if (span.getStart() >= span2.getEnd()) {
                n = 1;
            }
            else {
                n = 0;
            }
            return n;
        }
        
        static {
            instance = new SpanIntersection();
        }
    }
}
