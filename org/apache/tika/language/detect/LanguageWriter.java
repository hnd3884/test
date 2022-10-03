package org.apache.tika.language.detect;

import java.io.IOException;
import java.io.Writer;

public class LanguageWriter extends Writer
{
    private final LanguageDetector detector;
    
    public LanguageWriter(final LanguageDetector detector) {
        (this.detector = detector).reset();
    }
    
    public LanguageDetector getDetector() {
        return this.detector;
    }
    
    public LanguageResult getLanguage() {
        return this.detector.detect();
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) {
        this.detector.addText(cbuf, off, len);
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public void flush() {
    }
    
    public void reset() {
        this.detector.reset();
    }
}
