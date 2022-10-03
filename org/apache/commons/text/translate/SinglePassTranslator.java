package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;

abstract class SinglePassTranslator extends CharSequenceTranslator
{
    @Override
    public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        if (index != 0) {
            throw new IllegalArgumentException(this.getClassName() + ".translate(final CharSequence input, final int index, final Writer out) can not handle a non-zero index.");
        }
        this.translateWhole(input, out);
        return Character.codePointCount(input, index, input.length());
    }
    
    private String getClassName() {
        final Class<? extends SinglePassTranslator> clazz = this.getClass();
        return clazz.isAnonymousClass() ? clazz.getName() : clazz.getSimpleName();
    }
    
    abstract void translateWhole(final CharSequence p0, final Writer p1) throws IOException;
}
