package io.opencensus.tags;

import io.opencensus.common.Scope;

public abstract class Tagger
{
    public abstract TagContext empty();
    
    public abstract TagContext getCurrentTagContext();
    
    public abstract TagContextBuilder emptyBuilder();
    
    public abstract TagContextBuilder toBuilder(final TagContext p0);
    
    public abstract TagContextBuilder currentBuilder();
    
    public abstract Scope withTagContext(final TagContext p0);
}
