package com.unboundid.util;

import java.util.Iterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SASLMechanismInfo
{
    private final boolean acceptsPassword;
    private final boolean requiresPassword;
    private final List<SASLOption> options;
    private final String description;
    private final String name;
    
    public SASLMechanismInfo(final String name, final String description, final boolean acceptsPassword, final boolean requiresPassword, final SASLOption... options) {
        this.name = name;
        this.description = description;
        this.acceptsPassword = acceptsPassword;
        this.requiresPassword = requiresPassword;
        if (options == null || options.length == 0) {
            this.options = Collections.emptyList();
        }
        else {
            this.options = Collections.unmodifiableList((List<? extends SASLOption>)Arrays.asList((T[])options));
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean acceptsPassword() {
        return this.acceptsPassword;
    }
    
    public boolean requiresPassword() {
        return this.requiresPassword;
    }
    
    public List<SASLOption> getOptions() {
        return this.options;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("SASLMechanismInfo(name='");
        buffer.append(this.name);
        buffer.append("', description='");
        buffer.append(this.description);
        buffer.append("', acceptsPassword=");
        buffer.append(this.acceptsPassword);
        buffer.append(", requiresPassword=");
        buffer.append(this.requiresPassword);
        buffer.append(", options={");
        final Iterator<SASLOption> iterator = this.options.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
