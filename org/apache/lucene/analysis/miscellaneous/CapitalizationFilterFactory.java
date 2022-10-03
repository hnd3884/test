package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Collection;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class CapitalizationFilterFactory extends TokenFilterFactory
{
    public static final String KEEP = "keep";
    public static final String KEEP_IGNORE_CASE = "keepIgnoreCase";
    public static final String OK_PREFIX = "okPrefix";
    public static final String MIN_WORD_LENGTH = "minWordLength";
    public static final String MAX_WORD_COUNT = "maxWordCount";
    public static final String MAX_TOKEN_LENGTH = "maxTokenLength";
    public static final String ONLY_FIRST_WORD = "onlyFirstWord";
    public static final String FORCE_FIRST_LETTER = "forceFirstLetter";
    CharArraySet keep;
    Collection<char[]> okPrefix;
    final int minWordLength;
    final int maxWordCount;
    final int maxTokenLength;
    final boolean onlyFirstWord;
    final boolean forceFirstLetter;
    
    public CapitalizationFilterFactory(final Map<String, String> args) {
        super(args);
        this.okPrefix = (Collection<char[]>)Collections.emptyList();
        final boolean ignoreCase = this.getBoolean(args, "keepIgnoreCase", false);
        Set<String> k = this.getSet(args, "keep");
        if (k != null) {
            (this.keep = new CharArraySet(10, ignoreCase)).addAll(k);
        }
        k = this.getSet(args, "okPrefix");
        if (k != null) {
            this.okPrefix = new ArrayList<char[]>();
            for (final String item : k) {
                this.okPrefix.add(item.toCharArray());
            }
        }
        this.minWordLength = this.getInt(args, "minWordLength", 0);
        this.maxWordCount = this.getInt(args, "maxWordCount", Integer.MAX_VALUE);
        this.maxTokenLength = this.getInt(args, "maxTokenLength", Integer.MAX_VALUE);
        this.onlyFirstWord = this.getBoolean(args, "onlyFirstWord", true);
        this.forceFirstLetter = this.getBoolean(args, "forceFirstLetter", true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public CapitalizationFilter create(final TokenStream input) {
        return new CapitalizationFilter(input, this.onlyFirstWord, this.keep, this.forceFirstLetter, this.okPrefix, this.minWordLength, this.maxWordCount, this.maxTokenLength);
    }
}
