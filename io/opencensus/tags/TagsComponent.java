package io.opencensus.tags;

import io.opencensus.tags.propagation.TagPropagationComponent;

public abstract class TagsComponent
{
    public abstract Tagger getTagger();
    
    public abstract TagPropagationComponent getTagPropagationComponent();
    
    public abstract TaggingState getState();
    
    @Deprecated
    public abstract void setState(final TaggingState p0);
}
