package io.opencensus.tags;

import io.opencensus.internal.StringUtils;
import io.opencensus.internal.Utils;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class TagKey
{
    public static final int MAX_LENGTH = 255;
    
    TagKey() {
    }
    
    public static TagKey create(final String name) {
        Utils.checkArgument(isValid(name), "Invalid TagKey name: %s", name);
        return new AutoValue_TagKey(name);
    }
    
    public abstract String getName();
    
    private static boolean isValid(final String name) {
        return !name.isEmpty() && name.length() <= 255 && StringUtils.isPrintableString(name);
    }
}
