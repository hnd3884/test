package org.apache.lucene.search;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class Explanation
{
    private final boolean match;
    private final float value;
    private final String description;
    private final List<Explanation> details;
    
    public static Explanation match(final float value, final String description, final Collection<Explanation> details) {
        return new Explanation(true, value, description, details);
    }
    
    public static Explanation match(final float value, final String description, final Explanation... details) {
        return new Explanation(true, value, description, Arrays.asList(details));
    }
    
    public static Explanation noMatch(final String description, final Collection<Explanation> details) {
        return new Explanation(false, 0.0f, description, details);
    }
    
    public static Explanation noMatch(final String description, final Explanation... details) {
        return new Explanation(false, 0.0f, description, Arrays.asList(details));
    }
    
    private Explanation(final boolean match, final float value, final String description, final Collection<Explanation> details) {
        this.match = match;
        this.value = value;
        this.description = Objects.requireNonNull(description);
        this.details = Collections.unmodifiableList((List<? extends Explanation>)new ArrayList<Explanation>(details));
        for (final Explanation detail : details) {
            Objects.requireNonNull(detail);
        }
    }
    
    public boolean isMatch() {
        return this.match;
    }
    
    public float getValue() {
        return this.value;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    private String getSummary() {
        return this.getValue() + " = " + this.getDescription();
    }
    
    public Explanation[] getDetails() {
        return this.details.toArray(new Explanation[0]);
    }
    
    @Override
    public String toString() {
        return this.toString(0);
    }
    
    private String toString(final int depth) {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < depth; ++i) {
            buffer.append("  ");
        }
        buffer.append(this.getSummary());
        buffer.append("\n");
        final Explanation[] details = this.getDetails();
        for (int j = 0; j < details.length; ++j) {
            buffer.append(details[j].toString(depth + 1));
        }
        return buffer.toString();
    }
    
    public String toHtml() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<ul>\n");
        buffer.append("<li>");
        buffer.append(this.getSummary());
        buffer.append("<br />\n");
        final Explanation[] details = this.getDetails();
        for (int i = 0; i < details.length; ++i) {
            buffer.append(details[i].toHtml());
        }
        buffer.append("</li>\n");
        buffer.append("</ul>\n");
        return buffer.toString();
    }
}
