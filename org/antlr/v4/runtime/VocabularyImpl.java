package org.antlr.v4.runtime;

import java.util.Arrays;

public class VocabularyImpl implements Vocabulary
{
    private static final String[] EMPTY_NAMES;
    public static final VocabularyImpl EMPTY_VOCABULARY;
    private final String[] literalNames;
    private final String[] symbolicNames;
    private final String[] displayNames;
    private final int maxTokenType;
    
    public VocabularyImpl(final String[] literalNames, final String[] symbolicNames) {
        this(literalNames, symbolicNames, null);
    }
    
    public VocabularyImpl(final String[] literalNames, final String[] symbolicNames, final String[] displayNames) {
        this.literalNames = ((literalNames != null) ? literalNames : VocabularyImpl.EMPTY_NAMES);
        this.symbolicNames = ((symbolicNames != null) ? symbolicNames : VocabularyImpl.EMPTY_NAMES);
        this.displayNames = ((displayNames != null) ? displayNames : VocabularyImpl.EMPTY_NAMES);
        this.maxTokenType = Math.max(this.displayNames.length, Math.max(this.literalNames.length, this.symbolicNames.length)) - 1;
    }
    
    public static Vocabulary fromTokenNames(final String[] tokenNames) {
        if (tokenNames == null || tokenNames.length == 0) {
            return VocabularyImpl.EMPTY_VOCABULARY;
        }
        final String[] literalNames = Arrays.copyOf(tokenNames, tokenNames.length);
        final String[] symbolicNames = Arrays.copyOf(tokenNames, tokenNames.length);
        for (int i = 0; i < tokenNames.length; ++i) {
            final String tokenName = tokenNames[i];
            if (tokenName != null) {
                if (!tokenName.isEmpty()) {
                    final char firstChar = tokenName.charAt(0);
                    if (firstChar == '\'') {
                        symbolicNames[i] = null;
                        continue;
                    }
                    if (Character.isUpperCase(firstChar)) {
                        literalNames[i] = null;
                        continue;
                    }
                }
                symbolicNames[i] = (literalNames[i] = null);
            }
        }
        return new VocabularyImpl(literalNames, symbolicNames, tokenNames);
    }
    
    @Override
    public int getMaxTokenType() {
        return this.maxTokenType;
    }
    
    @Override
    public String getLiteralName(final int tokenType) {
        if (tokenType >= 0 && tokenType < this.literalNames.length) {
            return this.literalNames[tokenType];
        }
        return null;
    }
    
    @Override
    public String getSymbolicName(final int tokenType) {
        if (tokenType >= 0 && tokenType < this.symbolicNames.length) {
            return this.symbolicNames[tokenType];
        }
        if (tokenType == -1) {
            return "EOF";
        }
        return null;
    }
    
    @Override
    public String getDisplayName(final int tokenType) {
        if (tokenType >= 0 && tokenType < this.displayNames.length) {
            final String displayName = this.displayNames[tokenType];
            if (displayName != null) {
                return displayName;
            }
        }
        final String literalName = this.getLiteralName(tokenType);
        if (literalName != null) {
            return literalName;
        }
        final String symbolicName = this.getSymbolicName(tokenType);
        if (symbolicName != null) {
            return symbolicName;
        }
        return Integer.toString(tokenType);
    }
    
    static {
        EMPTY_NAMES = new String[0];
        EMPTY_VOCABULARY = new VocabularyImpl(VocabularyImpl.EMPTY_NAMES, VocabularyImpl.EMPTY_NAMES, VocabularyImpl.EMPTY_NAMES);
    }
}
