package io.opencensus.tags;

import io.opencensus.internal.StringUtils;
import io.opencensus.internal.Utils;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class TagValue
{
    public static final int MAX_LENGTH = 255;
    
    TagValue() {
    }
    
    public static TagValue create(final String value) {
        Utils.checkArgument(isValid(value), "Invalid TagValue: %s", value);
        return new AutoValue_TagValue(value);
    }
    
    public abstract String asString();
    
    private static boolean isValid(final String value) {
        return value.length() <= 255 && StringUtils.isPrintableString(value);
    }
}
