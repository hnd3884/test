package org.apache.commons.text.translate;

import java.io.IOException;
import java.util.Iterator;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class AggregateTranslator extends CharSequenceTranslator
{
    private final List<CharSequenceTranslator> translators;
    
    public AggregateTranslator(final CharSequenceTranslator... translators) {
        this.translators = new ArrayList<CharSequenceTranslator>();
        if (translators != null) {
            for (final CharSequenceTranslator translator : translators) {
                if (translator != null) {
                    this.translators.add(translator);
                }
            }
        }
    }
    
    @Override
    public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        for (final CharSequenceTranslator translator : this.translators) {
            final int consumed = translator.translate(input, index, out);
            if (consumed != 0) {
                return consumed;
            }
        }
        return 0;
    }
}
