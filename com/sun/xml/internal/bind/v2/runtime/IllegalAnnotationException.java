package com.sun.xml.internal.bind.v2.runtime;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import java.util.List;
import javax.xml.bind.JAXBException;

public class IllegalAnnotationException extends JAXBException
{
    private final List<List<Location>> pos;
    private static final long serialVersionUID = 1L;
    
    public IllegalAnnotationException(final String message, final Locatable src) {
        super(message);
        this.pos = this.build(src);
    }
    
    public IllegalAnnotationException(final String message, final Annotation src) {
        this(message, cast(src));
    }
    
    public IllegalAnnotationException(final String message, final Locatable src1, final Locatable src2) {
        super(message);
        this.pos = this.build(src1, src2);
    }
    
    public IllegalAnnotationException(final String message, final Annotation src1, final Annotation src2) {
        this(message, cast(src1), cast(src2));
    }
    
    public IllegalAnnotationException(final String message, final Annotation src1, final Locatable src2) {
        this(message, cast(src1), src2);
    }
    
    public IllegalAnnotationException(final String message, final Throwable cause, final Locatable src) {
        super(message, cause);
        this.pos = this.build(src);
    }
    
    private static Locatable cast(final Annotation a) {
        if (a instanceof Locatable) {
            return (Locatable)a;
        }
        return null;
    }
    
    private List<List<Location>> build(final Locatable... srcs) {
        final List<List<Location>> r = new ArrayList<List<Location>>();
        for (final Locatable l : srcs) {
            if (l != null) {
                final List<Location> ll = this.convert(l);
                if (ll != null && !ll.isEmpty()) {
                    r.add(ll);
                }
            }
        }
        return Collections.unmodifiableList((List<? extends List<Location>>)r);
    }
    
    private List<Location> convert(Locatable src) {
        if (src == null) {
            return null;
        }
        final List<Location> r = new ArrayList<Location>();
        while (src != null) {
            r.add(src.getLocation());
            src = src.getUpstream();
        }
        return Collections.unmodifiableList((List<? extends Location>)r);
    }
    
    public List<List<Location>> getSourcePos() {
        return this.pos;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getMessage());
        for (final List<Location> locs : this.pos) {
            sb.append("\n\tthis problem is related to the following location:");
            for (final Location loc : locs) {
                sb.append("\n\t\tat ").append(loc.toString());
            }
        }
        return sb.toString();
    }
}
