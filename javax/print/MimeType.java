package javax.print;

import java.util.Set;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map;
import java.io.Serializable;

class MimeType implements Serializable, Cloneable
{
    private static final long serialVersionUID = -2785720609362367683L;
    private String[] myPieces;
    private transient String myStringValue;
    private transient ParameterMapEntrySet myEntrySet;
    private transient ParameterMap myParameterMap;
    private static final int TOKEN_LEXEME = 0;
    private static final int QUOTED_STRING_LEXEME = 1;
    private static final int TSPECIAL_LEXEME = 2;
    private static final int EOF_LEXEME = 3;
    private static final int ILLEGAL_LEXEME = 4;
    
    public MimeType(final String s) {
        this.myStringValue = null;
        this.myEntrySet = null;
        this.myParameterMap = null;
        this.parse(s);
    }
    
    public String getMimeType() {
        return this.getStringValue();
    }
    
    public String getMediaType() {
        return this.myPieces[0];
    }
    
    public String getMediaSubtype() {
        return this.myPieces[1];
    }
    
    public Map getParameterMap() {
        if (this.myParameterMap == null) {
            this.myParameterMap = new ParameterMap();
        }
        return this.myParameterMap;
    }
    
    @Override
    public String toString() {
        return this.getStringValue();
    }
    
    @Override
    public int hashCode() {
        return this.getStringValue().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof MimeType && this.getStringValue().equals(((MimeType)o).getStringValue());
    }
    
    private String getStringValue() {
        if (this.myStringValue == null) {
            final StringBuffer sb = new StringBuffer();
            sb.append(this.myPieces[0]);
            sb.append('/');
            sb.append(this.myPieces[1]);
            for (int length = this.myPieces.length, i = 2; i < length; i += 2) {
                sb.append(';');
                sb.append(' ');
                sb.append(this.myPieces[i]);
                sb.append('=');
                sb.append(addQuotes(this.myPieces[i + 1]));
            }
            this.myStringValue = sb.toString();
        }
        return this.myStringValue;
    }
    
    private static String toUnicodeLowerCase(final String s) {
        final int length = s.length();
        final char[] array = new char[length];
        for (int i = 0; i < length; ++i) {
            array[i] = Character.toLowerCase(s.charAt(i));
        }
        return new String(array);
    }
    
    private static String removeBackslashes(final String s) {
        final int length = s.length();
        final char[] array = new char[length];
        int n = 0;
        for (int i = 0; i < length; ++i) {
            char c = s.charAt(i);
            if (c == '\\') {
                c = s.charAt(++i);
            }
            array[n++] = c;
        }
        return new String(array, 0, n);
    }
    
    private static String addQuotes(final String s) {
        final int length = s.length();
        final StringBuffer sb = new StringBuffer(length + 2);
        sb.append('\"');
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '\"') {
                sb.append('\\');
            }
            sb.append(char1);
        }
        sb.append('\"');
        return sb.toString();
    }
    
    private void parse(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(s);
        final Vector vector = new Vector();
        if (lexicalAnalyzer.getLexemeType() != 0) {
            throw new IllegalArgumentException();
        }
        final String unicodeLowerCase = toUnicodeLowerCase(lexicalAnalyzer.getLexeme());
        vector.add(unicodeLowerCase);
        lexicalAnalyzer.nextLexeme();
        final boolean equals = unicodeLowerCase.equals("text");
        if (lexicalAnalyzer.getLexemeType() != 2 || lexicalAnalyzer.getLexemeFirstCharacter() != '/') {
            throw new IllegalArgumentException();
        }
        lexicalAnalyzer.nextLexeme();
        if (lexicalAnalyzer.getLexemeType() != 0) {
            throw new IllegalArgumentException();
        }
        vector.add(toUnicodeLowerCase(lexicalAnalyzer.getLexeme()));
        lexicalAnalyzer.nextLexeme();
        while (lexicalAnalyzer.getLexemeType() == 2 && lexicalAnalyzer.getLexemeFirstCharacter() == ';') {
            lexicalAnalyzer.nextLexeme();
            if (lexicalAnalyzer.getLexemeType() != 0) {
                throw new IllegalArgumentException();
            }
            final String unicodeLowerCase2 = toUnicodeLowerCase(lexicalAnalyzer.getLexeme());
            vector.add(unicodeLowerCase2);
            lexicalAnalyzer.nextLexeme();
            final boolean equals2 = unicodeLowerCase2.equals("charset");
            if (lexicalAnalyzer.getLexemeType() != 2 || lexicalAnalyzer.getLexemeFirstCharacter() != '=') {
                throw new IllegalArgumentException();
            }
            lexicalAnalyzer.nextLexeme();
            if (lexicalAnalyzer.getLexemeType() == 0) {
                final String lexeme = lexicalAnalyzer.getLexeme();
                vector.add((equals && equals2) ? toUnicodeLowerCase(lexeme) : lexeme);
                lexicalAnalyzer.nextLexeme();
            }
            else {
                if (lexicalAnalyzer.getLexemeType() != 1) {
                    throw new IllegalArgumentException();
                }
                final String removeBackslashes = removeBackslashes(lexicalAnalyzer.getLexeme());
                vector.add((equals && equals2) ? toUnicodeLowerCase(removeBackslashes) : removeBackslashes);
                lexicalAnalyzer.nextLexeme();
            }
        }
        if (lexicalAnalyzer.getLexemeType() != 3) {
            throw new IllegalArgumentException();
        }
        final int size = vector.size();
        this.myPieces = vector.toArray(new String[size]);
        for (int i = 4; i < size; i += 2) {
            int j;
            for (j = 2; j < i && this.myPieces[j].compareTo(this.myPieces[i]) <= 0; j += 2) {}
            while (j < i) {
                final String s2 = this.myPieces[j];
                this.myPieces[j] = this.myPieces[i];
                this.myPieces[i] = s2;
                final String s3 = this.myPieces[j + 1];
                this.myPieces[j + 1] = this.myPieces[i + 1];
                this.myPieces[i + 1] = s3;
                j += 2;
            }
        }
    }
    
    private class ParameterMapEntry implements Map.Entry
    {
        private int myIndex;
        
        public ParameterMapEntry(final int myIndex) {
            this.myIndex = myIndex;
        }
        
        @Override
        public Object getKey() {
            return MimeType.this.myPieces[this.myIndex];
        }
        
        @Override
        public Object getValue() {
            return MimeType.this.myPieces[this.myIndex + 1];
        }
        
        @Override
        public Object setValue(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o instanceof Map.Entry && this.getKey().equals(((Map.Entry)o).getKey()) && this.getValue().equals(((Map.Entry)o).getValue());
        }
        
        @Override
        public int hashCode() {
            return this.getKey().hashCode() ^ this.getValue().hashCode();
        }
    }
    
    private class ParameterMapEntrySetIterator implements Iterator
    {
        private int myIndex;
        
        private ParameterMapEntrySetIterator() {
            this.myIndex = 2;
        }
        
        @Override
        public boolean hasNext() {
            return this.myIndex < MimeType.this.myPieces.length;
        }
        
        @Override
        public Object next() {
            if (this.hasNext()) {
                final ParameterMapEntry parameterMapEntry = new ParameterMapEntry(this.myIndex);
                this.myIndex += 2;
                return parameterMapEntry;
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private class ParameterMapEntrySet extends AbstractSet
    {
        @Override
        public Iterator iterator() {
            return new ParameterMapEntrySetIterator();
        }
        
        @Override
        public int size() {
            return (MimeType.this.myPieces.length - 2) / 2;
        }
    }
    
    private class ParameterMap extends AbstractMap
    {
        @Override
        public Set entrySet() {
            if (MimeType.this.myEntrySet == null) {
                MimeType.this.myEntrySet = new ParameterMapEntrySet();
            }
            return MimeType.this.myEntrySet;
        }
    }
    
    private static class LexicalAnalyzer
    {
        protected String mySource;
        protected int mySourceLength;
        protected int myCurrentIndex;
        protected int myLexemeType;
        protected int myLexemeBeginIndex;
        protected int myLexemeEndIndex;
        
        public LexicalAnalyzer(final String mySource) {
            this.mySource = mySource;
            this.mySourceLength = mySource.length();
            this.myCurrentIndex = 0;
            this.nextLexeme();
        }
        
        public int getLexemeType() {
            return this.myLexemeType;
        }
        
        public String getLexeme() {
            return (this.myLexemeBeginIndex >= this.mySourceLength) ? null : this.mySource.substring(this.myLexemeBeginIndex, this.myLexemeEndIndex);
        }
        
        public char getLexemeFirstCharacter() {
            return (this.myLexemeBeginIndex >= this.mySourceLength) ? '\0' : this.mySource.charAt(this.myLexemeBeginIndex);
        }
        
        public void nextLexeme() {
            int i = 0;
            int n = 0;
            while (i >= 0) {
                switch (i) {
                    case 0: {
                        if (this.myCurrentIndex >= this.mySourceLength) {
                            this.myLexemeType = 3;
                            this.myLexemeBeginIndex = this.mySourceLength;
                            this.myLexemeEndIndex = this.mySourceLength;
                            i = -1;
                            continue;
                        }
                        final char char1;
                        if (Character.isWhitespace(char1 = this.mySource.charAt(this.myCurrentIndex++))) {
                            i = 0;
                            continue;
                        }
                        if (char1 == '\"') {
                            this.myLexemeType = 1;
                            this.myLexemeBeginIndex = this.myCurrentIndex;
                            i = 1;
                            continue;
                        }
                        if (char1 == '(') {
                            ++n;
                            i = 3;
                            continue;
                        }
                        if (char1 == '/' || char1 == ';' || char1 == '=' || char1 == ')' || char1 == '<' || char1 == '>' || char1 == '@' || char1 == ',' || char1 == ':' || char1 == '\\' || char1 == '[' || char1 == ']' || char1 == '?') {
                            this.myLexemeType = 2;
                            this.myLexemeBeginIndex = this.myCurrentIndex - 1;
                            this.myLexemeEndIndex = this.myCurrentIndex;
                            i = -1;
                            continue;
                        }
                        this.myLexemeType = 0;
                        this.myLexemeBeginIndex = this.myCurrentIndex - 1;
                        i = 5;
                        continue;
                    }
                    case 1: {
                        if (this.myCurrentIndex >= this.mySourceLength) {
                            this.myLexemeType = 4;
                            this.myLexemeBeginIndex = this.mySourceLength;
                            this.myLexemeEndIndex = this.mySourceLength;
                            i = -1;
                            continue;
                        }
                        final char char2;
                        if ((char2 = this.mySource.charAt(this.myCurrentIndex++)) == '\"') {
                            this.myLexemeEndIndex = this.myCurrentIndex - 1;
                            i = -1;
                            continue;
                        }
                        if (char2 == '\\') {
                            i = 2;
                            continue;
                        }
                        i = 1;
                        continue;
                    }
                    case 2: {
                        if (this.myCurrentIndex >= this.mySourceLength) {
                            this.myLexemeType = 4;
                            this.myLexemeBeginIndex = this.mySourceLength;
                            this.myLexemeEndIndex = this.mySourceLength;
                            i = -1;
                            continue;
                        }
                        ++this.myCurrentIndex;
                        i = 1;
                        continue;
                    }
                    case 3: {
                        if (this.myCurrentIndex >= this.mySourceLength) {
                            this.myLexemeType = 4;
                            this.myLexemeBeginIndex = this.mySourceLength;
                            this.myLexemeEndIndex = this.mySourceLength;
                            i = -1;
                            continue;
                        }
                        final char char3;
                        if ((char3 = this.mySource.charAt(this.myCurrentIndex++)) == '(') {
                            ++n;
                            i = 3;
                            continue;
                        }
                        if (char3 == ')') {
                            i = ((--n == 0) ? 0 : 3);
                            continue;
                        }
                        if (char3 == '\\') {
                            i = 4;
                            continue;
                        }
                        i = 3;
                        continue;
                    }
                    case 4: {
                        if (this.myCurrentIndex >= this.mySourceLength) {
                            this.myLexemeType = 4;
                            this.myLexemeBeginIndex = this.mySourceLength;
                            this.myLexemeEndIndex = this.mySourceLength;
                            i = -1;
                            continue;
                        }
                        ++this.myCurrentIndex;
                        i = 3;
                        continue;
                    }
                    case 5: {
                        if (this.myCurrentIndex >= this.mySourceLength) {
                            this.myLexemeEndIndex = this.myCurrentIndex;
                            i = -1;
                            continue;
                        }
                        final char char4;
                        if (Character.isWhitespace(char4 = this.mySource.charAt(this.myCurrentIndex++))) {
                            this.myLexemeEndIndex = this.myCurrentIndex - 1;
                            i = -1;
                            continue;
                        }
                        if (char4 == '\"' || char4 == '(' || char4 == '/' || char4 == ';' || char4 == '=' || char4 == ')' || char4 == '<' || char4 == '>' || char4 == '@' || char4 == ',' || char4 == ':' || char4 == '\\' || char4 == '[' || char4 == ']' || char4 == '?') {
                            --this.myCurrentIndex;
                            this.myLexemeEndIndex = this.myCurrentIndex;
                            i = -1;
                            continue;
                        }
                        i = 5;
                        continue;
                    }
                }
            }
        }
    }
}
