package io.opencensus.trace;

import java.util.Collections;
import java.util.HashMap;
import io.opencensus.internal.Utils;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Annotation
{
    private static final Map<String, AttributeValue> EMPTY_ATTRIBUTES;
    
    public static Annotation fromDescription(final String description) {
        return new AutoValue_Annotation(description, Annotation.EMPTY_ATTRIBUTES);
    }
    
    public static Annotation fromDescriptionAndAttributes(final String description, final Map<String, AttributeValue> attributes) {
        return new AutoValue_Annotation(description, Collections.unmodifiableMap((Map<? extends String, ? extends AttributeValue>)new HashMap<String, AttributeValue>(Utils.checkNotNull(attributes, "attributes"))));
    }
    
    public abstract String getDescription();
    
    public abstract Map<String, AttributeValue> getAttributes();
    
    Annotation() {
    }
    
    static {
        EMPTY_ATTRIBUTES = Collections.unmodifiableMap(Collections.emptyMap());
    }
}
