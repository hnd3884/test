package org.apache.lucene.analysis.miscellaneous;

import java.util.regex.Matcher;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class WordDelimiterFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    public static final String PROTECTED_TOKENS = "protected";
    public static final String TYPES = "types";
    private final String wordFiles;
    private final String types;
    private final int flags;
    byte[] typeTable;
    private CharArraySet protectedWords;
    private static Pattern typePattern;
    char[] out;
    
    public WordDelimiterFilterFactory(final Map<String, String> args) {
        super(args);
        this.typeTable = null;
        this.protectedWords = null;
        this.out = new char[256];
        int flags = 0;
        if (this.getInt(args, "generateWordParts", 1) != 0) {
            flags |= 0x1;
        }
        if (this.getInt(args, "generateNumberParts", 1) != 0) {
            flags |= 0x2;
        }
        if (this.getInt(args, "catenateWords", 0) != 0) {
            flags |= 0x4;
        }
        if (this.getInt(args, "catenateNumbers", 0) != 0) {
            flags |= 0x8;
        }
        if (this.getInt(args, "catenateAll", 0) != 0) {
            flags |= 0x10;
        }
        if (this.getInt(args, "splitOnCaseChange", 1) != 0) {
            flags |= 0x40;
        }
        if (this.getInt(args, "splitOnNumerics", 1) != 0) {
            flags |= 0x80;
        }
        if (this.getInt(args, "preserveOriginal", 0) != 0) {
            flags |= 0x20;
        }
        if (this.getInt(args, "stemEnglishPossessive", 1) != 0) {
            flags |= 0x100;
        }
        this.wordFiles = this.get(args, "protected");
        this.types = this.get(args, "types");
        this.flags = flags;
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        if (this.wordFiles != null) {
            this.protectedWords = this.getWordSet(loader, this.wordFiles, false);
        }
        if (this.types != null) {
            final List<String> files = this.splitFileNames(this.types);
            final List<String> wlist = new ArrayList<String>();
            for (final String file : files) {
                final List<String> lines = this.getLines(loader, file.trim());
                wlist.addAll(lines);
            }
            this.typeTable = this.parseTypes(wlist);
        }
    }
    
    public TokenFilter create(final TokenStream input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_8_0)) {
            return new WordDelimiterFilter(input, (this.typeTable == null) ? WordDelimiterIterator.DEFAULT_WORD_DELIM_TABLE : this.typeTable, this.flags, this.protectedWords);
        }
        return new Lucene47WordDelimiterFilter(input, (this.typeTable == null) ? WordDelimiterIterator.DEFAULT_WORD_DELIM_TABLE : this.typeTable, this.flags, this.protectedWords);
    }
    
    private byte[] parseTypes(final List<String> rules) {
        final SortedMap<Character, Byte> typeMap = new TreeMap<Character, Byte>();
        for (final String rule : rules) {
            final Matcher m = WordDelimiterFilterFactory.typePattern.matcher(rule);
            if (!m.find()) {
                throw new IllegalArgumentException("Invalid Mapping Rule : [" + rule + "]");
            }
            final String lhs = this.parseString(m.group(1).trim());
            final Byte rhs = this.parseType(m.group(2).trim());
            if (lhs.length() != 1) {
                throw new IllegalArgumentException("Invalid Mapping Rule : [" + rule + "]. Only a single character is allowed.");
            }
            if (rhs == null) {
                throw new IllegalArgumentException("Invalid Mapping Rule : [" + rule + "]. Illegal type.");
            }
            typeMap.put(lhs.charAt(0), rhs);
        }
        final byte[] types = new byte[Math.max((char)typeMap.lastKey() + '\u0001', WordDelimiterIterator.DEFAULT_WORD_DELIM_TABLE.length)];
        for (int i = 0; i < types.length; ++i) {
            types[i] = WordDelimiterIterator.getType(i);
        }
        for (final Map.Entry<Character, Byte> mapping : typeMap.entrySet()) {
            types[(char)mapping.getKey()] = mapping.getValue();
        }
        return types;
    }
    
    private Byte parseType(final String s) {
        if (s.equals("LOWER")) {
            return 1;
        }
        if (s.equals("UPPER")) {
            return 2;
        }
        if (s.equals("ALPHA")) {
            return 3;
        }
        if (s.equals("DIGIT")) {
            return 4;
        }
        if (s.equals("ALPHANUM")) {
            return 7;
        }
        if (s.equals("SUBWORD_DELIM")) {
            return 8;
        }
        return null;
    }
    
    private String parseString(final String s) {
        int readPos = 0;
        final int len = s.length();
        int writePos = 0;
        while (readPos < len) {
            char c = s.charAt(readPos++);
            if (c == '\\') {
                if (readPos >= len) {
                    throw new IllegalArgumentException("Invalid escaped char in [" + s + "]");
                }
                c = s.charAt(readPos++);
                switch (c) {
                    case '\\': {
                        c = '\\';
                        break;
                    }
                    case 'n': {
                        c = '\n';
                        break;
                    }
                    case 't': {
                        c = '\t';
                        break;
                    }
                    case 'r': {
                        c = '\r';
                        break;
                    }
                    case 'b': {
                        c = '\b';
                        break;
                    }
                    case 'f': {
                        c = '\f';
                        break;
                    }
                    case 'u': {
                        if (readPos + 3 >= len) {
                            throw new IllegalArgumentException("Invalid escaped char in [" + s + "]");
                        }
                        c = (char)Integer.parseInt(s.substring(readPos, readPos + 4), 16);
                        readPos += 4;
                        break;
                    }
                }
            }
            this.out[writePos++] = c;
        }
        return new String(this.out, 0, writePos);
    }
    
    static {
        WordDelimiterFilterFactory.typePattern = Pattern.compile("(.*)\\s*=>\\s*(.*)\\s*$");
    }
}
