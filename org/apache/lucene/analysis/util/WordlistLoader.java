package org.apache.lucene.analysis.util;

import java.util.ArrayList;
import java.util.List;
import java.nio.charset.Charset;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.io.Reader;

public class WordlistLoader
{
    private static final int INITIAL_CAPACITY = 16;
    
    private WordlistLoader() {
    }
    
    public static CharArraySet getWordSet(final Reader reader, final CharArraySet result) throws IOException {
        BufferedReader br = null;
        try {
            br = getBufferedReader(reader);
            String word = null;
            while ((word = br.readLine()) != null) {
                result.add(word.trim());
            }
        }
        finally {
            IOUtils.close(new Closeable[] { br });
        }
        return result;
    }
    
    public static CharArraySet getWordSet(final Reader reader) throws IOException {
        return getWordSet(reader, new CharArraySet(16, false));
    }
    
    public static CharArraySet getWordSet(final Reader reader, final String comment) throws IOException {
        return getWordSet(reader, comment, new CharArraySet(16, false));
    }
    
    public static CharArraySet getWordSet(final Reader reader, final String comment, final CharArraySet result) throws IOException {
        BufferedReader br = null;
        try {
            br = getBufferedReader(reader);
            String word = null;
            while ((word = br.readLine()) != null) {
                if (!word.startsWith(comment)) {
                    result.add(word.trim());
                }
            }
        }
        finally {
            IOUtils.close(new Closeable[] { br });
        }
        return result;
    }
    
    public static CharArraySet getSnowballWordSet(final Reader reader, final CharArraySet result) throws IOException {
        BufferedReader br = null;
        try {
            br = getBufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                final int comment = line.indexOf(124);
                if (comment >= 0) {
                    line = line.substring(0, comment);
                }
                final String[] words = line.split("\\s+");
                for (int i = 0; i < words.length; ++i) {
                    if (words[i].length() > 0) {
                        result.add(words[i]);
                    }
                }
            }
        }
        finally {
            IOUtils.close(new Closeable[] { br });
        }
        return result;
    }
    
    public static CharArraySet getSnowballWordSet(final Reader reader) throws IOException {
        return getSnowballWordSet(reader, new CharArraySet(16, false));
    }
    
    public static CharArrayMap<String> getStemDict(final Reader reader, final CharArrayMap<String> result) throws IOException {
        BufferedReader br = null;
        try {
            br = getBufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                final String[] wordstem = line.split("\t", 2);
                result.put(wordstem[0], wordstem[1]);
            }
        }
        finally {
            IOUtils.close(new Closeable[] { br });
        }
        return result;
    }
    
    public static List<String> getLines(final InputStream stream, final Charset charset) throws IOException {
        BufferedReader input = null;
        boolean success = false;
        try {
            input = getBufferedReader(IOUtils.getDecodingReader(stream, charset));
            final ArrayList<String> lines = new ArrayList<String>();
            String word = null;
            while ((word = input.readLine()) != null) {
                if (lines.isEmpty() && word.length() > 0 && word.charAt(0) == '\ufeff') {
                    word = word.substring(1);
                }
                if (word.startsWith("#")) {
                    continue;
                }
                word = word.trim();
                if (word.length() == 0) {
                    continue;
                }
                lines.add(word);
            }
            success = true;
            return lines;
        }
        finally {
            if (success) {
                IOUtils.close(new Closeable[] { input });
            }
            else {
                IOUtils.closeWhileHandlingException(new Closeable[] { input });
            }
        }
    }
    
    private static BufferedReader getBufferedReader(final Reader reader) {
        return (BufferedReader)((reader instanceof BufferedReader) ? reader : new BufferedReader(reader));
    }
}
