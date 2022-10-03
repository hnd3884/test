package org.glassfish.jersey.internal.util;

import java.util.regex.Pattern;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;

public final class Tokenizer
{
    public static final String COMMON_DELIMITERS = " ,;\n";
    
    private Tokenizer() {
    }
    
    public static String[] tokenize(final String[] entries) {
        return tokenize(entries, " ,;\n");
    }
    
    public static String[] tokenize(final String[] entries, final String delimiters) {
        final List<String> tokens = new LinkedList<String>();
        for (String entry : entries) {
            if (entry != null) {
                if (!entry.isEmpty()) {
                    entry = entry.trim();
                    if (!entry.isEmpty()) {
                        tokenize(entry, delimiters, tokens);
                    }
                }
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }
    
    public static String[] tokenize(final String entry) {
        return tokenize(entry, " ,;\n");
    }
    
    public static String[] tokenize(final String entry, final String delimiters) {
        final Collection<String> tokens = tokenize(entry, delimiters, new LinkedList<String>());
        return tokens.toArray(new String[tokens.size()]);
    }
    
    private static Collection<String> tokenize(final String entry, final String delimiters, final Collection<String> tokens) {
        final StringBuilder regexpBuilder = new StringBuilder(delimiters.length() * 3);
        regexpBuilder.append('[');
        for (final char c : delimiters.toCharArray()) {
            regexpBuilder.append(Pattern.quote(String.valueOf(c)));
        }
        regexpBuilder.append(']');
        final String[] split;
        final String[] tokenArray = split = entry.split(regexpBuilder.toString());
        for (String token : split) {
            if (token != null) {
                if (!token.isEmpty()) {
                    token = token.trim();
                    if (!token.isEmpty()) {
                        tokens.add(token);
                    }
                }
            }
        }
        return tokens;
    }
}
