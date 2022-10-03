package org.apache.poi.ooxml.util;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedList;

public class IdentifierManager
{
    public static final long MAX_ID = 9223372036854775806L;
    public static final long MIN_ID = 0L;
    private final long upperbound;
    private final long lowerbound;
    private LinkedList<Segment> segments;
    
    public IdentifierManager(final long lowerbound, final long upperbound) {
        if (lowerbound > upperbound) {
            throw new IllegalArgumentException("lowerbound must not be greater than upperbound, had " + lowerbound + " and " + upperbound);
        }
        if (lowerbound < 0L) {
            final String message = "lowerbound must be greater than or equal to " + Long.toString(0L);
            throw new IllegalArgumentException(message);
        }
        if (upperbound > 9223372036854775806L) {
            throw new IllegalArgumentException("upperbound must be less than or equal to " + Long.toString(9223372036854775806L) + " but had " + upperbound);
        }
        this.lowerbound = lowerbound;
        this.upperbound = upperbound;
        (this.segments = new LinkedList<Segment>()).add(new Segment(lowerbound, upperbound));
    }
    
    public long reserve(final long id) {
        if (id < this.lowerbound || id > this.upperbound) {
            throw new IllegalArgumentException("Value for parameter 'id' was out of bounds, had " + id + ", but should be within [" + this.lowerbound + ":" + this.upperbound + "]");
        }
        this.verifyIdentifiersLeft();
        if (id == this.upperbound) {
            final Segment lastSegment = this.segments.getLast();
            if (lastSegment.end == this.upperbound) {
                lastSegment.end = this.upperbound - 1L;
                if (lastSegment.start > lastSegment.end) {
                    this.segments.removeLast();
                }
                return id;
            }
            return this.reserveNew();
        }
        else {
            if (id != this.lowerbound) {
                final ListIterator<Segment> iter = this.segments.listIterator();
                while (iter.hasNext()) {
                    final Segment segment = iter.next();
                    if (segment.end < id) {
                        continue;
                    }
                    if (segment.start > id) {
                        break;
                    }
                    if (segment.start == id) {
                        segment.start = id + 1L;
                        if (segment.end < segment.start) {
                            iter.remove();
                        }
                        return id;
                    }
                    if (segment.end == id) {
                        segment.end = id - 1L;
                        if (segment.start > segment.end) {
                            iter.remove();
                        }
                        return id;
                    }
                    iter.add(new Segment(id + 1L, segment.end));
                    segment.end = id - 1L;
                    return id;
                }
                return this.reserveNew();
            }
            final Segment firstSegment = this.segments.getFirst();
            if (firstSegment.start == this.lowerbound) {
                firstSegment.start = this.lowerbound + 1L;
                if (firstSegment.end < firstSegment.start) {
                    this.segments.removeFirst();
                }
                return id;
            }
            return this.reserveNew();
        }
    }
    
    public long reserveNew() {
        this.verifyIdentifiersLeft();
        final Segment segment = this.segments.getFirst();
        final long result = segment.start;
        final Segment segment2 = segment;
        ++segment2.start;
        if (segment.start > segment.end) {
            this.segments.removeFirst();
        }
        return result;
    }
    
    public boolean release(final long id) {
        if (id < this.lowerbound || id > this.upperbound) {
            throw new IllegalArgumentException("Value for parameter 'id' was out of bounds, had " + id + ", but should be within [" + this.lowerbound + ":" + this.upperbound + "]");
        }
        if (id == this.upperbound) {
            final Segment lastSegment = this.segments.getLast();
            if (lastSegment.end == this.upperbound - 1L) {
                lastSegment.end = this.upperbound;
                return true;
            }
            if (lastSegment.end == this.upperbound) {
                return false;
            }
            this.segments.add(new Segment(this.upperbound, this.upperbound));
            return true;
        }
        else {
            if (id != this.lowerbound) {
                final long higher = id + 1L;
                final long lower = id - 1L;
                final ListIterator<Segment> iter = this.segments.listIterator();
                while (iter.hasNext()) {
                    final Segment segment = iter.next();
                    if (segment.end < lower) {
                        continue;
                    }
                    if (segment.start > higher) {
                        iter.previous();
                        iter.add(new Segment(id, id));
                        return true;
                    }
                    if (segment.start == higher) {
                        segment.start = id;
                        return true;
                    }
                    if (segment.end == lower) {
                        segment.end = id;
                        if (iter.hasNext()) {
                            final Segment next = iter.next();
                            if (next.start == segment.end + 1L) {
                                segment.end = next.end;
                                iter.remove();
                            }
                        }
                        return true;
                    }
                    break;
                }
                return false;
            }
            final Segment firstSegment = this.segments.getFirst();
            if (firstSegment.start == this.lowerbound + 1L) {
                firstSegment.start = this.lowerbound;
                return true;
            }
            if (firstSegment.start == this.lowerbound) {
                return false;
            }
            this.segments.addFirst(new Segment(this.lowerbound, this.lowerbound));
            return true;
        }
    }
    
    public long getRemainingIdentifiers() {
        long result = 0L;
        for (final Segment segment : this.segments) {
            result -= segment.start;
            result = result + segment.end + 1L;
        }
        return result;
    }
    
    private void verifyIdentifiersLeft() {
        if (this.segments.isEmpty()) {
            throw new IllegalStateException("No identifiers left");
        }
    }
    
    private static class Segment
    {
        public long start;
        public long end;
        
        public Segment(final long start, final long end) {
            this.start = start;
            this.end = end;
        }
        
        @Override
        public String toString() {
            return "[" + this.start + "; " + this.end + "]";
        }
    }
}
