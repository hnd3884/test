package com.google.json;

import java.util.Arrays;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;

public final class EvalMinifier
{
    private static final String ENVELOPE_P1 = "(function(";
    private static final String ENVELOPE_P2 = "){return";
    private static final String ENVELOPE_P3 = "}(";
    private static final String ENVELOPE_P4 = "))";
    private static final int BOILERPLATE_COST;
    private static final int MARGINAL_VAR_COST;
    private static final int SAVINGS_THRESHOLD = 32;
    private static final String[][] RESERVED_KEYWORDS;
    
    public static String minify(final String jsonish) {
        final JsonSanitizer s = new JsonSanitizer(jsonish);
        s.sanitize();
        return minify(s.toCharSequence()).toString();
    }
    
    public static String minify(final String jsonish, final int maximumNestingDepth) {
        final JsonSanitizer s = new JsonSanitizer(jsonish, maximumNestingDepth);
        s.sanitize();
        return minify(s.toCharSequence()).toString();
    }
    
    private static CharSequence minify(final CharSequence json) {
        final Map<Token, Token> pool = new HashMap<Token, Token>();
        final int n = json.length();
        for (int i = 0; i < n; ++i) {
            final char ch = json.charAt(i);
            int tokEnd;
            if (ch == '\"') {
                for (tokEnd = i + 1; tokEnd < n; ++tokEnd) {
                    final char tch = json.charAt(tokEnd);
                    if (tch == '\\') {
                        ++tokEnd;
                    }
                    else if (tch == '\"') {
                        ++tokEnd;
                        break;
                    }
                }
            }
            else {
                if (!isLetterOrNumberChar(ch)) {
                    continue;
                }
                for (tokEnd = i + 1; tokEnd < n && isLetterOrNumberChar(json.charAt(tokEnd)); ++tokEnd) {}
            }
            int nextNonWhitespace;
            for (nextNonWhitespace = tokEnd; nextNonWhitespace < n; ++nextNonWhitespace) {
                final char wch = json.charAt(nextNonWhitespace);
                if (wch != '\t' && wch != '\n' && wch != '\r' && wch != ' ') {
                    break;
                }
            }
            if (nextNonWhitespace == n || (':' != json.charAt(nextNonWhitespace) && tokEnd - i >= 4)) {
                final Token tok = new Token(i, tokEnd, json);
                final Token last = pool.put(tok, tok);
                if (last != null) {
                    tok.prev = last;
                }
            }
            i = nextNonWhitespace - 1;
        }
        int potentialSavings = 0;
        final List<Token> dupes = new ArrayList<Token>();
        final Iterator<Token> values = pool.values().iterator();
        while (values.hasNext()) {
            final Token tok2 = values.next();
            if (tok2.prev == null) {
                values.remove();
            }
            else {
                int chainDepth = 0;
                for (Token t = tok2; t != null; t = t.prev) {
                    ++chainDepth;
                }
                final int tokSavings = (chainDepth - 1) * (tok2.end - tok2.start) - EvalMinifier.MARGINAL_VAR_COST;
                if (tokSavings <= 0) {
                    continue;
                }
                potentialSavings += tokSavings;
                for (Token t2 = tok2; t2 != null; t2 = t2.prev) {
                    dupes.add(t2);
                }
            }
        }
        if (potentialSavings <= EvalMinifier.BOILERPLATE_COST + 32) {
            return json;
        }
        Collections.sort(dupes);
        final int nTokens = dupes.size();
        final StringBuilder sb = new StringBuilder(n);
        sb.append("(function(");
        final NameGenerator nameGenerator = new NameGenerator();
        boolean first = true;
        for (final Token tok3 : pool.values()) {
            final String name = nameGenerator.next();
            for (Token t3 = tok3; t3 != null; t3 = t3.prev) {
                t3.name = name;
            }
            if (first) {
                first = false;
            }
            else {
                sb.append(',');
            }
            sb.append(name);
        }
        sb.append("){return");
        final int afterReturn = sb.length();
        int pos = 0;
        int tokIndex = 0;
        while (true) {
            final Token tok3 = (tokIndex < nTokens) ? dupes.get(tokIndex++) : null;
            final int limit = (tok3 != null) ? tok3.start : n;
            boolean inString = false;
            for (int j = pos; j < limit; ++j) {
                final char ch2 = json.charAt(j);
                if (inString) {
                    if (ch2 == '\"') {
                        inString = false;
                    }
                    else if (ch2 == '\\') {
                        ++j;
                    }
                }
                else if (ch2 == '\t' || ch2 == '\n' || ch2 == '\r' || ch2 == ' ') {
                    if (pos != j) {
                        sb.append(json, pos, j);
                    }
                    pos = j + 1;
                }
                else if (ch2 == '\"') {
                    inString = true;
                }
            }
            assert !inString;
            if (pos != limit) {
                sb.append(json, pos, limit);
            }
            if (tok3 == null) {
                final char ch3 = sb.charAt(afterReturn);
                if (ch3 != '{' && ch3 != '[' && ch3 != '\"') {
                    sb.insert(afterReturn, ' ');
                }
                sb.append("}(");
                boolean first2 = true;
                for (final Token tok4 : pool.values()) {
                    if (first2) {
                        first2 = false;
                    }
                    else {
                        sb.append(',');
                    }
                    sb.append(tok4.seq, tok4.start, tok4.end);
                }
                sb.append("))");
                return sb;
            }
            sb.append(tok3.name);
            pos = tok3.end;
        }
    }
    
    private static boolean isLetterOrNumberChar(final char ch) {
        if ('0' <= ch && ch <= '9') {
            return true;
        }
        final char lch = (char)(ch | ' ');
        return ('a' <= lch && lch <= 'z') || ch == '_' || ch == '$' || ch == '-' || ch == '.';
    }
    
    static boolean regionMatches(final CharSequence a, final int as, final int ae, final CharSequence b, final int bs, final int be) {
        final int n = ae - as;
        if (be - bs != n) {
            return false;
        }
        for (int ai = as, bi = bs; ai < ae; ++ai, ++bi) {
            if (a.charAt(ai) != b.charAt(bi)) {
                return false;
            }
        }
        return true;
    }
    
    static int nextIdentChar(final char ch, final boolean allowDigits) {
        if (ch == 'z') {
            return 65;
        }
        if (ch == 'Z') {
            return 95;
        }
        if (ch == '_') {
            return 36;
        }
        if (ch == '$') {
            if (allowDigits) {
                return 48;
            }
            return -1;
        }
        else {
            if (ch == '9') {
                return -1;
            }
            return (char)(ch + '\u0001');
        }
    }
    
    static {
        BOILERPLATE_COST = "(function(){return}())".length();
        MARGINAL_VAR_COST = ",,".length();
        RESERVED_KEYWORDS = new String[][] { new String[0], new String[0], { "as", "do", "if", "in", "of" }, { "for", "get", "let", "new", "set", "try", "var" }, { "case", "else", "enum", "eval", "from", "null", "this", "true", "void", "with" }, { "async", "await", "break", "catch", "class", "const", "false", "super", "throw", "while", "yield" }, { "delete", "export", "import", "public", "return", "switch", "static", "target", "typeof" }, { "default", "extends", "finally", "package", "private" }, { "continue", "debugger", "function" }, { "arguments", "interface", "protected" }, { "implements", "instanceof" } };
    }
    
    private static final class Token implements Comparable<Token>
    {
        private final int start;
        private final int end;
        private final int hashCode;
        @Nonnull
        private final CharSequence seq;
        @Nullable
        Token prev;
        @Nullable
        String name;
        
        Token(final int start, final int end, final CharSequence seq) {
            this.start = start;
            this.end = end;
            this.seq = seq;
            int hc = 0;
            for (int i = start; i < end; ++i) {
                final char ch = seq.charAt(i);
                hc = hc * 31 + ch;
            }
            this.hashCode = hc;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            if (!(o instanceof Token)) {
                return false;
            }
            final Token that = (Token)o;
            return this.hashCode == that.hashCode && EvalMinifier.regionMatches(this.seq, this.start, this.end, that.seq, that.start, that.end);
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        @Override
        public int compareTo(final Token t) {
            return this.start - t.start;
        }
    }
    
    static final class NameGenerator
    {
        private final StringBuilder sb;
        
        NameGenerator() {
            this.sb = new StringBuilder("a");
        }
        
        public String next() {
            String name;
            while (true) {
                name = this.sb.toString();
                int i;
                final int sbLen = i = this.sb.length();
                while (--i >= 0) {
                    final int next = EvalMinifier.nextIdentChar(this.sb.charAt(i), i != 0);
                    if (next >= 0) {
                        this.sb.setCharAt(i, (char)next);
                        break;
                    }
                    this.sb.setCharAt(i, 'a');
                    if (i != 0) {
                        continue;
                    }
                    this.sb.append('a');
                }
                final int nameLen = name.length();
                if (nameLen >= EvalMinifier.RESERVED_KEYWORDS.length || Arrays.binarySearch(EvalMinifier.RESERVED_KEYWORDS[nameLen], name) < 0) {
                    break;
                }
            }
            return name;
        }
    }
}
