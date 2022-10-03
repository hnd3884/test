package org.apache.lucene.search.suggest.document;

import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;
import java.util.Iterator;
import org.apache.lucene.analysis.TokenStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ContextSuggestField extends SuggestField
{
    public static final int CONTEXT_SEPARATOR = 29;
    static final byte TYPE = 1;
    private final Set<CharSequence> contexts;
    
    public ContextSuggestField(final String name, final String value, final int weight, final CharSequence... contexts) {
        super(name, value, weight);
        this.validate(value);
        this.contexts = new HashSet<CharSequence>((contexts != null) ? contexts.length : 0);
        if (contexts != null) {
            Collections.addAll(this.contexts, contexts);
        }
    }
    
    protected Iterable<CharSequence> contexts() {
        return this.contexts;
    }
    
    @Override
    protected CompletionTokenStream wrapTokenStream(final TokenStream stream) {
        final Iterable<CharSequence> contexts = this.contexts();
        for (final CharSequence context : contexts) {
            this.validate(context);
        }
        CompletionTokenStream completionTokenStream;
        if (stream instanceof CompletionTokenStream) {
            completionTokenStream = (CompletionTokenStream)stream;
            final PrefixTokenFilter prefixTokenFilter = new PrefixTokenFilter(completionTokenStream.inputTokenStream, '\u001d', contexts);
            completionTokenStream = new CompletionTokenStream((TokenStream)prefixTokenFilter, completionTokenStream.preserveSep, completionTokenStream.preservePositionIncrements, completionTokenStream.maxGraphExpansions);
        }
        else {
            completionTokenStream = new CompletionTokenStream((TokenStream)new PrefixTokenFilter(stream, '\u001d', contexts));
        }
        return completionTokenStream;
    }
    
    @Override
    protected byte type() {
        return 1;
    }
    
    private void validate(final CharSequence value) {
        for (int i = 0; i < value.length(); ++i) {
            if ('\u001d' == value.charAt(i)) {
                throw new IllegalArgumentException("Illegal value [" + (Object)value + "] UTF-16 codepoint [0x" + Integer.toHexString(value.charAt(i)) + "] at position " + i + " is a reserved character");
            }
        }
    }
    
    private static final class PrefixTokenFilter extends TokenFilter
    {
        private final char separator;
        private final CharTermAttribute termAttr;
        private final PositionIncrementAttribute posAttr;
        private final Iterable<CharSequence> prefixes;
        private Iterator<CharSequence> currentPrefix;
        
        public PrefixTokenFilter(final TokenStream input, final char separator, final Iterable<CharSequence> prefixes) {
            super(input);
            this.termAttr = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
            this.posAttr = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
            this.prefixes = prefixes;
            this.currentPrefix = null;
            this.separator = separator;
        }
        
        public boolean incrementToken() throws IOException {
            if (this.currentPrefix != null) {
                if (!this.currentPrefix.hasNext()) {
                    return this.input.incrementToken();
                }
                this.posAttr.setPositionIncrement(0);
            }
            else {
                this.currentPrefix = this.prefixes.iterator();
                this.termAttr.setEmpty();
                this.posAttr.setPositionIncrement(1);
            }
            this.termAttr.setEmpty();
            if (this.currentPrefix.hasNext()) {
                this.termAttr.append((CharSequence)this.currentPrefix.next());
            }
            this.termAttr.append(this.separator);
            return true;
        }
        
        public void reset() throws IOException {
            super.reset();
            this.currentPrefix = null;
        }
    }
}
