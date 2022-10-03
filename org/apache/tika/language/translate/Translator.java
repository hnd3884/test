package org.apache.tika.language.translate;

import java.io.IOException;
import org.apache.tika.exception.TikaException;

public interface Translator
{
    String translate(final String p0, final String p1, final String p2) throws TikaException, IOException;
    
    String translate(final String p0, final String p1) throws TikaException, IOException;
    
    boolean isAvailable();
}
