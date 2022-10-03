package org.apache.tika.language.detect;

import java.io.Writer;
import java.io.IOException;
import org.apache.tika.sax.WriteOutContentHandler;

public class LanguageHandler extends WriteOutContentHandler
{
    private final LanguageWriter writer;
    
    public LanguageHandler() throws IOException {
        this(new LanguageWriter(LanguageDetector.getDefaultLanguageDetector().loadModels()));
    }
    
    public LanguageHandler(final LanguageWriter writer) {
        super(writer);
        this.writer = writer;
    }
    
    public LanguageHandler(final LanguageDetector detector) {
        this(new LanguageWriter(detector));
    }
    
    public LanguageDetector getDetector() {
        return this.writer.getDetector();
    }
    
    public LanguageResult getLanguage() {
        return this.writer.getLanguage();
    }
}
