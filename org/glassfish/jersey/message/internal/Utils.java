package org.glassfish.jersey.message.internal;

import java.io.IOException;
import java.io.File;

public final class Utils
{
    static void throwIllegalArgumentExceptionIfNull(final Object toCheck, final String errorMessage) {
        if (toCheck == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    public static File createTempFile() throws IOException {
        final File file = File.createTempFile("rep", "tmp");
        file.deleteOnExit();
        return file;
    }
    
    private Utils() {
        throw new AssertionError((Object)"No instances allowed.");
    }
}
