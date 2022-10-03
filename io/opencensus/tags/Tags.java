package io.opencensus.tags;

import java.util.logging.Level;
import io.opencensus.internal.Provider;
import javax.annotation.Nullable;
import io.opencensus.tags.propagation.TagPropagationComponent;
import java.util.logging.Logger;

public final class Tags
{
    private static final Logger logger;
    private static final TagsComponent tagsComponent;
    
    private Tags() {
    }
    
    public static Tagger getTagger() {
        return Tags.tagsComponent.getTagger();
    }
    
    public static TagPropagationComponent getTagPropagationComponent() {
        return Tags.tagsComponent.getTagPropagationComponent();
    }
    
    public static TaggingState getState() {
        return Tags.tagsComponent.getState();
    }
    
    @Deprecated
    public static void setState(final TaggingState state) {
        Tags.tagsComponent.setState(state);
    }
    
    static TagsComponent loadTagsComponent(@Nullable final ClassLoader classLoader) {
        try {
            return Provider.createInstance(Class.forName("io.opencensus.impl.tags.TagsComponentImpl", true, classLoader), TagsComponent.class);
        }
        catch (final ClassNotFoundException e) {
            Tags.logger.log(Level.FINE, "Couldn't load full implementation for TagsComponent, now trying to load lite implementation.", e);
            try {
                return Provider.createInstance(Class.forName("io.opencensus.impllite.tags.TagsComponentImplLite", true, classLoader), TagsComponent.class);
            }
            catch (final ClassNotFoundException e) {
                Tags.logger.log(Level.FINE, "Couldn't load lite implementation for TagsComponent, now using default implementation for TagsComponent.", e);
                return NoopTags.newNoopTagsComponent();
            }
        }
    }
    
    static {
        logger = Logger.getLogger(Tags.class.getName());
        tagsComponent = loadTagsComponent(TagsComponent.class.getClassLoader());
    }
}
